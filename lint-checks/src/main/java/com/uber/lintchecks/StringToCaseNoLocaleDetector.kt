/*
 * Copyright (C) 2019. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.lintchecks

import com.android.tools.lint.client.api.JavaEvaluator
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiMethod
import com.uber.lintchecks.android.createImplementation
import org.jetbrains.uast.UCallExpression

/**
 * Detector that checks for usage of String.toUpperCase without Locale
 */
class StringToCaseNoLocaleDetector : Detector(), SourceCodeScanner {
  companion object {
    private const val ISSUE_ID = "StringToCaseNoLocale"
    private const val BRIEF_DESCRIPTION = "Don't use toLowerCase/toUpperCase without a locale"
    const val LINT_ERROR_MESSAGE = "Calling String#toLowerCase() or #toUpperCase() without specifying an\n" +
        "explicit locale is a common source of bugs. The reason for that is that\n" +
        "those methods will use the current locale on the user's device, and even\n" +
        "though the code appears to work correctly when you are developing the app,\n" +
        "it will fail in some locales. For example, in the Turkish locale, the\n" +
        "uppercase replacement for i is not I.\n" +
        "If you want the methods to just perform ASCII replacement, for example to\n" +
        "convert an enum name, call String#toUpperCase(Locale.US) instead. If you\n" +
        "really want to use the current locale, call\n" +
        "String#toUpperCase(Locale.getDefault()) instead."

    val ISSUE = Issue.create(
        id = ISSUE_ID,
        briefDescription = BRIEF_DESCRIPTION,
        explanation = LINT_ERROR_MESSAGE,
        category = Category.CORRECTNESS,
        priority = 6,
        severity = Severity.ERROR,
        implementation = createImplementation<StringToCaseNoLocaleDetector>())
  }

  override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
    if (!getApplicableMethodNames().contains(node.methodName)) return

    if (isStringType(context.evaluator, node) && node.valueArgumentCount == 0) {
      context.report(ISSUE, context.getLocation(node), BRIEF_DESCRIPTION)
    }
  }

  private fun isStringType(evaluator: JavaEvaluator, node: UCallExpression): Boolean {
    return evaluator.isMemberInClass(node.resolve(), "java.lang.String") ||
        node.receiverType?.canonicalText == "java.lang.String"
  }

  override fun getApplicableMethodNames(): List<String> = listOf("toUpperCase", "toLowerCase")
}

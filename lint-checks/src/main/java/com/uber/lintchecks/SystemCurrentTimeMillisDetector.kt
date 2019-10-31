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
 * Prevent usage of System.currentTimeMillis.
 */
class SystemCurrentTimeMillisDetector : Detector(), SourceCodeScanner {

  companion object {
    private const val ISSUE_ID = "SystemCurrentTimeMillis"
    private const val BRIEF_DESCRIPTION = "Don't use System.currentTimeMillis"
    const val LINT_ERROR_MESSAGE = """Don't use System.currentTimeMillis.
      Please prefer an abstraction like Clock, as this is easily mocked, injected, and tested."""
    val ISSUE = Issue.create(
        id = ISSUE_ID,
        briefDescription = BRIEF_DESCRIPTION,
        explanation = LINT_ERROR_MESSAGE,
        category = Category.CORRECTNESS,
        priority = 6,
        severity = Severity.ERROR,
        implementation = createImplementation<SystemCurrentTimeMillisDetector>())
  }

  override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
    if (!getApplicableMethodNames().contains(node.methodName)) return

    val evaluator = context.evaluator
    if (node.methodName == "currentTimeMillis" && isSystemType(evaluator, node)) {
        context.report(ISSUE, context.getLocation(node), LINT_ERROR_MESSAGE)
    }
  }

  private fun isSystemType(evaluator: JavaEvaluator, node: UCallExpression): Boolean {
    return evaluator.isMemberInClass(node.resolve(), "java.lang.System")
  }

  override fun getApplicableMethodNames(): List<String> = listOf("currentTimeMillis")
}

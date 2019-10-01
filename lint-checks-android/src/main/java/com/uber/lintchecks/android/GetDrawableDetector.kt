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
package com.uber.lintchecks.android

import com.android.tools.lint.client.api.JavaEvaluator
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Detector to check for usages of `ResourcesCompat.getDrawable` or `ContextCompat.getDrawable`.
 */
class GetDrawableDetector : Detector(), SourceCodeScanner {
  companion object {
    private const val ISSUE_ID = "GetDrawable"
    const val LINT_ERROR_MESSAGE = "Don't use ContextCompat#getDrawable(Context,int), instead use " +
        "AppCompatResources#getDrawable(Context, int) since it understands how to process vector drawables."
    @JvmField
    val ISSUE = Issue.create(
        id = ISSUE_ID,
        briefDescription = "Use AppCompatResources#getDrawable(Context, int)",
        explanation = LINT_ERROR_MESSAGE,
        category = Category.CORRECTNESS,
        priority = 6,
        severity = Severity.ERROR,
        implementation = createImplementation<GetDrawableDetector>())
  }

  override fun visitMethodCall(
    context: JavaContext,
    node: UCallExpression,
    method: PsiMethod
  ) {
    if (!getApplicableMethodNames().contains(node.methodName)) return

    if (node.methodName == "getDrawable" && isBlacklisted(context.evaluator, node)) {
      context.report(ISSUE, context.getLocation(node), LINT_ERROR_MESSAGE)
    }
  }

  private fun isBlacklisted(evaluator: JavaEvaluator, node: UCallExpression): Boolean {
    return evaluator.isMemberInClass(node.resolve(), "androidx.core.content.ContextCompat") ||
        evaluator.isMemberInClass(node.resolve(), "androidx.core.content.res.ResourcesCompat")
  }

  override fun getApplicableMethodNames(): List<String> = listOf("getDrawable")
}

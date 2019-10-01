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
package com.uber.lintchecks.rxjava

import com.android.tools.lint.client.api.JavaEvaluator
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiMethod
import com.uber.lintchecks.android.createImplementation
import org.jetbrains.uast.UCallExpression

/**
 * A [Detector] that detects usages of RxJava distinct()
 */
class RxJavaDistinctDetector : Detector(), SourceCodeScanner {

  companion object {
    private const val ISSUE_ID = "RxJavaDistinct"
    const val LINT_ERROR_MESSAGE = "distinct() works by holding all previous values in memory and " +
        "only can be used with a bounded observable. In most cases, distinctUntilChanged() works since " +
        "it only compares against the last emitted item instead of all items."

    val ISSUE = Issue.create(
        id = ISSUE_ID,
        briefDescription = "Distinct leads to wasting memory, use distinctUntilChanged instead since " +
            "that covers most common use-cases",
        explanation = LINT_ERROR_MESSAGE,
        category = Category.CORRECTNESS,
        priority = 6,
        severity = Severity.WARNING,
        implementation = createImplementation<RxJavaDistinctDetector>())

    private val RX_JAVA_TYPES = setOf("io.reactivex.Observable",
        "io.reactivex.Flowable")
  }

  override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
    if (node.methodName == "distinct" && isMethodCallOnReactiveTypes(context.evaluator, node)) {
      val quickFix = LintFix.create()
          .replace()
          .pattern("distinct")
          .with("distinctUntilChanged")
          .build()
      context.report(ISSUE, context.getLocation(node), LINT_ERROR_MESSAGE, quickFix)
    }
  }

  private fun isMethodCallOnReactiveTypes(evaluator: JavaEvaluator, node: UCallExpression): Boolean {
    return RX_JAVA_TYPES.any { evaluator.isMemberInClass(node.resolve(), it) }
  }

  override fun getApplicableMethodNames(): List<String> = listOf("distinct")
}

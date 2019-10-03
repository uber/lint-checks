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
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiMethod
import com.uber.lintchecks.android.createImplementation
import org.jetbrains.uast.UCallExpression

/**
 * Detects unsafe conversions of RxJava types to Single
 */
class RxJavaToSingleDetector : Detector(), SourceCodeScanner {

  companion object {
    private const val ISSUE_ID = "RxJavaToSingle"
    const val BRIEF_DESCRIPTION = "Unsafe conversion to Single. Use first(), firstElement(), " +
        "firstOrError() or toSingle(defaultValue) instead."
    const val LINT_ERROR_MESSAGE = "single(), singleOrError() and singleElement() will emit an error " +
        "if there is more than 1 element in the stream. On top of that, single() will not actually " +
        "emit the element until the stream is completed. This issue is usually mitigated by adding " +
        "take(1), however this is error prone as developers might forget to do that and the code " +
        "will still compile and possibly even run without issues for some time. This issue could be " +
        "easily avoided by using first(), firstOrError() or firstElement() instead.\n" +
        "\n" + "Maybe.toSingle() is disregarding the core idea of Maybe - that the stream can " +
        "complete without emitting any values. Calling toSingle() on such stream would emit an error."

    val ISSUE = Issue.create(
        id = ISSUE_ID,
        briefDescription = BRIEF_DESCRIPTION,
        explanation = LINT_ERROR_MESSAGE,
        category = Category.CORRECTNESS,
        priority = 6,
        severity = Severity.WARNING,
        implementation = createImplementation<RxJavaToSingleDetector>())

    val REACTIVE_TYPES = setOf("io.reactivex.Observable",
        "io.reactivex.Flowable",
        "io.reactivex.Maybe")
  }

  override fun getApplicableMethodNames(): List<String> = listOf("single",
      "singleOrError",
      "singleElement",
      "toSingle")

  override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
    if (getApplicableMethodNames().contains(node.methodName) &&
        isOnReactiveTypes(context.evaluator, node)) {
      context.report(ISSUE, context.getLocation(node), BRIEF_DESCRIPTION)
    }
  }

  private fun isOnReactiveTypes(evaluator: JavaEvaluator, node: UCallExpression): Boolean {
    return REACTIVE_TYPES.any { evaluator.isMemberInClass(node.resolve(), it) }
  }
}

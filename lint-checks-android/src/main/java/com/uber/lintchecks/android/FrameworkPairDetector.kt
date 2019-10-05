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

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UImportStatement

/**
 * Detector that checks for usages of Android platform Pair and recommends usage of androidx
 */
class FrameworkPairDetector : Detector(), SourceCodeScanner {
  companion object {
    const val ISSUE_ID = "FrameworkPair"
    val LINT_ERROR_MESSAGE = """
                The framework Pair class implementation has bugs on older versions of Android.
                You should use the support version instead (androidx.core.util.Pair).
                """.trimIndent().replace('\n', ' ')
    @JvmField
    val ISSUE = Issue.create(
        id = ISSUE_ID,
        briefDescription = "Don't use android.util.Pair. Instead prefer AndroidX Pair",
        explanation = LINT_ERROR_MESSAGE,
        category = Category.CORRECTNESS,
        priority = 6,
        severity = Severity.WARNING,
        implementation = createImplementation<FrameworkPairDetector>(),
        enabledByDefault = false)
  }

  override fun createUastHandler(context: JavaContext): UElementHandler {
    return object : UElementHandler() {
      override fun visitImportStatement(node: UImportStatement) {
        val importReference = node.importReference?.asSourceString().orEmpty()
        if (importReference == "android.util.Pair") {
          context.report(ISSUE, context.getLocation(node), LINT_ERROR_MESSAGE)
        }
      }
    }
  }

  override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UImportStatement::class.java)
}

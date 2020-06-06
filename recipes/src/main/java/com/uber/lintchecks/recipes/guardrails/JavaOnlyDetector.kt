/*
 * Copyright (C) 2020. Uber Technologies
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
package com.uber.lintchecks.recipes.guardrails

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.UastLintUtils
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.uber.lintchecks.android.createImplementation
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UAnonymousClass
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UCallableReferenceExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UReturnExpression
import org.jetbrains.uast.getContainingUClass
import org.jetbrains.uast.getContainingUMethod
import org.jetbrains.uast.toUElementOfType

/**
 * Logic adapted from the analogous KotlinOnlyChecker in Error-Prone.
 */
class JavaOnlyDetector : Detector(), SourceCodeScanner {
  companion object {
    private const val KOTLIN_ONLY = "com.uber.lintchecks.recipes.guardrails.KotlinOnly"
    private const val JAVA_ONLY = "com.uber.lintchecks.recipes.guardrails.JavaOnly"
    private const val ISSUE_ID = "JavaOnlyDetector"
    private const val MESSAGE_LINT_ERROR_TITLE = "Using @JavaOnly elements in Kotlin code."
    private const val MESSAGE_LINT_ERROR_EXPLANATION = "This should not be called from Kotlin code"
    @JvmField
    val ISSUE = Issue.create(
        ISSUE_ID,
        MESSAGE_LINT_ERROR_TITLE,
        MESSAGE_LINT_ERROR_EXPLANATION,
        Category.INTEROPERABILITY_KOTLIN,
        6,
        Severity.ERROR,
        createImplementation<JavaOnlyDetector>())

    private fun anonymousTypeString(psiClass: PsiClass, type: String): String {
      return "Cannot create $type instances of @JavaOnly-annotated type ${UastLintUtils.getClassName(psiClass)} (in ${psiClass.containingFile.name}) " +
          "in Kotlin. Make a concrete class instead."
    }
  }

  override fun createUastHandler(context: JavaContext): UElementHandler? {
    if (context.file.extension != "kt") {
      // We only run this on Kotlin files, the ErrorProne analogue handles Java files. Can revisit if we get lint in the IDE or otherwise unify
      return UElementHandler.NONE
    }
    return object : UElementHandler() {
      override fun visitClass(node: UClass) {
        val hasJavaOnly = context.evaluator.findAnnotation(node, JAVA_ONLY) != null
        val hasKotlinOnly = context.evaluator.findAnnotation(node, KOTLIN_ONLY) != null
        if (hasJavaOnly && hasKotlinOnly) {
          context.report(ISSUE, context.getLocation(node.sourcePsi!!),
              "Cannot annotate types with both @KotlinOnly and @JavaOnly")
          return
        }
        if (hasJavaOnly || hasKotlinOnly) {
          return
        }
        if (node is UAnonymousClass) {
          if (node.uastParent.isReturnExpression() && node.isEnclosedInJavaOnlyMethod()) {
            return
          }
          node.baseClassType.resolve()?.let { psiClass ->
            context.evaluator.findAnnotation(psiClass, JAVA_ONLY)?.run {
              val message = anonymousTypeString(psiClass, "anonymous")
              context.report(ISSUE, context.getLocation(node.sourcePsi!!), message)
            }
          }
          return
        }
        val reportData = checkMissingSubclass(node, KOTLIN_ONLY, "KotlinOnly")
            ?: checkMissingSubclass(node, JAVA_ONLY, "JavaOnly") ?: return
        context.report(ISSUE, context.getLocation(node.sourcePsi!!), reportData.first,
            reportData.second)
      }

      private fun checkMissingSubclass(
        node: UClass,
        targetAnnotation: String,
        targetAnnotationSimpleName: String
      ): Pair<String, LintFix>? {
        return listOfNotNull(node.superClass, *node.interfaces)
            .mapNotNull { psiClass ->
              context.evaluator.findAnnotation(psiClass, targetAnnotation)?.run {
                val message = "Type subclasses/implements ${UastLintUtils.getClassName(psiClass)} in ${psiClass.containingFile.name} which is annotated @$targetAnnotationSimpleName, it should also be annotated."
                val source = node.text
                return@mapNotNull message to fix()
                    .replace()
                    .name("Add @$targetAnnotationSimpleName")
                    .range(context.getLocation(node.sourcePsi!!))
                    .shortenNames()
                    .text(source)
                    .with("@$targetAnnotation $source")
                    .autoFix()
                    .build()
              }
            }
            .firstOrNull()
      }

      override fun visitLambdaExpression(node: ULambdaExpression) {
        if (node.isReturnExpression() && node.isEnclosedInJavaOnlyMethod()) {
          return
        }
        node.functionalInterfaceType?.let { type ->
          if (type is PsiClassType) {
            type.resolve()?.let { psiClass ->
              context.evaluator.findAnnotation(psiClass, JAVA_ONLY)?.let {
                val message = anonymousTypeString(psiClass, "lambda")
                context.report(ISSUE, context.getLocation(node.sourcePsi!!), message)
                return
              }
              val functionalMethod = psiClass.methods.first()
              functionalMethod.toUElementOfType<UMethod>()?.isAnnotationPresent()?.let {
                node.report(it, "expressed as a lambda in Kotlin")
              }
            }
          }
        }
      }

      override fun visitMethod(node: UMethod) {
        val hasJavaOnly = context.evaluator.findAnnotation(node, JAVA_ONLY) != null
        val hasKotlinOnly = context.evaluator.findAnnotation(node, KOTLIN_ONLY) != null
        if (hasJavaOnly && hasKotlinOnly) {
          context.report(ISSUE, context.getLocation(node.sourcePsi!!),
              "Cannot annotate functions with both @KotlinOnly and @JavaOnly")
          return
        }
        if (hasJavaOnly || hasKotlinOnly) {
          return
        }
        val reportData = checkMissingOverride(node, KOTLIN_ONLY, "KotlinOnly")
            ?: checkMissingOverride(node, JAVA_ONLY, "JavaOnly") ?: return
        context.report(ISSUE, context.getLocation(node), reportData.first, reportData.second)
      }

      private fun checkMissingOverride(
        node: UMethod,
        targetAnnotation: String,
        targetAnnotationSimpleName: String
      ): Pair<String, LintFix>? {
        return context.evaluator.getSuperMethod(node)?.let { method ->
          context.evaluator.findAnnotation(method, targetAnnotation)?.run {
            val message = "Function overrides ${method.name} in ${UastLintUtils.getClassName(
                method.containingClass)} which is annotated @$targetAnnotationSimpleName, it should also be annotated."
            val modifier = node.modifierList.children.joinToString(separator = " ") { it.text }
            return@let message to fix()
                .replace()
                .name("Add @$targetAnnotationSimpleName")
                .range(context.getLocation(node))
                .shortenNames()
                .text(modifier)
                .with("@$targetAnnotation $modifier")
                .autoFix()
                .build()
          }
        }
      }

      override fun visitCallExpression(node: UCallExpression) {
        node.resolve().toUElementOfType<UMethod>()?.isAnnotationPresent()?.let {
          node.report(it)
        }
      }

      override fun visitCallableReferenceExpression(node: UCallableReferenceExpression) {
        node.resolve().toUElementOfType<UMethod>()?.isAnnotationPresent()?.let {
          node.report(it)
        }
      }

      private fun UExpression.report(javaOnlyMessage: String?, callString: String = "called from Kotlin") {
        val message = StringBuilder("This method should not be $callString")
        if (javaOnlyMessage.isNullOrBlank()) {
          message.append(", see its documentation for details.")
        } else {
          message.append(": $javaOnlyMessage")
        }
        context.report(ISSUE, context.getLocation(this), message.toString())
      }

      private fun UElement?.isReturnExpression(): Boolean = this != null && uastParent is UReturnExpression

      private fun UElement.isEnclosedInJavaOnlyMethod(): Boolean {
        return getContainingUMethod()?.isAnnotationPresent() != null
      }

      private fun UMethod.isAnnotationPresent(): String? {
        findAnnotation(JAVA_ONLY)?.let { return it.extractValue() }
        getContainingUClass()?.findAnnotation(JAVA_ONLY)?.let { return it.extractValue() }
        context.evaluator.getPackage(this)?.let { pkg ->
          context.evaluator.findAnnotation(pkg,
              KOTLIN_ONLY)?.toUElementOfType<UAnnotation>()?.let { return it.extractValue() }
        }
        return null
      }

      private fun UAnnotation.extractValue(): String {
        return UastLintUtils.getAnnotationStringValue(this, "value").orEmpty()
      }
    }
  }

  override fun getApplicableUastTypes(): List<Class<out UElement>> {
    return listOf(
        UMethod::class.java,
        UCallExpression::class.java,
        UCallableReferenceExpression::class.java,
        ULambdaExpression::class.java,
        UClass::class.java
    )
  }
}

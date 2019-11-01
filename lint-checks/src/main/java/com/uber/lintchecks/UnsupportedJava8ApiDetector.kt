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
import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.google.common.collect.Sets
import com.intellij.psi.PsiMethod
import com.uber.lintchecks.android.createImplementation
import org.jetbrains.uast.*

/**
 * Prevent importing unsupported Java 8 APIs when using Retrolambda
 */
class UnsupportedJava8ApiDetector : Detector(), SourceCodeScanner {
    companion object {
        private const val ISSUE_ID = "UnsupportedJava8Api"
        private const val BRIEF_DESCRIPTION = "Don't import stream, time, or file packages from Java 8 when using Retrolambda"
        const val LINT_ERROR_MESSAGE = """Cannot import and use classes from the Java 8 APIs that are 
      unsupported by Android. If this code will never run on Android and you want to use these APIs, then
      please add your module to the whitelist in tooling/buck-defs/errorprone."""

        const val LAMBDA_IN_INTERFACE_ERROR_MESSAGE = """We do not yet support lambdas within interfaces for
      Android code.  If this code will never run on Android, then
      please add your module to the whitelist in tooling/buck-defs/errorprone."""

        val FORBIDDEN_IMPORTS: Set<String> = Sets.newHashSet(
                "java.lang.FunctionalInterface",
                "java.nio.file.Files",
                "java.time",
                "java.util.Objects",
                "java.util.function",
                "java.util.stream")

        val FORBIDDEN_METHODS: Map<String, List<String>> = hashMapOf(
                "java.lang.Iterable" to listOf("forEach", "spliterator"),
                "java.util.Collection" to listOf("removeIf", "stream", "parallelStream"),
                "java.util.List" to listOf("sort", "replaceAll"),
                "java.util.Map" to listOf("getOrDefault", "forEach", "replaceAll", "putIfAbsent", "computeIfAbsent", "computeIfPresent", "compute", "merge")
        )


        val ISSUE = Issue.create(
                id = ISSUE_ID,
                briefDescription = BRIEF_DESCRIPTION,
                explanation = LINT_ERROR_MESSAGE,
                category = Category.CORRECTNESS,
                priority = 6,
                severity = Severity.ERROR,
                implementation = createImplementation<UnsupportedJava8ApiDetector>())
    }



    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitImportStatement(node: UImportStatement) {
                val importReference = node.importReference?.asSourceString().orEmpty()

                if (FORBIDDEN_IMPORTS.any { it == importReference || it.startsWith(importReference) }) {
                    context.report(ISSUE, context.getLocation(node), LINT_ERROR_MESSAGE)
                }
            }

            override fun visitLambdaExpression(node: ULambdaExpression) {
                val containingClass = node.getContainingUClass()

                if (containingClass != null) {
                    if(containingClass.isInterface) {
                        context.report(ISSUE, context.getLocation(node), LAMBDA_IN_INTERFACE_ERROR_MESSAGE)
                    }
                } else {
                    throw RuntimeException("should have found an enclosing class")
                }
            }
        }
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (!getApplicableMethodNames().contains(node.methodName)) return

        if (isImportOfType(context.evaluator, node)) {
            context.report(ISSUE, context.getLocation(node), BRIEF_DESCRIPTION)
        }
    }

    private fun isImportOfType(evaluator: JavaEvaluator, node: UCallExpression): Boolean {
        val className = FORBIDDEN_METHODS.filterValues { it.contains(node.methodName) }.keys
        return className.count { evaluator.isMemberInClass(node.resolve(), it)  } != 0
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UImportStatement::class.java, ULambdaExpression::class.java)

    override fun getApplicableMethodNames(): List<String> = FORBIDDEN_METHODS.values.flatten().toHashSet().toList()
}

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

import com.android.SdkConstants.ANDROID_NS_NAME_PREFIX
import com.android.SdkConstants.ATTR_FILL_VIEWPORT
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.ResourceXmlDetector
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.XmlContext
import org.w3c.dom.Element

/** Custom lint check to make sure that Scrollview which has WebView as its child sets fillViewport to true*/
class XmlWebViewInsideScrollViewDetector : ResourceXmlDetector() {
    companion object {
        private const val ISSUE_ID = "WebViewInsideScrollview"
        private const val BRIEF_DESCRIPTION = "Add android:fillViewport=true in the ScrollView to avoid unexpected behaviors in WebView"
        val LINT_ERROR_MESSAGE = """
                Add android:fillViewport=true in the ScrollView to avoid unexpected behaviors in WebView.
                When WebView is wrapped inside ScrollView, the WebView sometimes doesn't provide the necessary
                viewport height to the webpage and a results in a zero height page.""".trimIndent().replace('\n', ' ')
        val ISSUE = Issue.create(
                id = ISSUE_ID,
                briefDescription = BRIEF_DESCRIPTION,
                explanation = LINT_ERROR_MESSAGE,
                category = Category.CORRECTNESS,
                priority = 6,
                severity = Severity.ERROR,
                implementation = createImplementation<XmlWebViewInsideScrollViewDetector>())

        private const val SCROLLVIEW_VIEW_CLASS_NAME = "ScrollView"
        private const val SUPPORT_WIDGET_NESTED_SCROLLVIEW_CLASS_NAME = "android.support.v4.widget.NestedScrollView"
        private const val ANDROIDX_WIDGET_NESTED_SCROLLVIEW_CLASS_NAME = "androidx.core.widget.NestedScrollView"
        private const val WEB_VIEW_CLASS_NAME = "WebView"
        private const val ATTR_ANDROID_VIEWPORT = ANDROID_NS_NAME_PREFIX + ATTR_FILL_VIEWPORT
    }

    override fun appliesTo(folderType: ResourceFolderType) = folderType == ResourceFolderType.LAYOUT

    override fun getApplicableElements() = setOf(WEB_VIEW_CLASS_NAME)

    override fun visitElement(context: XmlContext, element: Element) {
        val parentScrollView = findParentScrollView(element)
        parentScrollView?.attributes?.let { attrs ->
            if (attrs.getNamedItem(ATTR_ANDROID_VIEWPORT) == null ||
                    attrs.getNamedItem(ATTR_ANDROID_VIEWPORT).nodeValue != "true") {
                val replaceFix = LintFix.create()
                        .set()
                        .attribute(ATTR_FILL_VIEWPORT)
                        .value("true")
                        .build()
                context.report(ISSUE,
                        context.getElementLocation(parentScrollView), LINT_ERROR_MESSAGE,
                        replaceFix)
            }
        }
    }

    private fun findParentScrollView(element: Element): Element? {
        return when {
            SCROLLVIEW_VIEW_CLASS_NAME == element.tagName -> element
            SUPPORT_WIDGET_NESTED_SCROLLVIEW_CLASS_NAME == element.tagName -> element
            ANDROIDX_WIDGET_NESTED_SCROLLVIEW_CLASS_NAME == element.tagName -> element
            element.parentNode is Element -> findParentScrollView(element.parentNode as Element)
            else -> null
        }
    }
}

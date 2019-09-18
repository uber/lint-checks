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

import com.android.SdkConstants
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.ResourceXmlDetector
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.XmlContext
import com.android.tools.lint.detector.api.XmlScannerConstants
import org.w3c.dom.Attr

/**
 * Custom lint check to make sure we aren't using hard coded dimensions and colors in resource files.
 */
class XmlHardcodedColorOrDimensionDetector : ResourceXmlDetector() {

  companion object {
    const val ISSUE_ID = "HardcodedValueInXML"
    val LINT_ERROR_MESSAGE = """
                It's generally good practice not to hardcode colors and instead use theme attributes.
                 This allows for easy refactoring and makes sure there aren't too many shades of the same color.
                 Dimensions should use theme attributes or (if they're specific to a feature) local resource references.
                """.trimIndent().replace('\n', ' ')
    @JvmField
    val ISSUE = Issue.create(
        ISSUE_ID,
        "Don't use hardcoded values",
        LINT_ERROR_MESSAGE,
        Category.CORRECTNESS,
        6,
        Severity.ERROR,
        createImplementation<XmlHardcodedColorOrDimensionDetector>())

    private val HARDCODED_DIMEN_PATTERN = ".*(dp|sp|px|dip)".toRegex()
    private val VECTOR_TAGS = setOf(SdkConstants.TAG_VECTOR, "animated-vector")
    private const val ZERO_DP = "0dp"
  }

  override fun appliesTo(folderType: ResourceFolderType) =
      folderType in setOf(ResourceFolderType.ANIM, ResourceFolderType.DRAWABLE, ResourceFolderType.LAYOUT)

  override fun getApplicableAttributes(): List<String> = XmlScannerConstants.ALL

  override fun visitAttribute(context: XmlContext, attr: Attr) {
    val isVector = context.document.documentElement?.tagName in VECTOR_TAGS
    val value = attr.value.orEmpty()
    if (value.startsWith("#") ||
        (!isVector && value != ZERO_DP && HARDCODED_DIMEN_PATTERN.matches(value))) {
      context.report(ISSUE, context.getLocation(attr), LINT_ERROR_MESSAGE)
    }
  }
}

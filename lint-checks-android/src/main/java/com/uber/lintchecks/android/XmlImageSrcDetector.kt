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
import com.android.SdkConstants.ATTR_SRC
import com.android.SdkConstants.ATTR_SRC_COMPAT
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.ResourceXmlDetector
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.XmlContext
import org.w3c.dom.Attr

/**
 * Custom lint check to make sure we aren't using android:src in xml and use app:srcCompat instead.
 */
class XmlImageSrcDetector : ResourceXmlDetector() {

  companion object {
    const val ISSUE_ID = "SrcCompatUsage"
    const val LINT_ERROR_MESSAGE = "Use app:$ATTR_SRC_COMPAT for vector drawable compatibility."
    @JvmField
    val ISSUE = Issue.create(
        ISSUE_ID,
        "Use app:srcCompat for vector drawable compatibility.",
        LINT_ERROR_MESSAGE,
        Category.CORRECTNESS,
        6,
        Severity.ERROR,
        createImplementation<XmlImageSrcDetector>())

    private const val ANDROID_SRC = ANDROID_NS_NAME_PREFIX + ATTR_SRC
  }

  override fun appliesTo(folderType: ResourceFolderType) = folderType == ResourceFolderType.LAYOUT

  override fun getApplicableAttributes() = setOf(ATTR_SRC)

  override fun visitAttribute(context: XmlContext, attribute: Attr) {
    if (ANDROID_SRC == attribute.name && context.mainProject.minSdk < 21) {
      val replaceFix = LintFix.create()
          .composite(
              LintFix.create()
                  .set()
                  .namespace("http://schemas.android.com/apk/res-auto")
                  .attribute("srcCompat")
                  .value(attribute.value)
                  .build(),
              LintFix.create()
                  .unset()
                  .android()
                  .attribute("src")
                  .build())

      context.report(ISSUE, context.getLocation(attribute), LINT_ERROR_MESSAGE,
          LintFix.create().composite(replaceFix))
    }
  }
}

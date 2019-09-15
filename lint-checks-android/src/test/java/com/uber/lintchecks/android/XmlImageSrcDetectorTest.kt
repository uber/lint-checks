package com.uber.lintchecks.android

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.uber.lintchecks.android.XmlImageSrcDetector.Companion.LINT_ERROR_MESSAGE
import com.uber.lintchecks.base.test.LintTestBase
import org.junit.Test

class XmlImageSrcDetectorTest : LintTestBase() {

  override val testResourcesPath: String = "sample/ui/xml_image_src"

  @Test
  fun testDetector_useSrc_shouldFail() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/ub__contains_android_src.xml",
            """<?xml version="1.0" encoding="utf-8"?>
              <ImageView 
              xmlns:android="http://schemas.android.com/apk/res/android" 
              android:layout_width="@dimen/blah" 
              android:layout_height="@dimen/blah" 
              android:src="@drawable/something"/>
              """))
        .detector(XmlImageSrcDetector())
        .issues(XmlImageSrcDetector.ISSUE)
        .run()
        .expectErrorCount(1)
        .expectMatches(LINT_ERROR_MESSAGE)
  }

  @Test
  fun testDetector_useSrcWithMinSdk21_shouldPass() {
    val manifestFile = TestFile.ManifestTestFile()
    manifestFile.minSdk(22)
    TestLintTask.lint()
        .files(xmlSource("res/layout/ub__contains_android_src.xml",
            """<?xml version="1.0" encoding="utf-8"?>
              <ImageView 
              xmlns:android="http://schemas.android.com/apk/res/android" 
              android:layout_width="@dimen/blah" 
              android:layout_height="@dimen/blah" 
              android:src="@drawable/something"/>
              """), manifestFile)
        .detector(XmlImageSrcDetector())
        .issues(XmlImageSrcDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun testDetector_useSrcCompat_shouldPass() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/ub__contains_srccompat.xml",
            """<?xml version="1.0" encoding="utf-8"?>
              <ImageView 
              xmlns:android="http://schemas.android.com/apk/res/android" 
              xmlns:app="http://schemas.android.com/apk/res-auto" 
              android:layout_width="@dimen/blah" 
              android:layout_height="@dimen/blah" 
              app:srcCompat="@drawable/something"/>
              """))
        .detector(XmlImageSrcDetector())
        .issues(XmlImageSrcDetector.ISSUE)
        .run()
        .expectClean()
  }
}

package com.uber.lintchecks.android

import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.uber.lintchecks.base.test.LintTestBase
import org.junit.Test

class ColorResourceUsageDetectorTest : LintTestBase() {

  override val testResourcesPath = ""

  @Test
  fun testDetector_containsColorRes_shouldFail() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/ub__contains_color_res.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <ImageView
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    android:layout_width="@dimen/blah"
                    android:layout_height="@dimen/blah"
                    android:background="@color/black"/>
                    """))
        .detector(ColorResourceUsageDetector())
        .issues(ColorResourceUsageDetector.ISSUE)
        .run()
        .expectErrorCount(1)
        .expectMatches(ColorResourceUsageDetector.LINT_ERROR_MESSAGE)
  }

  @Test
  fun testDetector_useThemeAttr_shouldPass() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/ub__contains_theme_attr.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <ImageView
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    android:layout_width="@dimen/blah"
                    android:layout_height="@dimen/blah"
                    android:background="?accentColor"/>
                    """))
        .detector(ColorResourceUsageDetector())
        .issues(ColorResourceUsageDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun testDetector_containsThemeAttrInQualifiedFolder_shouldPass() {
    TestLintTask.lint()
        .files(xmlSource("res/drawable-v21/ub__contains_theme_attr_qualified_folder.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
                    <size android:height="?someThemeAttrDimen" />
                    <solid android:color="?someThemeAttrColor" />
                </shape>
                """))
        .detector(ColorResourceUsageDetector())
        .issues(ColorResourceUsageDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun testDetector_containsThemeAttrInV24QualifiedFolder_shouldPass() {
    TestLintTask.lint()
        .files(xmlSource("res/drawable-v24/ub__contains_theme_attr_qualified_folder.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
                    <size android:height="?someThemeAttrDimen" />
                    <solid android:color="?someThemeAttrColor" />
                </shape>
                """))
        .detector(ColorResourceUsageDetector())
        .issues(ColorResourceUsageDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun testDetector_containsThemeAttrInUnqualifiedFolder_shouldFail() {
    TestLintTask.lint()
        .files(xmlSource("res/drawable/ub__contains_theme_attr_unqualified_folder.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
                    <size android:height="?someThemeAttrDimen" />
                    <solid android:color="?someThemeAttrColor" />
                </shape>
                """))
        .detector(ColorResourceUsageDetector())
        .issues(ColorResourceUsageDetector.ISSUE)
        .run()
        .expectErrorCount(1)
        .expectMatches(ColorResourceUsageDetector.LINT_ERROR_MESSAGE)
  }

  @Test
  fun testDetector_containsThemeAttrInUnqualifiedFolderInVector_shouldPass() {
    TestLintTask.lint()
        .files(xmlSource("res/drawable/ub__contains_theme_attr_vector.xml",
            """<vector xmlns:android="http://schemas.android.com/apk/res/android"
                android:width="24dp"
                android:height="24dp"
                android:viewportHeight="64.0"
                android:viewportWidth="64.0">
                    <path
                        android:fillColor="?brandGrey80"
                        android:pathData="M32,32m-32,0a32,32 0,1 1,64 0a32,32 0,1 1,-64 0" />
                    <path
                        android:fillColor="?brandGrey60"
                        android:pathData="M32,29c3.3,0 6,-2.7 6,-6s-2.7,-6 -6,-6s-6,2.7 -6,6S28.7,29 32,29zM32,18.5c2.5,0 4.5,2 4.5,4.5s-2,4.5 -4.5,4.5s-4.5,-2 -4.5,-4.5S29.5,18.5 32,18.5z" />
                    <path
                        android:fillColor="?brandGrey60"
                        android:pathData="M41,32H23c-1.7,0 -3,1.3 -3,3v9h1.5v-9c0,-0.8 0.7,-1.5 1.5,-1.5h18c0.8,0 1.5,0.7 1.5,1.5v9H44v-9C44,33.3 42.7,32 41,32z" />
                </vector>
                """))
        .detector(ColorResourceUsageDetector())
        .issues(ColorResourceUsageDetector.ISSUE)
        .run()
        .expectClean()
  }
}

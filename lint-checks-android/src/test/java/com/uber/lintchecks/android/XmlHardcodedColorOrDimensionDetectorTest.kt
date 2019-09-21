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

import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.uber.lintchecks.base.test.LintTestBase
import org.junit.Test

class XmlHardcodedColorOrDimensionDetectorTest : LintTestBase() {

  @Test
  fun testDetector_vectorDrawables_shouldIgnoreHardcodedDimensions() {
    TestLintTask.lint()
        .files(xmlSource("res/drawable/ub__vector.xml", """<?xml version="1.0" encoding="utf-8"?>
          <vector xmlns:android="http://schemas.android.com/apk/res/android"
            android:width="16dp"
            android:height="16dp"
            android:viewportHeight="16"
            android:viewportWidth="16">

          <group
            android:translateX="2.000000"
            android:translateY="2.000000">
                <path
                    android:pathData="M0.5,12.5 L0.5,10.998 C0.5,9.339 1.787,7.994 3.375,7.994 L9.125,7.994
C10.713,7.994 12,9.339 12,10.998 L12,12.5"
                    android:strokeColor="?iconColor"
                    android:strokeWidth="1" />
                    <path
                    android:pathData="M9.125,3.4412 C9.125,5.0652 7.838,6.3822 6.25,6.3822 C4.662,6.3822 3.375,5.0652
3.375,3.4412 C3.375,1.8172 4.662,0.5002 6.25,0.5002 C7.838,0.5002 9.125,1.8172
9.125,3.4412 L9.125,3.4412 Z"
                    android:strokeColor="?iconColor"
                    android:strokeWidth="1" />
                </group>
                </vector>
        """).indented())
        .detector(XmlHardcodedColorOrDimensionDetector())
        .issues(XmlHardcodedColorOrDimensionDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun testDetector_hardcodedDimen() {
    // assertThat(lintFiles(file)).contains("=\"100dp\"")
    TestLintTask.lint()
        .files(xmlSource("res/layout/ub__contains_hardcoded_dimen.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <LinearLayout
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    android:layout_width="match_parent"
                    android:layout_height="100dp"
                    android:orientation="vertical"/>
        """))
        .detector(XmlHardcodedColorOrDimensionDetector())
        .issues(XmlHardcodedColorOrDimensionDetector.ISSUE)
        .run()
        .expectErrorCount(1)
        .expectMatches("=\"100dp\"")
  }

  @Test
  fun testDetector_hardcodedColor() {
    // assertThat(lintFiles(file)).contains("=\"#ffffff\"")
    TestLintTask.lint()
        .files(xmlSource("res/layout/ub__contains_hardcoded_color.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <LinearLayout
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    android:layout_width="@dimen/blah"
                    android:layout_height="@dimen/blah"
                    android:background="#ffffff"
                    android:orientation="vertical"/>"""))
        .detector(XmlHardcodedColorOrDimensionDetector())
        .issues(XmlHardcodedColorOrDimensionDetector.ISSUE)
        .run()
        .expectErrorCount(1)
        .expectMatches("=\"#ffffff\"")
  }

  @Test
  fun testDetector_hardcodedDimen_shouldIgnore0dp() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/ub__contains_hardcoded_dimen_zerodp.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <LinearLayout
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    android:layout_width="0dp"
                    android:layout_height="@dimen/blah"
                    android:orientation="vertical"/>"""))
        .detector(XmlHardcodedColorOrDimensionDetector())
        .issues(XmlHardcodedColorOrDimensionDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun testDetector_noHardcodes_shouldNotWarn() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/ub__contains_no_hardcoding.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <LinearLayout
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    android:layout_width="@dimen/whatever"
                    android:layout_height="@dimen/whatever"
                    android:background="?colorAccent"
                    android:orientation="vertical"/>"""))
        .detector(XmlHardcodedColorOrDimensionDetector())
        .issues(XmlHardcodedColorOrDimensionDetector.ISSUE)
        .run()
        .expectClean()
  }
}

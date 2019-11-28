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

class XmlWebViewInsideScrollViewDetectorTest : LintTestBase() {
  @Test
  fun testDetector_scrollViewWithoutFillViewPort_shouldFail() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/webview_inside_scrollview_wo_fill_viewport.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <ScrollView
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent">

                    <WebView
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"/>
                </ScrollView>"""))
        .detector(XmlWebViewInsideScrollViewDetector())
        .issues(XmlWebViewInsideScrollViewDetector.ISSUE)
        .run()
        .expectErrorCount(1)
        .expectMatches(XmlWebViewInsideScrollViewDetector.LINT_ERROR_MESSAGE)
  }

  @Test
  fun testDetector_supportWidgetNestedScrollViewWithoutFillViewPort_shouldFail() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/webview_inside_support_widget_scrollview_wo_fill_viewport.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <android.support.v4.widget.NestedScrollView
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    app:layout_behavior="@string/appbar_scrolling_view_behavior">

                    <WebView
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"/>
                </android.support.v4.widget.NestedScrollView>"""))
        .detector(XmlWebViewInsideScrollViewDetector())
        .issues(XmlWebViewInsideScrollViewDetector.ISSUE)
        .run()
        .expectErrorCount(1)
        .expectMatches(XmlWebViewInsideScrollViewDetector.LINT_ERROR_MESSAGE)
  }

  @Test
  fun testDetector_androidxWidgetNestedScrollViewWithoutFillViewPort_shouldFail() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/webview_inside_androidx_widget_scrollview_wo_fill_viewport.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <androidx.core.widget.NestedScrollView
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    app:layout_behavior="@string/appbar_scrolling_view_behavior">

                    <WebView
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"/>
                </androidx.core.widget.NestedScrollView>"""))
        .detector(XmlWebViewInsideScrollViewDetector())
        .issues(XmlWebViewInsideScrollViewDetector.ISSUE)
        .run()
        .expectErrorCount(1)
        .expectMatches(XmlWebViewInsideScrollViewDetector.LINT_ERROR_MESSAGE)
  }

  @Test
  fun testDetector_scrollViewWithFillViewPort_shouldPass() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/webview_inside_scrollview_with_fill_viewport.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <ScrollView
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:fillViewport="true">

                    <WebView
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"/>
                </ScrollView>"""))
        .detector(XmlWebViewInsideScrollViewDetector())
        .issues(XmlWebViewInsideScrollViewDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun testDetector_supportWidgetNestedScrollViewWithFillViewPort_shouldPass() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/webview_inside_support_widget_scrollview_with_fill_viewport.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <android.support.v4.widget.NestedScrollView
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:fillViewport="true"
                    app:layout_behavior="@string/appbar_scrolling_view_behavior">

                    <WebView
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"/>
                </android.support.v4.widget.NestedScrollView>"""))
        .detector(XmlWebViewInsideScrollViewDetector())
        .issues(XmlWebViewInsideScrollViewDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun testDetector_androidxWidgetNestedScrollViewWithFillViewPort_shouldPass() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/webview_inside_androidx_widget_scrollview_with_fill_viewport.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <androidx.core.widget.NestedScrollView
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:fillViewport="true"
                    app:layout_behavior="@string/appbar_scrolling_view_behavior">

                    <WebView
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"/>
                </androidx.core.widget.NestedScrollView>"""))
        .detector(XmlWebViewInsideScrollViewDetector())
        .issues(XmlWebViewInsideScrollViewDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun testDetector_scrollView_withMultipleChildren_WithoutFillViewPort_shouldFail() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/scrollview_with_multiple_children_without_fill_viewport.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <ScrollView
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    app:layout_behavior="@string/appbar_scrolling_view_behavior">

                    <FrameLayout
                        android:layout_width="match_parent"
                        android:layout_height="match_parent">

                        <WebView
                            android:layout_width="match_parent"
                            android:layout_height="match_parent"/>
                    </FrameLayout>
                </ScrollView>"""))
        .detector(XmlWebViewInsideScrollViewDetector())
        .issues(XmlWebViewInsideScrollViewDetector.ISSUE)
        .run()
        .expectErrorCount(1)
        .expectMatches(XmlWebViewInsideScrollViewDetector.LINT_ERROR_MESSAGE)
  }

  @Test
  fun testDetector_scrollView_withMultipleChildren_WithFillViewPort_shouldPass() {
    TestLintTask.lint()
        .files(xmlSource("res/layout/scrollview_with_multiple_children_with_fill_viewport.xml",
            """<?xml version="1.0" encoding="utf-8"?>
                <ScrollView 
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:fillViewport="true"
                    app:layout_behavior="@string/appbar_scrolling_view_behavior">

                    <FrameLayout
                        android:layout_width="match_parent"
                        android:layout_height="match_parent">

                        <WebView
                            android:layout_width="match_parent"
                            android:layout_height="match_parent"/>
                    </FrameLayout>
                </ScrollView>"""))
        .detector(XmlWebViewInsideScrollViewDetector())
        .issues(XmlWebViewInsideScrollViewDetector.ISSUE)
        .run()
        .expectClean()
  }
}

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

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.uber.lintchecks.base.test.LintTestBase
import org.junit.Test

class GetDrawableDetectorTest : LintTestBase() {

  @Test
  fun testGetDrawable_java_contextCompat_fails() {
    lint()
        .files(appcompat(), javaSource("""
          package foo;

          import androidx.core.content.ContextCompat;

          public class Activity {
            void test() {
              ContextCompat.getDrawable(null, 0);
            }
          }
        """))
        .detector(GetDrawableDetector())
        .issues(GetDrawableDetector.ISSUE)
        .run()
        .expectErrorCount(1)
  }

  @Test
  fun testGetDrawable_kotlin_contextCompat_fails() {
    lint()
        .files(appcompat(), kotlinSource("""
          package foo

          import androidx.core.content.ContextCompat

          class Activity {
            fun test() {
              ContextCompat.getDrawable(null, 0);
            }
          }
        """))
        .detector(GetDrawableDetector())
        .issues(GetDrawableDetector.ISSUE)
        .run()
        .expectErrorCount(1)
  }

  @Test
  fun testGetDrawable_java_resourcesCompat_fails() {
    lint()
        .files(appcompat(), javaSource("""
          package foo;

          import androidx.core.content.res.ResourcesCompat;

          public class Activity {
            void test() {
              ResourcesCompat.getDrawable(null, 0, null);
            }
          }
        """))
        .detector(GetDrawableDetector())
        .issues(GetDrawableDetector.ISSUE)
        .run()
        .expectErrorCount(1)
  }

  @Test
  fun testGetDrawable_kotlin_resourcesCompat_fails() {
    lint()
        .files(appcompat(), kotlinSource("""
          package foo

          import androidx.core.content.res.ResourcesCompat

          class Activity {
            fun test() {
              ResourcesCompat.getDrawable(null, 0, null)
            }
          }
        """))
        .detector(GetDrawableDetector())
        .issues(GetDrawableDetector.ISSUE)
        .run()
        .expectErrorCount(1)
  }

  @Test
  fun testGetDrawable_java_appcompatResources_pass() {
    lint()
        .files(appcompat(), javaSource("""
          package foo;

          import androidx.appcompat.content.res.AppCompatResources;

          public class Activity {
            void test() {
              AppCompatResourcees.getDrawable(null, 0);
            }
          }
        """))
        .detector(GetDrawableDetector())
        .issues(GetDrawableDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun testGetDrawable_kotlin_appcompatResources_fails() {
    lint()
        .files(appcompat(), kotlinSource("""
          package foo

          import androidx.core.content.res.ResourcesCompat

          class Activity {
            fun test() {
              AppCompatResources.getDrawable(null, 0)
            }
          }
        """))
        .detector(GetDrawableDetector())
        .issues(GetDrawableDetector.ISSUE)
        .run()
        .expectClean()
  }
}

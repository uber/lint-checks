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

class ResCompatGetColorDetectorTest : LintTestBase() {

  @Test
  fun testResCompatGetColor_java_fails() {
    lint()
        .files(appcompat(), javaSource("""
          package foo;

          import androidx.core.content.res.ResourcesCompat;

          public class Activity {
            void test() {
              ResourcesCompat.getColor(null, 0, null);
            }
          }
        """))
        .detector(ResCompatGetColorDetector())
        .issues(ResCompatGetColorDetector.ISSUE)
        .run()
        .expectErrorCount(1)
  }

  @Test
  fun testResCompatGetColor_kotlin_fails() {
    lint()
        .files(appcompat(), kotlinSource("""
          package foo

          import androidx.core.content.res.ResourcesCompat

          class Activity {
            fun test() {
              ResourcesCompat.getColor(null, 0, null)
            }
          }
        """))
        .detector(ResCompatGetColorDetector())
        .issues(ResCompatGetColorDetector.ISSUE)
        .run()
        .expectErrorCount(1)
  }

  @Test
  fun testContextCompatGetColor_kotlin_fails() {
    lint()
        .files(appcompat(), kotlinSource("""
          package foo

          import androidx.core.content.ContextCompat

          class Activity {
            fun test() {
              ContextCompat.getColor(null, 0)
            }
          }
        """))
        .detector(ResCompatGetColorDetector())
        .issues(ResCompatGetColorDetector.ISSUE)
        .run()
        .expectErrorCount(0)
  }
}

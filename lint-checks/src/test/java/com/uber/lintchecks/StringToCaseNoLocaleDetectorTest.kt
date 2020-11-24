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

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.uber.lintchecks.base.test.LintTestBase
import org.junit.Test

class StringToCaseNoLocaleDetectorTest : LintTestBase() {

  @Test
  fun kotlinWithoutLocale_errors() {
    lint()
        .files(kotlinSource("""
          package foo

          class Sample {
            fun sample() {
              "hello".toUpperCase()
            }
          }
        """))
        // Ignored because Lint seems to run tests in two modes where `kotlin.uast.force.uinjectionhost` is
        // on or off. The output is different in those two modes where the position at which the lin
        // error underline happens is off by one. This doesn't mean that the bug is in the detector
        // since it correctly identifies the issue.
        .checkUInjectionHost(false)
        .detector(StringToCaseNoLocaleDetector())
        .issues(StringToCaseNoLocaleDetector.ISSUE)
        .run()
        .expectErrorCount(1)
  }

  @Test
  fun kotlinWithLocale_clean() {
    lint()
        .files(kotlinSource("""
          package foo
          import java.util.Locale

          class Sample {
            fun sample() {
              "hello".toUpperCase(Locale.CANADA)
            }
          }
        """))
        .detector(StringToCaseNoLocaleDetector())
        .issues(StringToCaseNoLocaleDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun javaWithoutLocale_errors() {
    lint()
        .files(javaSource("""
          package foo;

          class Sample {
            void sample() {
              "hello".toUpperCase();
            }
          }
        """))
        .detector(StringToCaseNoLocaleDetector())
        .issues(StringToCaseNoLocaleDetector.ISSUE)
        .run()
        .expectErrorCount(1)
  }

  @Test
  fun javaWithLocale_clean() {
    lint()
        .files(javaSource("""
          package foo;
          import java.util.Locale;

          class Sample {
            fun sample() {
              "hello".toLowerCase(Locale.CANADA);
            }
          }
        """))
        .detector(StringToCaseNoLocaleDetector())
        .issues(StringToCaseNoLocaleDetector.ISSUE)
        .run()
        .expectClean()
  }
}

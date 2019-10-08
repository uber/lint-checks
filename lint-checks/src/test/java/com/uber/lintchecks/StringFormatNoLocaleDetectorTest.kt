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

class StringFormatNoLocaleDetectorTest : LintTestBase() {

  @Test
  fun stringFormatWithoutLocale_errorsOut() {
    lint()
        .files(kotlinSource("""
          package foo

          import java.lang.String

          class Test {
            fun something() {
              String.format("Hello %s", "there")
            }
          }
        """))
        .detector(StringFormatNoLocaleDetector())
        .issues(StringFormatNoLocaleDetector.ISSUE)
        .run()
        .expectErrorCount(1)
  }

  @Test
  fun kotlinStringFormatWithoutLocale_errorsOut() {
    lint()
        .files(kotlinSource("""
          package foo

          import kotlin.String

          class Test {
            fun something() {
              String.format("Hello %s", "there")
            }
          }
        """))
        .detector(StringFormatNoLocaleDetector())
        .issues(StringFormatNoLocaleDetector.ISSUE)
        .run()
        .expectErrorCount(1)
  }

  @Test
  fun stringFormatWithLocale_clean() {
    lint()
        .files(kotlinSource("""
          package foo

          import java.lang.String
          import java.util.Locale

          class Test {
            fun something() {
              String.format(Locale.getDefault(), "Hello %s", "there")
            }
          }
        """))
        .detector(StringFormatNoLocaleDetector())
        .issues(StringFormatNoLocaleDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun kotlinStringFormatWithLocale_clean() {
    lint()
        .files(kotlinSource("""
          package foo

          import kotlin.String
          import java.util.Locale

          class Test {
            fun something() {
              String.format(Locale.getDefault(), "Hello %s", "there")
            }
          }
        """))
        .detector(StringFormatNoLocaleDetector())
        .issues(StringFormatNoLocaleDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun formatMethodNotOnString_expectClean() {
    lint()
        .files(kotlinSource("""
          package foo

          import java.lang.String
          import java.util.Locale

          class Test {
            fun something() {
              format("hello there")
            }

            fun format(format: String)
          }
        """))
        .detector(StringFormatNoLocaleDetector())
        .issues(StringFormatNoLocaleDetector.ISSUE)
        .run()
        .expectClean()
  }
}

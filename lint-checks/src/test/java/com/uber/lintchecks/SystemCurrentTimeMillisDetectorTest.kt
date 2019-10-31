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

class SystemCurrentTimeMillisDetectorTest : LintTestBase() {

  @Test
  fun systemCurrentTimeMillis_errorsOut() {
    lint()
            .files(kotlinSource("""
          package foo

          class Test {
            fun something() {
              val time = System.currentTimeMillis()
            }
          }
        """))
            .detector(SystemCurrentTimeMillisDetector())
            .issues(SystemCurrentTimeMillisDetector.ISSUE)
            .run()
            .expectErrorCount(1)
  }

  @Test
  fun systemCurrentTimeMillis_with_import_errorsOut() {
    lint()
            .files(kotlinSource("""
          package foo
          
          import java.lang.System.currentTimeMillis

          class Test {
            fun something() {
              val time = currentTimeMillis()
            }
          }
        """))
            .detector(SystemCurrentTimeMillisDetector())
            .issues(SystemCurrentTimeMillisDetector.ISSUE)
            .run()
            .expectErrorCount(1)
  }

  @Test
  fun systemCurrentTimeMillis_with_wildcard_import_errorsOut() {
    lint()
            .files(kotlinSource("""
          package foo
          
          import java.lang.System.*

          class Test {
            fun something() {
              val time = currentTimeMillis()
            }
          }
        """))
            .detector(SystemCurrentTimeMillisDetector())
            .issues(SystemCurrentTimeMillisDetector.ISSUE)
            .run()
            .expectErrorCount(1)
  }

  @Test
  fun systemCurrentTimeMillis_with_clock_clean() {
    lint()
            .files(kotlinSource("""
          package foo
          
          import java.lang.Clock

          class Test {
            fun something() {
              val time = currentTimeMillis()
            }
          }
        """))
            .detector(SystemCurrentTimeMillisDetector())
            .issues(SystemCurrentTimeMillisDetector.ISSUE)
            .run()
            .expectClean()
  }

  @Test
  fun systemCurrentTimeMillis_with_as_import_errorsOut() {
    lint()
            .files(kotlinSource("""
          package foo
          
          import java.lang.System.currentTimeMillis as cmt

          class Test {
            fun something() {
              val time = cmt()
            }
          }
        """))
            .detector(SystemCurrentTimeMillisDetector())
            .issues(SystemCurrentTimeMillisDetector.ISSUE)
            .run()
            .expectErrorCount(1)
  }
}

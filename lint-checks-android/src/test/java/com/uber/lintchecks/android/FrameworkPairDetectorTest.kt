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

class FrameworkPairDetectorTest : LintTestBase() {

  @Test
  fun platformPair_errors() {
    lint()
        .files(kotlinSource("""
          package foo

          import android.util.Pair

          class Activity {

          }
        """))
        .detector(FrameworkPairDetector())
        .issues(FrameworkPairDetector.ISSUE)
        .run()
        .expectWarningCount(1)
  }

  @Test
  fun framework_Clean() {
    lint()
        .files(appcompat(), kotlinSource("""
          package foo

          import androidx.core.util.Pair

          class Activity {

          }
        """))
        .detector(FrameworkPairDetector())
        .issues(FrameworkPairDetector.ISSUE)
        .run()
        .expectClean()
  }
}

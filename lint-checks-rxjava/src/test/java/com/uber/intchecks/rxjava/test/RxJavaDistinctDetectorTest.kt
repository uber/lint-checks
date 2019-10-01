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
package com.uber.intchecks.rxjava.test

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.uber.lintchecks.base.test.LintTestBase
import com.uber.lintchecks.rxjava.RxJavaDistinctDetector
import com.uber.lintchecks.rxjava.RxJavaDistinctDetector.Companion.BRIEF_DESCRIPTION
import org.junit.Test

class RxJavaDistinctDetectorTest : LintTestBase() {

  @Test
  fun testDistinct_kotlin_observable_fails() {
    lint()
        .files(rxjava2(), kotlinSource("""
          package foo

          import io.reactivex.Observable

          class Activity {
            fun something() {
              Observable.just(1)
                  .distinct()
                  .subscribe()
            }
          }
        """).indented())
        .detector(RxJavaDistinctDetector())
        .issues(RxJavaDistinctDetector.ISSUE)
        .run()
        .expectWarningCount(1)
        .expectMatches(BRIEF_DESCRIPTION)
  }

  @Test
  fun testDistinct_java_observable_fails() {
    lint()
        .files(rxjava2(), javaSource("""
          package foo;

          import io.reactivex.Observable;

          class Activity {
            void something() {
              Observable.just(1)
                  .distinct()
                  .subscribe();
            }
          }
        """).indented())
        .detector(RxJavaDistinctDetector())
        .issues(RxJavaDistinctDetector.ISSUE)
        .run()
        .expectWarningCount(1)
        .expectMatches(BRIEF_DESCRIPTION)
  }

  @Test
  fun testDistinct_kotlin_flowable_fails() {
    lint()
        .files(rxjava2(), kotlinSource("""
          package foo

          import io.reactivex.Flowable

          class Activity {
            fun something() {
              Flowable.just(1)
                  .distinct()
                  .subscribe()
            }
          }
        """).indented())
        .detector(RxJavaDistinctDetector())
        .issues(RxJavaDistinctDetector.ISSUE)
        .run()
        .expectWarningCount(1)
        .expectMatches(BRIEF_DESCRIPTION)
  }

  @Test
  fun testDistinct_java_flowable_fails() {
    lint()
        .files(rxjava2(), javaSource("""
          package foo;

          import io.reactivex.Flowable;

          class Activity {
            void something() {
              Flowable.just(1)
                  .distinct()
                  .subscribe();
            }
          }
        """).indented())
        .detector(RxJavaDistinctDetector())
        .issues(RxJavaDistinctDetector.ISSUE)
        .run()
        .expectWarningCount(1)
        .expectMatches(BRIEF_DESCRIPTION)
  }

  @Test
  fun testDistinctUntilChanged_passes() {
    lint()
        .files(rxjava2(), kotlinSource("""
          package foo

          import io.reactivex.Observable

          class Activity {
            fun something() {
              Observable.just(1)
                  .distinctUntilChanged()
                  .subscribe()
            }
          }
        """).indented())
        .detector(RxJavaDistinctDetector())
        .issues(RxJavaDistinctDetector.ISSUE)
        .run()
        .expectWarningCount(0)
  }
}

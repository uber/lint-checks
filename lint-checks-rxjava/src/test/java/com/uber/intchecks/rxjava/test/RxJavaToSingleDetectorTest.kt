package com.uber.intchecks.rxjava.test

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.uber.lintchecks.base.test.LintTestBase
import com.uber.lintchecks.rxjava.RxJavaToSingleDetector
import org.junit.Test

class RxJavaToSingleDetectorTest: LintTestBase() {

  @Test
  fun observable_single_warns() {
    lint()
        .files(rxjava2(), kotlinSource("""
          package foo
          
          import io.reactivex.Observable
          
          class Activity {
            fun something() {
              Observable.just(1)
                .single(1)
                .subscribe()
            }
          }
        """))
        .detector(RxJavaToSingleDetector())
        .issues(RxJavaToSingleDetector.ISSUE)
        .run()
        .expectWarningCount(1)
  }

  @Test
  fun observable_singleOrError_warns() {
    lint()
        .files(rxjava2(), kotlinSource("""
          package foo
          
          import io.reactivex.Observable
          
          class Activity {
            fun something() {
              Observable.just(1)
                .singleOrError()
                .subscribe()
            }
          }
        """))
        .detector(RxJavaToSingleDetector())
        .issues(RxJavaToSingleDetector.ISSUE)
        .run()
        .expectWarningCount(1)
  }

  @Test
  fun observable_singleElement_warns() {
    lint()
        .files(rxjava2(), kotlinSource("""
          package foo
          
          import io.reactivex.Observable
          
          class Activity {
            fun something() {
              Observable.just(1)
                .singleElement()
                .subscribe()
            }
          }
        """))
        .detector(RxJavaToSingleDetector())
        .issues(RxJavaToSingleDetector.ISSUE)
        .run()
        .expectWarningCount(1)
  }

  @Test
  fun observable_first_clean() {
    lint()
        .files(rxjava2(), kotlinSource("""
          package foo
          
          import io.reactivex.Observable
          
          class Activity {
            fun something() {
              Observable.just(1)
                .first(1)
                .subscribe()
            }
          }
        """))
        .detector(RxJavaToSingleDetector())
        .issues(RxJavaToSingleDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun flowable_single_warns() {
    lint()
        .files(rxjava2(), kotlinSource("""
          package foo
          
          import io.reactivex.Flowable
          
          class Activity {
            fun something() {
              Flowable.just(1)
                .single(1)
                .subscribe()
            }
          }
        """))
        .detector(RxJavaToSingleDetector())
        .issues(RxJavaToSingleDetector.ISSUE)
        .run()
        .expectWarningCount(1)
  }

  @Test
  fun flowable_singleOrError_warns() {
    lint()
        .files(rxjava2(), kotlinSource("""
          package foo
          
          import io.reactivex.Flowable
          
          class Activity {
            fun something() {
              Flowable.just(1)
                .singleOrError()
                .subscribe()
            }
          }
        """))
        .detector(RxJavaToSingleDetector())
        .issues(RxJavaToSingleDetector.ISSUE)
        .run()
        .expectWarningCount(1)
  }

  @Test
  fun flowable_singleElement_warns() {
    lint()
        .files(rxjava2(), kotlinSource("""
          package foo
          
          import io.reactivex.Flowable
          
          class Activity {
            fun something() {
              Flowable.just(1)
                .singleElement()
                .subscribe()
            }
          }
        """))
        .detector(RxJavaToSingleDetector())
        .issues(RxJavaToSingleDetector.ISSUE)
        .run()
        .expectWarningCount(1)
  }

  @Test
  fun flowable_first_clean() {
    lint()
        .files(rxjava2(), kotlinSource("""
          package foo
          
          import io.reactivex.Flowable
          
          class Activity {
            fun something() {
              Flowable.just(1)
                .first(1)
                .subscribe()
            }
          }
        """))
        .detector(RxJavaToSingleDetector())
        .issues(RxJavaToSingleDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun maybe_toSingle_warn() {
    lint()
        .files(rxjava2(), kotlinSource("""
          package foo
          
          import io.reactivex.Maybe
          
          class Activity {
            fun something() {
              Maybe.just(1)
                .toSingle()
                .subscribe()
            }
          }
        """))
        .detector(RxJavaToSingleDetector())
        .issues(RxJavaToSingleDetector.ISSUE)
        .run()
        .expectWarningCount(1)
  }
}

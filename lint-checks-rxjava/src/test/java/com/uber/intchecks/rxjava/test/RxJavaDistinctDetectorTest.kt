package com.uber.intchecks.rxjava.test

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.uber.lintchecks.base.test.LintTestBase
import com.uber.lintchecks.rxjava.RxJavaDistinctDetector
import org.junit.Test

class RxJavaDistinctDetectorTest: LintTestBase() {

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
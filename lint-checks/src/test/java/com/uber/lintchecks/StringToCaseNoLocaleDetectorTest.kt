package com.uber.lintchecks

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.uber.lintchecks.base.test.LintTestBase
import org.junit.Test

class StringToCaseNoLocaleDetectorTest: LintTestBase() {

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
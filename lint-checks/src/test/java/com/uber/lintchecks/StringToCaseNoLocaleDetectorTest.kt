package com.uber.lintchecks

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.uber.lintchecks.base.test.LintTestBase
import org.junit.Test

class StringToCaseNoLocaleDetector: LintTestBase() {
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
  }
}
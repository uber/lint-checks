package com.uber.lintchecks.android

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.uber.lintchecks.base.test.LintTestBase
import org.junit.Test

class ResCompatGetColorDetectorTest: LintTestBase() {
  override val testResourcesPath: String
    get() = ""

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
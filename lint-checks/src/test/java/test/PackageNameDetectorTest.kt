package test

import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.uber.lintchecks.PackageNameDetector
import org.junit.Test

class PackageNameDetectorTest : UberLintTestBase() {

  override val testResourcesPath = "sample"

  @Test
  fun testDetect_whenCorrectPackageName_noWarning() {
    TestLintTask.lint()
        .files(java("right_pack/CorrectPackageName.java"))
        .detector(PackageNameDetector())
        .issues(PackageNameDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test
  fun testDetect_whenPeriodInPackageName_shouldRaiseWarning() {
    TestLintTask.lint()
        .files(java("wrong.pack/PeriodInPackageName.java"))
        .detector(PackageNameDetector())
        .issues(PackageNameDetector.ISSUE)
        .run()
        .expectErrorCount(1)
        .expectMatches("A package name/file wrong.pack of a class has a period")
  }
}

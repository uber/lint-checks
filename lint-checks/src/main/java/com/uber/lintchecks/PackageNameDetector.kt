package com.uber.lintchecks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UFile

/** Custom lint rule to prevent usages of improper package names.  */
class PackageNameDetector : Detector(), SourceCodeScanner {

  override fun getApplicableUastTypes() = listOf<Class<out UElement>>(UFile::class.java)

  override fun createUastHandler(context: JavaContext): UElementHandler? {
    val filePath = context.file.toRelativeString(context.project.dir).replace("../", "", false)
    val dotCount = filePath.length - filePath.replace(".", "").length
    return object : UElementHandler() {
      override fun visitFile(node: UFile) {
        // There is a period in the file wrong.pack before the extension, meaning that the package name
        // has a
        // period in it
        if (dotCount > 1) {
          context.report(ISSUE, node, context.getLocation(node), MESSAGE_LINT_ERROR_EXPLANATION)
        }
      }
    }
  }

  companion object {
    const val MESSAGE_LINT_ERROR_TITLE = "Package name/file wrong.pack has a period in it"
    const val MESSAGE_LINT_ERROR_EXPLANATION = "A package name/file wrong.pack of a class has a period in it. This is not allowed, as it will break Herald rules (such as https://code.uberinternal.com/H5265)."
    const val ISSUE_ID = "PackageNameDetector"

    @JvmField
    val ISSUE = Issue.create(
        ISSUE_ID,
        MESSAGE_LINT_ERROR_TITLE,
        MESSAGE_LINT_ERROR_EXPLANATION,
        Category.CORRECTNESS,
        6,
        Severity.ERROR,
        createImplementation<PackageNameDetector>())
  }
}

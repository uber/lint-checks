package com.uber.lintchecks.android

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.ResourceXmlDetector
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.XmlContext
import com.android.tools.lint.detector.api.XmlScannerConstants
import com.uber.lintchecks.android.UiDetectorUtils.isInVectorDrawable
import org.w3c.dom.Attr
import java.io.File

/**
 * Custom lint check to make sure we aren't using color resources and use theme attributes instead.
 */
class ColorResourceUsageDetector : ResourceXmlDetector() {

  companion object {
    const val ISSUE_ID = "ColorResourceUsage"
    val LINT_ERROR_MESSAGE = """
                You should use theme attributes that refer to color resources instead of directly
                using color resources. But, using theme attributes in non-vector drawables causes
                crashes on 4.x devices. In these cases, you should have one copy of your drawable
                that directly uses color resources in the drawable folder, and another copy in the
                drawable-v21 folder that uses a theme attribute.
                """.trimIndent().replace('\n', ' ')
    @JvmField
    val ISSUE = Issue.create(
        ISSUE_ID,
        "Use theme attributes that refer to colors instead of color resources directly.",
        LINT_ERROR_MESSAGE,
        Category.CORRECTNESS,
        6,
        Severity.ERROR,
        createImplementation<ColorResourceUsageDetector>())

    private const val LOLLIPOP = 21
    private const val COLOR = "color"
    private const val DRAWABLE = "drawable"
    private const val LAYOUT = "layout"
    private const val PREFIX_COLOR_RESOURCE = "@color/"
    private const val PREFIX_THEME_ATTR = "?"
    private const val QUALIFIER_V21 = "-v21"
    private const val FILE_EXT_XML = ".xml"
    private const val VECTOR = "vector"
    private val COLOR_ATTRIBUTES = setOf(
        "android:background",
        "android:color",
        "android:fillColor",
        "android:strokeColor",
        "android:textColor",
        "android:startColor",
        "android:endColor")
  }

  override fun appliesTo(folderType: ResourceFolderType) =
      folderType == ResourceFolderType.DRAWABLE || folderType == ResourceFolderType.LAYOUT

  override fun getApplicableAttributes() = XmlScannerConstants.ALL

  override fun visitAttribute(context: XmlContext, attribute: Attr) {
    if (COLOR_ATTRIBUTES.contains(attribute.name)) {
      attribute.value?.let { attributeVal ->
        if (attributeVal.startsWith(PREFIX_COLOR_RESOURCE)) {
          if (isInResFolder(context, LAYOUT) && !isColorSelector(context, attributeVal)) {
            context.report(ISSUE, context.getLocation(attribute), LINT_ERROR_MESSAGE)
          } else if (isInVectorDrawable(attribute)) {
            context.report(ISSUE, context.getLocation(attribute), LINT_ERROR_MESSAGE)
          } else if (isInResFolder(context, DRAWABLE) &&
              !hasQualifiedVersion(context, DRAWABLE, QUALIFIER_V21) &&
              !isColorSelector(context, attributeVal)) {
            context.report(ISSUE, context.getLocation(attribute), LINT_ERROR_MESSAGE)
          }
        } else if (attributeVal.startsWith(PREFIX_THEME_ATTR) &&
            isInResFolder(context, DRAWABLE) &&
            !isInVectorDrawable(attribute) &&
            !isAtLeastV21QualifiedVersion(context) &&
            context.project.minSdk < LOLLIPOP) {
          context.report(ISSUE, context.getLocation(attribute), LINT_ERROR_MESSAGE)
        }
      }
    }
  }

  // Theme attrs in drawables are not supported on 4.x, so allow the usage if there is a -v21 version of the file
  private fun hasQualifiedVersion(context: XmlContext, resFolderName: String, qualifier: String): Boolean {
    if (isInResFolder(context, resFolderName)) {
      context.file.absolutePath?.let {
        val filePath = it.replace(
            File.separator + resFolderName + File.separator,
            File.separator + resFolderName + qualifier + File.separator)
        return File(filePath).exists()
      }
    }
    return false
  }

  // If referring to a color selector from a file in src/res/color, then allow the res usage
  private fun isColorSelector(context: XmlContext?, colorResName: String): Boolean {
    return context?.file?.let { file ->
      val unprefixedColorResName = colorResName.replace(PREFIX_COLOR_RESOURCE, "")
      val resFolder = file.parentFile.parentFile.absolutePath
      val filePath = listOf(resFolder, COLOR, unprefixedColorResName + FILE_EXT_XML).joinToString(separator = File.separator)
      return@let File(filePath).exists()
    } ?: false
  }

  private fun isAtLeastV21QualifiedVersion(context: XmlContext?): Boolean {
    val drawablePath = context?.file?.absolutePath.orEmpty()
    return "/$DRAWABLE-v" in drawablePath &&
        drawablePath.substringAfter("/$DRAWABLE-v").substringBefore("/").toInt() >= LOLLIPOP
  }

  private fun isInResFolder(context: XmlContext?, resFolderName: String): Boolean {
    return context?.file?.path?.contains(File.separator + resFolderName) ?: false
  }
}

package com.uber.lintchecks.android

import org.w3c.dom.Attr
import org.w3c.dom.Element
import org.w3c.dom.Node

/**
 * Utils regarding vector drawables.
 */
object UiDetectorUtils {
  const val VECTOR = "vector"

  fun isInVectorDrawable(attribute: Attr) = isInVectorDrawable(attribute.ownerElement)

  fun isInVectorDrawable(element: Element): Boolean {
    var node: Node? = element
    while (node != null) {
      if (node.nodeName == VECTOR) {
        return true
      } else {
        node = node.parentNode
      }
    }
    return false
  }
}

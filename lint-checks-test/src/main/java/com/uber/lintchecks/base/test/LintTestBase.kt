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
package com.uber.lintchecks.base.test

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles
import com.google.common.base.Charsets
import com.google.common.io.Resources
import junit.framework.TestCase.fail
import org.intellij.lang.annotations.Language
import java.io.File
import java.io.IOException

abstract class LintTestBase {

  protected abstract val testResourcesPath: String

  @Language("JAVA")
  protected fun relativeJavaTestResource(path: String) = relativeTestResource(path)

  @Language("XML")
  protected fun relativeXmlTestResource(path: String) = relativeTestResource(path)

  private fun relativeTestResource(path: String): String {
    try {
      return Resources.toString(Resources.getResource(testResourcesPath + File.separator + path), Charsets.UTF_8)
    } catch (e: IOException) {
      val errorMessage = "Could not find file $path"
      fail(errorMessage)
      throw AssertionError(errorMessage)
    }
  }

  @Language("JAVA")
  protected fun javaTestResource(path: String) = testResource(path)

  @Language("XML")
  protected fun xmlTestResource(path: String) = testResource(path)

  private fun testResource(path: String): String {
    try {
      return Resources.toString(Resources.getResource(path), Charsets.UTF_8)
    } catch (e: IOException) {
      val errorMessage = "Could not find file $path"
      fail(errorMessage)
      throw AssertionError(errorMessage)
    }
  }

  /** @return a Java [TestFile] with indention. */
  protected fun javaSource(@Language("java") source: String): TestFile {
    return TestFiles.java(source).indented()
  }

  /** @return a Kotlin [TestFile] with indention. */
  protected fun kotlinSource(@Language("kotlin") source: String): TestFile {
    return TestFiles.kotlin(source).indented()
  }

  /** @return an XML [TestFile] with indention. */
  protected fun xmlSource(fileName: String, @Language("xml") source: String): TestFile {
    return TestFiles.xml(fileName, source).indented()
  }

  protected fun java(relativeFilePath: String): TestFile {
    return TestFiles.java("src/$relativeFilePath", relativeJavaTestResource(relativeFilePath))
  }

  protected fun xml(relativeFilePath: String): TestFile {
    return TestFiles.xml(relativeFilePath, relativeXmlTestResource(relativeFilePath))
  }
}

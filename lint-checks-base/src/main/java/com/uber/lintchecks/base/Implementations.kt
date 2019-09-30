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
@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.uber.lintchecks.android

import com.android.tools.lint.detector.api.BinaryResourceScanner
import com.android.tools.lint.detector.api.ClassScanner
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.FileScanner
import com.android.tools.lint.detector.api.GradleScanner
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.OtherFileScanner
import com.android.tools.lint.detector.api.ResourceFolderScanner
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.XmlScanner
import java.util.EnumSet
import kotlin.reflect.KClass

/**
 * Creates a new [Implementation] with standard scopes set.
 *
 * @param scope the target scope. Default will automatically resolve based on the implementation of the detector.
 * @param enableInTests whether or not to enable this lint in tests. Default is to enable for [Scope.JAVA_FILE] only.
 */
inline fun <reified T> createImplementation(
  scope: Scope = T::class.resolveScope(),
  enableInTests: Boolean = scope == Scope.JAVA_FILE
): Implementation where T : Detector, T : FileScanner {
  return createImplementation(T::class.java, scope, enableInTests)
}

/**
 * Creates a new [Implementation] with standard scopes set for a resource folder scope. This is special cased from
 * the other detectors because the [ResourceFolderScanner] interface is a special case.
 */
inline fun <reified T> createResourceFolderImplementation(): Implementation where T : Detector, T : ResourceFolderScanner {
  return createImplementation(T::class.java, Scope.RESOURCE_FOLDER, false)
}

/**
 * Creates a new [Implementation] with standard scopes set.
 *
 * @param scope the target scope. Default will automatically resolve based on the implementation of the detector.
 * @param enableInTests whether or not to enable this lint in tests. Default is to enable for [Scope.JAVA_FILE] only.
 */
inline fun createImplementation(
  clazz: Class<out Detector>,
  scope: Scope,
  enableInTests: Boolean
): Implementation {
  // We use the overloaded constructor that takes a varargs of `Scope` as the last param.
  // This is to enable on-the-fly IDE checks. We are telling lint to run on both
  // `scope` and TEST_SOURCES in the `scope` parameter but by providing the `analysisScopes`
  // params, we're indicating that this check can run on either `scope` or TEST_SOURCES and
  // doesn't require both of them together.
  // From discussion on lint-dev https://groups.google.com/d/msg/lint-dev/ULQMzW1ZlP0/1dG4Vj3-AQAJ
  // TODO: Remove after AGP 3.4 release when this behavior will no longer be required.
  val (primaryScope, varargScopes) = if (enableInTests) {
    EnumSet.of(scope, Scope.TEST_SOURCES) to arrayOf(EnumSet.of(scope), EnumSet.of(Scope.TEST_SOURCES))
  } else {
    EnumSet.of(scope) to arrayOf<EnumSet<Scope>>()
  }
  return Implementation(clazz,
      primaryScope,
      *varargScopes
  )
}

inline fun <T> KClass<T>.resolveScope(): Scope where T : Detector, T : FileScanner {
  return java.let { clazz ->
    when {
      SourceCodeScanner::class.java.isAssignableFrom(clazz) -> Scope.JAVA_FILE
      ClassScanner::class.java.isAssignableFrom(clazz) -> Scope.CLASS_FILE
      BinaryResourceScanner::class.java.isAssignableFrom(clazz) -> Scope.BINARY_RESOURCE_FILE
      XmlScanner::class.java.isAssignableFrom(clazz) -> Scope.RESOURCE_FILE
      GradleScanner::class.java.isAssignableFrom(clazz) -> Scope.GRADLE_FILE
      OtherFileScanner::class.java.isAssignableFrom(clazz) -> Scope.OTHER
      else -> TODO("Unsupported detector type! ${clazz.name}")
    }
  }
}

package com.uber.lintchecks.android

import com.android.tools.lint.checks.infrastructure.TestFiles

fun Any.appcompat() = TestFiles.bytes("libs/core-1.1.0.jar", javaClass.getResourceAsStream("/core-1.1.0.jar").readBytes())
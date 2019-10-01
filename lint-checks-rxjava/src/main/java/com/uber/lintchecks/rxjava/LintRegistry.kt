package com.uber.lintchecks.rxjava

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue
import com.google.auto.service.AutoService

@AutoService(IssueRegistry::class)
class LintRegistry: IssueRegistry() {
  override val issues: List<Issue> = listOf(
      RxJavaDistinctDetector.ISSUE
  )
}

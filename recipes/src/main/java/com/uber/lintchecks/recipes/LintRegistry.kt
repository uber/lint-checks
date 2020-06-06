/*
 * Copyright (C) 2020. Uber Technologies
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
package com.uber.lintchecks.recipes

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.google.auto.service.AutoService
import com.uber.lintchecks.recipes.guardrails.JavaOnlyDetector

@AutoService(IssueRegistry::class)
class LintRegistry : IssueRegistry() {
  override val issues: List<Issue> = listOf(
      JavaOnlyDetector.ISSUE
  )

  override val api: Int = CURRENT_API
}

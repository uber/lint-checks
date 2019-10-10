# Lint Checks 

![Build](https://github.com/uber/lint-checks/workflows/CI/badge.svg)

## Motivation

As a codebase increases in size and complexity, static analysis checks become a very useful tool in ensuring high quality, bug free code. This repository provides a collection of lint checks based on Android Lint that guard against common pitfalls and bugs that you might encounter in day to day development.

## Download

Java/Kotlin Lint Checks

```groovy
lintChecks "com.uber.lint-checks:lint-checks:x.y.z
```

Android Lint Checks

```groovy
lintChecks "com.uber.lint-checks:lint-checks-android:x.y.z
```
RxJava Lint Checks

```groovy
lintChecks "com.uber.lint-checks:lint-checks-rxjava:x.y.z
```

## License

    Copyright (C) 2019 Uber Technologies

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


# Getting Started

To add lint-checks to your project, simply add the relevant artifact in your app/module's `build.gradle` file.
```groovy
dependencies {
  lintChecks "com.uber.lint-checks:lint-checks:x.y.z
}
```

## Lint Modules

Here's a list of lint modules that this project provides:

#### Java/Kotlin Lint Checks
```groovy
lintChecks "com.uber.lint-checks:lint-checks:x.y.z
```
#### Android Lint Checks
```groovy
lintChecks "com.uber.lint-checks:lint-checks-android:x.y.z
```
#### RxJava Lint Checks
```groovy
lintChecks "com.uber.lint-checks:lint-checks-rxjava:x.y.z
```
The list of individual lint checks in each module can be found [here](https://uber.github.io/lint-checks/CHECKS/)

## Integrating In An Existing Codebase

Integrating new tooling into an existing codebase can be a pain. Luckily, Android Lint makes it easy to start using lint by providing a [baseline](https://developer.android.com/studio/write/lint#snapshot). A baseline lets you take a snapshot of your project and then uses the snapshot as a baseline for future inspection runs. 

To create a baseline add the following in your `build.gradle` file.
```groovy
android {
  lintOptions {
    baseline file("lint-baseline.xml")
  }
}
```
More info on baselines can be found in the [official documentation](https://developer.android.com/studio/write/lint#snapshot)

## Enabling/Disabling Lint Checks

There are cases when a particular lint check is not applicable to your codebase or when you want to _enable_ a lint check that's disabled by default. 

You can use Android Lint's `lintOptions` to customize your own setup. 

```groovy
android {
  lintOptions {
    // Turns off checks for the issue IDs you specify.
    disable 'RxJavaDistinct'
    // Turns on checks for the issue IDs you specify. These checks are in
    // addition to the default lint checks.
    enable 'FrameworkPair'
  }
}
```

For more info on lintOptions, take a look at the [official documentation](https://developer.android.com/studio/write/lint)


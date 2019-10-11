# Checks

This is an exhaustive list of the lint checks packaged in each module. 

## lint-checks

| Issue | Description | Severity | Enabled |
| ----- | ----------- | -------- | ------- |
| StringFormatNoLocale | String.format, when used without a locale can cause crashes when the input text doesn't match the user's locale. Pass a locale to prevent this ambiguity. | ERROR | true |
| StringToCaseNoLocale | Calling `String.toLowerCase()` or `toUpperCase()` without specifying an explicit locale is a common source of bugs. The reason for that is that those methods will use the current locale on the user's device, and even though the code appears to work correctly when you are developing the app, it will fail in some locales. For example, in the Turkish locale, the uppercase replacement for i is not I. If you want the methods to just perform ASCII replacement, for example to convert an enum name, call `String.toUpperCase(Locale.US)` instead. If you really want to use the current locale, call `String.toUpperCase(Locale.getDefault())` instead. | ERROR | true |

## lint-checks-android

| Issue | Description | Severity | Enabled |
| ----- | ----------- | -------- | ------- |
| SrcCompatUsage | Use `app:srcCompat` instead of `android:src` for vector drawable compatibility. | ERROR | true |
| FrameworkPair | The framework Pair class implementation has bugs on older versions of Android. You should use the support version instead (`androidx.core.util.Pair`). | ERROR | false |
| HardcodedValueInXML | It's generally good practice not to hardcoded colors and instead use theme attributes. This allows for easy refactoring, multi-theme support and makes sure there aren't too many shades of the same color. Dimensions should use theme attributes or (if they're specific to a feature) local resource references. | ERROR | true |
| ColorResourceUsage | You should use theme attributes that refer to color resources instead of directly using color resources. But, using theme attributes in non-vector drawables causes crashes on 4.x devices. In these cases, you should have one copy of your drawable that directly uses color resources in the drawable folder, and another copy in the drawable-v21 folder that uses a theme attribute. | ERROR | true |
| GetDrawable | Don't use ContextCompat#getDrawable(Context,int), instead use AppCompatResources#getDrawable(Context, int) since it understands how to process vector drawables | ERROR | true |
| ResCompatGetColorUsage | Don't use ResourcesCompat#getColor(Resources,int,Theme), instead use ContextCompat.getColor(Context,int) | ERROR | true |

## lint-checks-rxjava
| Issue | Description | Severity | Enabled |
| ----- | ----------- | -------- | ------- |
| RxJavaDistinct | distinct() works by holding all previous values in memory and only can be used with a bounded observable. In most cases, distinctUntilChanged() works since it only compares against the last emitted item instead of all items. | WARNING | true |
| RxJavaToSingle | single(), singleOrError() and singleElement() will emit an error if there is more than 1 element in the stream. On top of that, single() will not actually emit the element until the stream is completed. This issue is usually mitigated by adding take(1), however this is error prone as developers might forget to do that and the code will still compile and possibly even run without issues for some time. This issue could be easily avoided by using first(), firstOrError() or firstElement() instead. Maybe.toSingle() is disregarding the core idea of Maybe - that the stream can complete without emitting any values. Calling toSingle() on such stream would emit an error. | WARNING | true |

# MoJcampConverter
**MoJcampConverter** is a open-source package to convert JCAMP-DX files to spectra.

[![](https://jitpack.io/v/baolanlequang/jcamp-converter-android.svg)](https://jitpack.io/#baolanlequang/jcamp-converter-android)

## How to user MoJcampConverter
**MoJcampConverter** is released as dependency package on [jitpack](https://jitpack.io). 

### 1. Add *MoJcampConverter* to your project
Add `jitpack` in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```


### 2. Add the dependency
```
dependencies {
        implementation 'com.github.baolanlequang:jcamp-converter-android:0.0.7'
}
```

### 3. Using the converter
```kotlin
val input = assets.open("testdata/test_file_20.dx")

val reader = JcampReader(input)
val jcamp = reader.jcamp
if (jcamp != null) {
    for (spec in jcamp.spectra) {
        Log.d(TAG, spec.xValues.size.toString())
    }
}
else {
    Log.d(TAG, "cannot read jcamp")
}

```

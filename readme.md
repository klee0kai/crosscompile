## Cross Compiling

[![](https://img.shields.io/badge/license-GNU_GPLv3-blue.svg?style=flat-square)](./LICENSE)
[![](https://jitpack.io/v/klee0kai/crosscompile.svg)](https://jitpack.io/#klee0kai/crosscompile)

Build you own C/C++ project for each arch.
We're looking for toolchains, your only select which one need to run.

## Usage

Configure classpath in project's `build.gradle`:

```kotlin
buildscript {
    repositories {
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath("com.github.klee0kai:crosscompile:0.0.1")
    }
}
```

Apply plugin in your module's `build.gradle`:

```kotlin
crosscompile {

    bashBuild("toybox", "android_arm7a") {
        container {
            sh("git clone --branch android-13.0.0_r1 https://android.googlesource.com/platform/external/toybox")
            container {
                workFolder = "./toybox"
                use(findAndroidNdk())
                use(android_i686(30))

                configureAutomake("./configure")
                sh("make")
            }
        }
    }
}
```

Build you lib

```bash
./gradlew assemble
```


## License

```
Copyright (c) 2023 Andrey Kuzubov
```


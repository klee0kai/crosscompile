package com.github.klee0kai.androidnative.env

import com.android.build.gradle.internal.SdkLocator
import com.github.klee0kai.androidnative.utils.EmptyIssueReporter
import org.gradle.api.Project
import java.io.File

fun Project.guessAndroidSdk(): String? {
    listOf(
        readProperty("sdk.dir"),
        //https://android.googlesource.com/platform/tools/build/+/d69964104aed4cfae5052028b5c5e57580441ae8/gradle/src/main/groovy/com/android/build/gradle/internal/Sdk.groovy
        readProperty("android.dir"),
        System.getenv()["ANDROID_SDK"],
        System.getenv()["ANDROID_HOME"],
        System.getProperty("android.home"),
        System.getProperty("android.dir"),
    ).firstOrNull {
        it != null && File(it).exists()
    }?.let {
        return it;
    }

    return SdkLocator.getSdkDirectory(project.rootDir, EmptyIssueReporter).absolutePath
}
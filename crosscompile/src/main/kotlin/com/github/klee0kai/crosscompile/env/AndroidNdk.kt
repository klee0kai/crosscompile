package com.github.klee0kai.crosscompile.env

import com.android.build.gradle.internal.SdkLocator
import com.github.klee0kai.crosscompile.toolchain.AndroidNdkToolchain
import com.github.klee0kai.crosscompile.utils.EmptyIssueReporter
import com.github.klee0kai.crosscompile.utils.pathPlus
import org.gradle.api.Project
import java.io.File

fun Project.findAndroidNdk(
    androidSdk: String? = guessAndroidSdk(),
    androidNdk: String? = guessAndroidNdk(androidSdk),
) = AndroidNdkToolchain(
    sdkPath = androidSdk,
    ndkPath = androidNdk,
)


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

fun Project.guessAndroidNdk(
    androidSdk: String? = guessAndroidSdk()
): String? {
    return listOf(
        androidSdk?.pathPlus("ndk")?.pathPlus(readProperty("android.ndkVersion")),
        readProperty("ndk.dir"),
        System.getenv()["ANDROID_NDK"],
        System.getenv()["NDK_ROOT"],
        System.getenv()["ANDROID_NDK_ROOT"],
        System.getenv()["ANDROID_NDK_HOME"],
        androidSdk?.pathPlus("ndk")?.pathPlus(System.getenv()["ANDROID_NDK_VERSION"]),
        androidSdk?.pathPlus("ndk")?.let { File(it).listFiles()?.firstOrNull()?.absolutePath },
    ).firstOrNull {
        it != null && File(it).exists()
    }
}



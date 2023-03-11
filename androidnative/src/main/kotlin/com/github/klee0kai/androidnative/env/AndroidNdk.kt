package com.github.klee0kai.androidnative.env

import com.github.klee0kai.androidnative.utils.pathPlus
import org.gradle.api.Project
import java.io.File

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

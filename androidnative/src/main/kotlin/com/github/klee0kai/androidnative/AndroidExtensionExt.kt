package com.github.klee0kai.androidnative

import com.github.klee0kai.androidnative.toolchain.IToolchain


private fun AndroidNativeExtension.android_arch(arch: String, compileSdk: Int? = null): IToolchain {
    val compileSdkPostfix = compileSdk?.toString() ?: ""
    val compileSdkPostfix2 = "eabi${compileSdkPostfix}"
    return toolchains.find {
        val isTheToolchain = it.name.startsWith("${arch}-")
                && (it.name.endsWith(compileSdkPostfix) || it.name.endsWith(compileSdkPostfix2)
                )
        isTheToolchain
    } ?: error("Can't find android ${arch} - $compileSdkPostfix toolchain. Please check your sdk manager")
}

fun AndroidNativeExtension.android_arm(compileSdk: Int? = null): IToolchain =
    android_arch("armv", compileSdk)

fun AndroidNativeExtension.android_arm7a(compileSdk: Int? = null): IToolchain =
    android_arch("armv7a", compileSdk)


fun AndroidNativeExtension.android_aarch64(compileSdk: Int? = null): IToolchain =
    android_arch("aarch64", compileSdk)


fun AndroidNativeExtension.android_i686(compileSdk: Int? = null): IToolchain =
    android_arch("i686", compileSdk)


fun AndroidNativeExtension.android_x86_64(compileSdk: Int? = null): IToolchain =
    android_arch("x86_64", compileSdk)
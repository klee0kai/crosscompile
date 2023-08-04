package com.github.klee0kai.crosscompile

import com.github.klee0kai.crosscompile.toolchain.Toolchain


private fun CrossCompileExtension.android_arch(arch: String, compileSdk: Int? = null): Toolchain {
    val compileSdkPostfix = compileSdk?.toString() ?: ""
    val compileSdkPostfix2 = "eabi${compileSdkPostfix}"
    return toolchains.find {
        val isTheToolchain = it.name.startsWith("${arch}-")
                && (it.name.endsWith(compileSdkPostfix) || it.name.endsWith(compileSdkPostfix2)
                )
        isTheToolchain
    } ?: error("Can't find android ${arch} - $compileSdkPostfix toolchain. Please check your sdk manager")
}

fun CrossCompileExtension.android_arm(compileSdk: Int? = null): Toolchain =
    android_arch("armv", compileSdk)

fun CrossCompileExtension.android_arm7a(compileSdk: Int? = null): Toolchain =
    android_arch("armv7a", compileSdk)


fun CrossCompileExtension.android_aarch64(compileSdk: Int? = null): Toolchain =
    android_arch("aarch64", compileSdk)


fun CrossCompileExtension.android_i686(compileSdk: Int? = null): Toolchain =
    android_arch("i686", compileSdk)


fun CrossCompileExtension.android_x86_64(compileSdk: Int? = null): Toolchain =
    android_arch("x86_64", compileSdk)
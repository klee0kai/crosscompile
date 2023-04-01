package com.github.klee0kai.crosscompile.toolchain

import com.github.klee0kai.crosscompile.bashtask.IEnvContainer

open class AndroidNdk(
    val sdkPath: String?,
    val ndkPath: String?,
) {


    fun apply(envContainer: IEnvContainer) = envContainer.run {
        env["ANDROID_SDK"] = sdkPath
        env["ANDROID_SDK_ROOT"] = sdkPath

        env["ANDROID_NDK"] = ndkPath
        env["ANDROID_NDK_ROOT"] = ndkPath
        env["ANDROID_NDK_HOME"] = ndkPath
    }

}

package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.bashtask.IEnvContainer

class AndroidLLVMToolchain(
    val sdkPath: String?,
    val ndkPath: String?,

    val toolchain: IToolchain,
) : IToolchain by toolchain {


    override fun applyAutoToolConf(envContainer: IEnvContainer) {
        toolchain.applyAutoToolConf(envContainer)
        envContainer.runWrapper.run {

            env("ANDROID_SDK", sdkPath)
            env("ANDROID_SDK_ROOT", sdkPath)

            env("ANDROID_NDK", ndkPath)
            env("ANDROID_NDK_ROOT", ndkPath)
            env("ANDROID_NDK_HOME", ndkPath)
        }
    }

}
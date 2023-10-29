package com.github.klee0kai.crosscompile.toolchain2.applicable

import com.github.klee0kai.crosscompile.bashtask.EnvContainer
import com.github.klee0kai.crosscompile.toolchain2.Toolchain

interface ApplicableToolchain : Toolchain {

    fun automakeConf(envContainer: EnvContainer) {
        throw IllegalAccessError("autoMake not support in ${name}")
    }

    fun cmakeConf(envContainer: EnvContainer) {
        throw IllegalAccessError("cmake not support in ${name}")
    }

}
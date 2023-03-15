package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.bashtask.IEnvContainer

interface IToolchain {

    val name: String
    val path: String?

    fun applyBinAppAlias(envContainer: IEnvContainer) = Unit

    fun applyAutoToolConf(envContainer: IEnvContainer) = Unit


}




package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.bashtask.IEnvContainer

interface IToolchain {

    val name: String
    val path: String?


    /**
     *  Env for [automake](https://www.gnu.org/software/automake/)
     */
    fun automakeConf(envContainer: IEnvContainer) = Unit


}




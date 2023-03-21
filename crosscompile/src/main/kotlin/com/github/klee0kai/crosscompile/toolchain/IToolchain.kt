package com.github.klee0kai.crosscompile.toolchain

import com.github.klee0kai.crosscompile.bashtask.IEnvContainer

interface IToolchain {

    val name: String
    val path: String?


    /**
     *  Env for [automake](https://www.gnu.org/software/automake/)
     */
    fun automakeConf(envContainer: IEnvContainer) = Unit


}




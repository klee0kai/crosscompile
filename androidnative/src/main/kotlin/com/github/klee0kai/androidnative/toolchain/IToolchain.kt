package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.script.RunWrapper

interface IToolchain {

    val name: String
    val path: String?

    val runWrapper: RunWrapper

    fun genWrapperIfNeed()

}




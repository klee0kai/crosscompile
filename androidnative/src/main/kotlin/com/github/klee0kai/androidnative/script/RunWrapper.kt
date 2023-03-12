package com.github.klee0kai.androidnative.script

import java.io.File

interface RunWrapper {

    val runWrapperPath: File?

    fun alias(name: String, alias: String?)

    fun gen()

}
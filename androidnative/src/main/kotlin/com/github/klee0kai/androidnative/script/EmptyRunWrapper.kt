package com.github.klee0kai.androidnative.script

import java.io.File

object EmptyRunWrapper : RunWrapper {
    override val runWrapperPath: File? = null

    override fun alias(name: String, alias: String?) = Unit

    override fun gen() = Unit
}
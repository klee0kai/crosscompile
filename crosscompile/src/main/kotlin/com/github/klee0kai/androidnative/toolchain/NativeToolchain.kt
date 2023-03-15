package com.github.klee0kai.androidnative.toolchain

/**
 * Native toolchain:
 *  build = host = target
 *
 * No any overrides
 */
object NativeToolchain : IToolchain {
    override val name: String = "cur_os"
    override val path: String? = null


    override fun toString(): String {
        return "current os toolchain: no overrides"
    }

}




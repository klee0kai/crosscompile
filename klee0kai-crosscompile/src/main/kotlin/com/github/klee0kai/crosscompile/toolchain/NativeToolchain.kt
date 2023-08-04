package com.github.klee0kai.crosscompile.toolchain

/**
 * Native toolchain:
 *  build = host = target
 *
 * No have any overrides
 */
object NativeToolchain : Toolchain {
    override val name: String = "cur_os"
    override val path: String? = null


    override fun toString(): String {
        return "current os toolchain: no overrides"
    }

}




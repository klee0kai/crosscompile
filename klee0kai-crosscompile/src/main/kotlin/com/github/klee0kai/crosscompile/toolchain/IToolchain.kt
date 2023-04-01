package com.github.klee0kai.crosscompile.toolchain

import com.github.klee0kai.crosscompile.bashtask.IEnvContainer

interface IToolchain {

    val name: String
    val path: String?

    val nameHelper
        get() = ToolchainNaming(name)

    /**
     *  Env for [automake](https://www.gnu.org/software/automake/)
     */
    fun automakeConf(envContainer: IEnvContainer) = Unit

}

data class ToolchainNaming(
    val name: String,

    /**
     * available prefixs for bin apps. for example:
     *
     *  clang -> armv7a-linux-androideabi16-clang - prefix is  armv7a-linux-androideabi16-
     */
    val prefixes: List<String> = emptyList(),
) {
    val splitted
        get() = name.split("-")

    val targetArch: String?
        get() = splitted.getOrNull(0)

    val host: String?
        get() = splitted.getOrNull(1)

    val target: String?
        get() = splitted.getOrNull(2)

    val targetAbi: Int?
        get() = target
            ?.indexOfLast { !it.isDigit() }
            ?.let { index ->
                target?.substring(index + 1)?.toIntOrNull()
            }

}

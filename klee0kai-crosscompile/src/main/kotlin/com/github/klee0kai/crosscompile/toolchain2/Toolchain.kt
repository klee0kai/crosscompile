package com.github.klee0kai.crosscompile.toolchain2

import com.github.klee0kai.crosscompile.toolchain.ToolchainNaming

interface Toolchain {

    val name: String
    val labels: Set<ToolchainLabel>
    val path: String?
    val details: String?

    val nameHelper
        get() = ToolchainNaming(name)

}
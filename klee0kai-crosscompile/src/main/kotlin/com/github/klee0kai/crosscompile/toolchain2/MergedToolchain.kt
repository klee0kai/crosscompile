package com.github.klee0kai.crosscompile.toolchain2

import com.github.klee0kai.crosscompile.toolchain2.applicable.ApplicableToolchain

/**
 * Merged toolchain has collection of toolchains same target
 * But could be for difference build systems (cmake/make)
 */
class MergedToolchain(
    val toolchains: List<Toolchain>
) : Toolchain, ApplicableToolchain {

    override val name: String = toolchains.first().name

    override val labels: Set<ToolchainLabel> = toolchains.flatMap { it.labels }.toSet()

    override val path: String? = null

    override val details: String = "$name (${labels.joinToString()})"
}
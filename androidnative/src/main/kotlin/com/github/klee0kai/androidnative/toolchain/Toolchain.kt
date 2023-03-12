package com.github.klee0kai.androidnative.toolchain

import org.gradle.api.tasks.AbstractExecTask

interface IToolchain {

    val name: String
    fun applyToEnv(task: AbstractExecTask<*>)

}
open class Toolchain(
    val path: String?
)

object CurrentOs : Toolchain(null)

object Androidx86 : Toolchain(null)
object Androidx86_64 : Toolchain(null)
object AndroidArm : Toolchain(null)
object AndroidAarch64 : Toolchain(null)



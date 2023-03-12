package com.github.klee0kai.androidnative.toolchain

import org.gradle.api.tasks.AbstractExecTask

object CurOsToolchain : IToolchain {
    override val name: String
        get() = "cur_os"

    override fun applyToEnv(task: AbstractExecTask<*>) = Unit

}




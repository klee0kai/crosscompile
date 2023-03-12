package com.github.klee0kai.androidnative.toolchain

import org.gradle.api.tasks.AbstractExecTask

interface IToolchain {

    val name: String
    fun applyToEnv(task: AbstractExecTask<*>)

}




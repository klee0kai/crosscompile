package com.github.klee0kai.crosscompile.tasks

import com.github.klee0kai.crosscompile.toolchain.IToolchain
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.HelpTasksPlugin

fun Project.registerToolchainsTask(toolchains: List<IToolchain>): Task {
    return tasks.register("cppToolchains", DefaultTask::class.java) {
        description = "Print all available crosscompile cpp toolchains"
        group = HelpTasksPlugin.HELP_GROUP

        doLast {
            println("Available toolchains:\n")
            toolchains.forEach {
                println(it.toString())
            }
        }
    }.get()
}
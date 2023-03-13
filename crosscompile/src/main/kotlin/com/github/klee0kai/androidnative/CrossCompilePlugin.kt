package com.github.klee0kai.androidnative

import com.github.klee0kai.androidnative.bashtask.BashBuildTask
import com.github.klee0kai.androidnative.env.guessAndroidNdk
import com.github.klee0kai.androidnative.env.guessAndroidSdk
import com.github.klee0kai.androidnative.env.guessJdk
import com.github.klee0kai.androidnative.toolchain.NativeToolchain
import com.github.klee0kai.androidnative.toolchain.findAndroidToolchains
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.create
import org.gradle.language.base.plugins.LifecycleBasePlugin

class CrossCompilePlugin : Plugin<Project> {

    private val taskNames = mutableListOf<String>()


    override fun apply(project: Project) {
        project.pluginManager.apply(LifecycleBasePlugin::class.java)

        val jdkPath = project.guessJdk()
        val androidSdk = project.guessAndroidSdk()
        val androidNdk = project.guessAndroidNdk(androidSdk)
        val toolchains = project.findAndroidToolchains(androidSdk, androidNdk).sortedBy { it.name }

        val assembleTask = project.tasks.getByName(LifecycleBasePlugin.ASSEMBLE_TASK_NAME)
        val exists = mutableMapOf<String, Task>()

        val bashBuild: BashBuildLambda = { name, t, taskBlock ->
            val toolchain = t ?: NativeToolchain

            val taskName = genTaskNameFor("${name}-${toolchain.name}")
            val task = project.tasks.register(taskName, BashBuildTask::class.java, toolchain).get()
                .apply {
                    description = "Build $name library with ${toolchain.name} toolchain"
                    group = LifecycleBasePlugin.BUILD_GROUP
                    assembleTask.dependsOn(this)

                    toolchain.genWrapperIfNeed(project)

                    taskBlock.invoke(this)
                }

            exists.getOrDefault(name, null)?.let { task.mustRunAfter(it) }
            exists[name] = task
            task
        }


        project.tasks.register("toolchains", DefaultTask::class.java) {
            description = "Print all available crosscompile toolchains"
            group = LifecycleBasePlugin.BUILD_GROUP

            doLast {
                println("Available toolchains:\n")
                toolchains.forEach {
                    println(it.toString())
                }
            }
        }

        project.extensions.create<AndroidNativeExtension>("crosscompile", bashBuild, toolchains)
    }


    private fun genTaskNameFor(_name: String): String {
        var name = _name
        val exists = taskNames.filter { it.startsWith(name) }.count()
        if (exists > 0) {
            name = "${name}-${exists}"
        }
        taskNames.add(name)
        return name
    }

}


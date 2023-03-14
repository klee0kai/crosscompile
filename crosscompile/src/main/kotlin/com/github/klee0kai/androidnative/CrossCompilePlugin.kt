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
import org.gradle.api.plugins.HelpTasksPlugin.HELP_GROUP
import org.gradle.kotlin.dsl.create
import org.gradle.language.base.plugins.LifecycleBasePlugin

class CrossCompilePlugin : Plugin<Project> {

    private val taskNames = mutableListOf<String>()

    override fun apply(project: Project) = project.applyOnProject()

    private fun Project.applyOnProject() {
        pluginManager.apply(LifecycleBasePlugin::class.java)

        val jdkPath = guessJdk()
        val androidSdk = guessAndroidSdk()
        val androidNdk = guessAndroidNdk(androidSdk)
        val toolchains = findAndroidToolchains(androidSdk, androidNdk).sortedBy { it.name }

        val assembleTask = tasks.getByName(LifecycleBasePlugin.ASSEMBLE_TASK_NAME)
        val libGroups = mutableMapOf<String, MutableList<Task>>()

        val bashBuild: BashBuildLambda = { name, t, taskBlock ->
            val toolchain = t ?: NativeToolchain

            val taskName = genTaskNameFor("${name}-${toolchain.name}")
            val task = tasks.register(taskName, BashBuildTask::class.java, toolchain).get()
                .apply {
                    description = "Build $name library with ${toolchain.name} toolchain"
                    group = LifecycleBasePlugin.BUILD_GROUP
                    assembleTask.dependsOn(this)

                    toolchain.genWrapperIfNeed(project)

                    taskBlock.invoke(this)
                }

            libGroups.putIfAbsent(name, mutableListOf())
            libGroups[name]?.lastOrNull()?.let { task.mustRunAfter(it) }
            libGroups[name]?.add(task)

            task
        }


        tasks.register("cppToolchains", DefaultTask::class.java) {
            description = "Print all available crosscompile cpp toolchains"
            group = HELP_GROUP

            doLast {
                println("Available toolchains:\n")
                toolchains.forEach {
                    println(it.toString())
                }
            }
        }

        extensions.create<AndroidNativeExtension>("crosscompile", bashBuild, toolchains)

        afterEvaluate {
            libGroups.forEach { lib ->
                tasks.register(lib.key, DefaultTask::class.java) {
                    description = "Build $name library for all arch"
                    group = LifecycleBasePlugin.BUILD_GROUP

                    lib.value.forEach { dependsOn(it) }
                }
            }
        }

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


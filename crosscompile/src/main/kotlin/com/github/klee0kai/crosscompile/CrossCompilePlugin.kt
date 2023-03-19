package com.github.klee0kai.crosscompile

import com.github.klee0kai.crosscompile.bashtask.BashBuildTask
import com.github.klee0kai.crosscompile.env.findAndroidToolchains
import com.github.klee0kai.crosscompile.env.guessAndroidNdk
import com.github.klee0kai.crosscompile.env.guessAndroidSdk
import com.github.klee0kai.crosscompile.env.guessJdk
import com.github.klee0kai.crosscompile.model.TaskName
import com.github.klee0kai.crosscompile.toolchain.IToolchain
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
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
        val libGroups = mutableMapOf<String, MutableList<BashBuildTask>>()

        val bashBuild: com.github.klee0kai.crosscompile.BashBuildLambda = { name, subName, taskBlock ->

            val taskName = genTaskNameFor(if (subName != null) "${name}-${subName}" else name)
            val task = tasks.register(taskName, BashBuildTask::class.java, TaskName(name, subName)).get()
                .apply {
                    group = LifecycleBasePlugin.BUILD_GROUP
                    assembleTask.dependsOn(this)

                    taskBlock.invoke(this)
                }

            libGroups.putIfAbsent(name, mutableListOf())
            libGroups[name]?.lastOrNull()?.let { task.mustRunAfter(it) }
            libGroups[name]?.add(task)

            task
        }

        extensions.create<com.github.klee0kai.crosscompile.CrossCompileExtension>("crosscompile", bashBuild, toolchains)

        afterEvaluate {
            registerToolchainsTask(toolchains)
            libGroups.forEach { lib ->
                lib.value.forEach { it.fillDescIfNull() }
                registerLibAssembleTask(lib.value)
            }
        }

    }

    private fun BashBuildTask.fillDescIfNull() {
        if (description.isNullOrBlank()) {
            description = "Build $name"
        }
    }

    private fun Project.registerLibAssembleTask(libArchTasks: List<BashBuildTask>) {
        val name = libArchTasks.first().groupName

        if (tasks.findByName(name) != null)
            return

        tasks.register(name, DefaultTask::class.java) {
            val forAllArch = if (libArchTasks.size > 1) "for all arch" else ""
            description = "$name $forAllArch"
            group = LifecycleBasePlugin.BUILD_GROUP

            libArchTasks.forEach { dependsOn(it) }
        }
    }

    private fun Project.registerToolchainsTask(toolchains: List<IToolchain>) {
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


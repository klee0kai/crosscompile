package com.github.klee0kai.crosscompile

import com.github.klee0kai.crosscompile.bashtask.BashBuildTask
import com.github.klee0kai.crosscompile.env.findAndroidToolchains
import com.github.klee0kai.crosscompile.env.guessAndroidNdk
import com.github.klee0kai.crosscompile.env.guessAndroidSdk
import com.github.klee0kai.crosscompile.env.guessJdk
import com.github.klee0kai.crosscompile.model.TaskName
import com.github.klee0kai.crosscompile.tasks.dependsOnAssembleTask
import com.github.klee0kai.crosscompile.tasks.registerToolchainsTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.create
import org.gradle.language.base.plugins.LifecycleBasePlugin

open class CrossCompilePlugin : Plugin<Project> {

    private val taskNames = mutableListOf<String>()

    override fun apply(project: Project) = project.applyOnProject()

    private fun Project.applyOnProject() {
        pluginManager.apply(LifecycleBasePlugin::class.java)

        val jdkPath = guessJdk()
        val androidSdk = guessAndroidSdk()
        val androidNdk = guessAndroidNdk(androidSdk)
        val toolchains = findAndroidToolchains(androidSdk, androidNdk).sortedBy { it.name }
        val buildTasks = mutableSetOf<Task>()

        val assembleTask = tasks.getByName(LifecycleBasePlugin.ASSEMBLE_TASK_NAME)

        val bashBuild: BashBuildLambda = { name, subName, taskBlock ->

            val taskName = genTaskNameFor(if (subName != null) "${name}-${subName}" else name)
            val task = tasks.register(taskName, BashBuildTask::class.java, TaskName(name, subName)).get()
                .apply {
                    group = LifecycleBasePlugin.BUILD_GROUP


                    try {
                        taskBlock.invoke(this)
                    } catch (e: Throwable) {
                        configureException = e
                    }
                    buildTasks.add(this)
                }

            val libAssembleTask = dependsOnAssembleTask(task)?.also {
                assembleTask.dependsOn(it)
                buildTasks.add(it)
            }
            if (libAssembleTask == null) {
                assembleTask.dependsOn(task)
            }
            task
        }

        extensions.create<CrossCompileExtension>("crosscompile", bashBuild, toolchains)
        registerToolchainsTask(toolchains)

        afterEvaluate {
            buildTasks.forEach { it.fillDescIfNull() }
        }
    }

    private fun Task.fillDescIfNull() {
        if (description.isNullOrBlank()) {
            description = "Build $name"
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


package com.github.klee0kai.androidnative

import com.github.klee0kai.androidnative.env.guessAndroidNdk
import com.github.klee0kai.androidnative.env.guessAndroidSdk
import com.github.klee0kai.androidnative.env.guessJdk
import com.github.klee0kai.androidnative.toolchain.CurOsToolchain
import com.github.klee0kai.androidnative.toolchain.findAndroidToolchains
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.language.base.plugins.LifecycleBasePlugin

class AndroidNativePlugin : Plugin<Project> {

    private val taskNames = mutableListOf<String>()

    override fun apply(project: Project) {
        project.pluginManager.apply(LifecycleBasePlugin::class.java)

        val jdkPath = project.guessJdk()
        val androidSdk = project.guessAndroidSdk()
        val androidNdk = project.guessAndroidNdk(androidSdk)
        val toolchains = project.findAndroidToolchains(androidNdk).sortedBy { it.name }


        val assembleTask = project.tasks.getByName(LifecycleBasePlugin.ASSEMBLE_TASK_NAME)

        val bashBuild: BashBuildLambda = { t, taskBlock ->
            val toolchain = t ?: CurOsToolchain

            val taskName = genTaskNameFor(toolchain.name)
            project.tasks.register(taskName, BashBuildTask::class.java, toolchain).get()
                .apply {
                    description = "Build native library with ${toolchain.name} toolchain"
                    group = LifecycleBasePlugin.BUILD_GROUP
                    assembleTask.dependsOn(this)

                    toolchain.genWrapperIfNeed(project)


                    taskBlock.invoke(this)
                }

        }

        project.extensions.create<AndroidNativeExtension>("androidnative", bashBuild, toolchains)
    }


    private fun genTaskNameFor(toolchainName: String): String {
        var name = "native_${toolchainName}"
        val exists = taskNames.filter { it.startsWith(name) }.count()
        if (exists > 0) {
            name = "${name}-${exists}"
        }
        taskNames.add(name)
        return name;
    }

}


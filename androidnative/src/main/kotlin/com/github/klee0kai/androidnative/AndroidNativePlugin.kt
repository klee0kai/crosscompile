package com.github.klee0kai.androidnative

import com.android.build.gradle.BasePlugin
import com.github.klee0kai.androidnative.env.guessAndroidNdk
import com.github.klee0kai.androidnative.env.guessAndroidSdk
import com.github.klee0kai.androidnative.env.guessJdk
import com.github.klee0kai.androidnative.toolchain.findAndroidToolchains
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class AndroidNativePlugin : BasePlugin() {


    override fun apply(project: Project) {

        val jdkPath = project.guessJdk()
        val androidSdk = project.guessAndroidSdk()
        val androidNdk = project.guessAndroidNdk(androidSdk)
        val toolchains = project.findAndroidToolchains(androidNdk)

        println("AndroidNativePlugin: ${jdkPath} ${androidSdk} ${androidNdk}")

        var bashTaskIndex = 0
        val __bashBuild: BashBuildLambda = { toolchain, taskBlock ->

            project.tasks.register(
                "native_${toolchain.name}_${bashTaskIndex++}",
                BashBuildTask::class.java,
                toolchain
            ).get().apply {
                toolchain.applyToEnv(this)

                taskBlock()
            }

        }

        val extension = project.extensions.create<AndroidNativeExtension>("androidnative", __bashBuild)

    }
}
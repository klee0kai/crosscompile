package com.github.klee0kai.androidnative

import com.android.build.gradle.BasePlugin
import com.github.klee0kai.androidnative.env.guessAndroidNdk
import com.github.klee0kai.androidnative.env.guessAndroidSdk
import com.github.klee0kai.androidnative.env.guessJdk
import org.gradle.api.Project

class AndroidNativePlugin : BasePlugin() {


    override fun apply(project: Project) {

        val jdkPath = project.guessJdk()
        val androidSdk = project.guessAndroidSdk()
        val androidNdk = project.guessAndroidNdk(androidSdk)


        println("AndroidNativePlugin: ${jdkPath} ${androidSdk} ${androidNdk}")

    }
}
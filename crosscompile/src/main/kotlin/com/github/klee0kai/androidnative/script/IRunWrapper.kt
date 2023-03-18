package com.github.klee0kai.androidnative.script

import org.gradle.api.Project
import java.io.File

interface IRunWrapper {

    var runWrapperPath: File?

    fun alias(name: String, alias: String?)

    fun env(name: String, value: String?)

    fun subWrapper(name: String): IRunWrapper

    fun gen(project: Project)

}
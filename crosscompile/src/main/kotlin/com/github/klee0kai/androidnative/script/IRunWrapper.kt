package com.github.klee0kai.androidnative.script

import org.gradle.api.Project
import java.io.File

interface IRunWrapper {

    val runWrapperPath: File?

    fun alias(name: String, alias: String?)

    fun gen(project: Project)

}
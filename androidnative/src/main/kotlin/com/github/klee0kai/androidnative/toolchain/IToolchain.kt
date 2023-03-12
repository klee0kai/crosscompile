package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.script.RunWrapper
import org.gradle.api.Project

interface IToolchain {

    val name: String
    val path: String?

    val runWrapper: RunWrapper

    fun genWrapperIfNeed(project: Project)

}




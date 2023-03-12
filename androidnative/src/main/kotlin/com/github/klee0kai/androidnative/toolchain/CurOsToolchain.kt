package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.script.EmptyRunWrapper
import org.gradle.api.Project

object CurOsToolchain : IToolchain {
    override val name: String = "cur_os"
    override val path: String? = null

    override val runWrapper = EmptyRunWrapper
    override fun genWrapperIfNeed(project: Project) {
        runWrapper.gen(project)
    }


}




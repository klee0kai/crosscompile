package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.script.EmptyRunWrapper
import org.gradle.api.Project
import org.gradle.process.internal.DefaultExecSpec

/**
 * Native toolchain:
 *  build = host = target
 *
 * No any overrides
 */
object NativeToolchain : IToolchain {
    override val name: String = "cur_os"
    override val path: String? = null

    override val runWrapper = EmptyRunWrapper
    override fun genWrapperIfNeed(project: Project) {
        runWrapper.gen(project)
    }

    override fun applyTo(spec: DefaultExecSpec) = Unit

    override fun toString(): String {
        return "current os toolchain: no overrides"
    }

}




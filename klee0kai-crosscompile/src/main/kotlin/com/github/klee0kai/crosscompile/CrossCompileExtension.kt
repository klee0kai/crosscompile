package com.github.klee0kai.crosscompile

import com.github.klee0kai.crosscompile.bashtask.BashBuildTask
import com.github.klee0kai.crosscompile.toolchain.Toolchain
import javax.inject.Inject


typealias BashBuildLambda = (name: String, subName: String?, block: BashBuildTask.() -> Unit) -> BashBuildTask

open class CrossCompileExtension @Inject constructor(
    private val __bashBuild: BashBuildLambda,
    val toolchains: List<Toolchain>,
) {

    /**
     * @param groupName lib name. Tasks with same name depends on each other
     * @param subName sub name for task. For example toolchain name
     * @param block bash cmds for building
     */
    fun bashBuild(groupName: String, subName: String? = null, block: BashBuildTask.() -> Unit) =
        __bashBuild.invoke(groupName, subName, block)

}


fun CrossCompileExtension.toolchain(name: String) = toolchains.find { it.name == name }

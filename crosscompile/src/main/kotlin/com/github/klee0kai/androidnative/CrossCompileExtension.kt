package com.github.klee0kai.androidnative

import com.github.klee0kai.androidnative.bashtask.BashBuildTask
import com.github.klee0kai.androidnative.toolchain.IToolchain
import javax.inject.Inject


typealias BashBuildLambda = (name: String, toolchain: IToolchain?, block: BashBuildTask.() -> Unit) -> BashBuildTask

open class AndroidNativeExtension @Inject constructor(
    private val __bashBuild: BashBuildLambda,
    val toolchains: List<IToolchain>,
) {

    /**
     * @param name lib name. Tasks with same name depends on each other
     * @param toolchain using toolchain
     * @param block bash cmds for building
     */
    fun bashBuild(name: String, toolchain: IToolchain? = null, block: BashBuildTask.() -> Unit) =
        __bashBuild.invoke(name, toolchain, block)

}


fun AndroidNativeExtension.toolchain(name: String) = toolchains.find { it.name == name }

package com.github.klee0kai.androidnative

import com.github.klee0kai.androidnative.bashtask.BashBuildTask
import com.github.klee0kai.androidnative.toolchain.IToolchain
import javax.inject.Inject


typealias BashBuildLambda = (toolchain: IToolchain?, block: BashBuildTask.() -> Unit) -> BashBuildTask

open class AndroidNativeExtension @Inject constructor(
    private val __bashBuild: BashBuildLambda,
    val toolchains: List<IToolchain>,
) {

    fun bashBuild(toolchain: IToolchain? = null, block: BashBuildTask.() -> Unit) =
        __bashBuild.invoke(toolchain, block)

}


fun AndroidNativeExtension.toolchain(name: String) = toolchains.find { it.name == name }

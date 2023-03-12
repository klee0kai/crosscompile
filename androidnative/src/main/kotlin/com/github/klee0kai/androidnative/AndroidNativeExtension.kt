package com.github.klee0kai.androidnative

import com.github.klee0kai.androidnative.toolchain.IToolchain
import javax.inject.Inject


typealias BashBuildLambda = (toolchain: IToolchain, block: BashBuildTask.() -> Unit) -> Unit

class AndroidNativeExtension @Inject constructor(
    private val __bashBuild: BashBuildLambda,
) {
    var message: String = "hello"

    fun bashBuild(toolchain: IToolchain, block: BashBuildTask.() -> Unit) =
        __bashBuild.invoke(toolchain, block)
}
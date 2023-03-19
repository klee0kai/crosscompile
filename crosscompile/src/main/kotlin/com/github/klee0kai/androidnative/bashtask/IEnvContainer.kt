package com.github.klee0kai.androidnative.bashtask

import com.github.klee0kai.androidnative.toolchain.IToolchain

interface IEnvContainer : IExec {

    val env: MutableMap<String, Any?>

    var workFolder: String

    var ignoreErr: Boolean

    fun cmd(vararg cmd: Any)

    fun container(name: String? = null, block: IEnvContainer.() -> Unit)


    // ------------ extensions ------------
    fun IEnvContainer.automakeConf(toolchain: IToolchain) {
        toolchain.automakeConf(this)
    }


    fun cmd(cmd: Any, block: CmdBuilder.() -> Unit) {
        val cmdBuilder = CmdBuilder()
        block.invoke(cmdBuilder)
        cmd(cmd, *cmdBuilder.arguments.toTypedArray())
    }


}






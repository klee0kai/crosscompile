package com.github.klee0kai.crosscompile.bashtask

import com.github.klee0kai.crosscompile.toolchain.AndroidNdkToolchain
import com.github.klee0kai.crosscompile.toolchain.IToolchain
import java.io.File

interface IEnvContainer : IExec {

    val env: MutableMap<String, Any?>

    var workFolder: String

    var installFolder: String?

    var ignoreErr: Boolean

    fun cmd(vararg cmd: Any)

    fun createEnvFile(file: File)

    fun container(name: String? = null, block: IEnvContainer.() -> Unit)


    // ------------ extensions ------------
    fun IEnvContainer.automakeConf(toolchain: IToolchain) {
        toolchain.automakeConf(this)
    }

    fun IEnvContainer.conf(ndk: AndroidNdkToolchain) {
        ndk.conf(this)
    }

    fun cmd(cmd: Any, block: CmdBuilder.() -> Unit) {
        val cmdBuilder = CmdBuilder()
        block.invoke(cmdBuilder)
        cmd(cmd, *cmdBuilder.arguments.toTypedArray())
    }


}






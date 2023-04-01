package com.github.klee0kai.crosscompile.bashtask

import com.github.klee0kai.crosscompile.toolchain.AndroidNdk
import java.io.File

interface IEnvContainer : IExec {

    val env: MutableMap<String, Any?>

    var workFolder: String

    var ignoreErr: Boolean

    fun cmd(vararg cmd: Any)

    fun createEnvFile(file: File)

    fun container(name: String? = null, block: EnvContainer.() -> Unit)

}


infix fun IEnvContainer.use(ndk: AndroidNdk) = ndk.apply(this)







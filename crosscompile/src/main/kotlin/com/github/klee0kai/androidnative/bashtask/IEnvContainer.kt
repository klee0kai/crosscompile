package com.github.klee0kai.androidnative.bashtask

import com.github.klee0kai.androidnative.script.IRunWrapper
import com.github.klee0kai.androidnative.toolchain.IToolchain

interface IEnvContainer : IExec {

    val toolchain: IToolchain

    val env: MutableMap<String, Any?>

    val runWrapper: IRunWrapper

    var workFolder: String

    var ignoreErr: Boolean

    fun cmd(vararg cmd: Any)

    fun env(name: String? = null, block: IEnvContainer.() -> Unit)


}

fun IEnvContainer.configAll() {
    configBinAlias()
    configAutoTool()
}

fun IEnvContainer.configBinAlias() =
    toolchain.applyBinAppAlias(this)

fun IEnvContainer.configAutoTool() =
    toolchain.applyAutoToolConf(this)

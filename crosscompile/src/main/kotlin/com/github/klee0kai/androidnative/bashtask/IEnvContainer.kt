package com.github.klee0kai.androidnative.bashtask

interface IEnvContainer : IExec {

    val env: MutableMap<String, Any?>

    var workFolder: String

    var ignoreErr: Boolean

    fun cmd(vararg cmd: Any)

    fun env(block: IEnvContainer.() -> Unit)


}
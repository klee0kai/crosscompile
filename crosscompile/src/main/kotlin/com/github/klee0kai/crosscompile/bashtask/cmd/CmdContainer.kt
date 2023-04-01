package com.github.klee0kai.crosscompile.bashtask.cmd

import com.github.klee0kai.crosscompile.bashtask.EnvContainer

open class CmdContainer(
    name: String,
    env: EnvContainer
) : EnvContainer(name, env) {

    open val arguments = mutableListOf<Any>()

    open fun addExecArgs(vararg args: Any) {
        this.arguments.addAll(args)
    }

    override fun fullExecCmd(vararg cmd: Any): Array<Any> =
        arrayOf(*cmd, *arguments.toTypedArray(), *execSpec.args.toTypedArray())

}

fun EnvContainer.exec(
    vararg cmd: Any,
    block: CmdContainer.() -> Unit
) {
    val envName = "${this.name}_${cmd}_${childEnvInc}"
    runQueue.add(CmdContainer(envName, this)
        .apply {
            exec(*cmd)
            block.invoke(this)
        }
    )
}
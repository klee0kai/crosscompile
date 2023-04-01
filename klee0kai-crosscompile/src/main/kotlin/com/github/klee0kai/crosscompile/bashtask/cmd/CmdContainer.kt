package com.github.klee0kai.crosscompile.bashtask.cmd

import com.github.klee0kai.crosscompile.bashtask.EnvContainer

open class CmdContainer(
    name: String,
    env: EnvContainer
) : EnvContainer(name, env) {

    open val arguments = mutableListOf<Any>()

    open fun addArguments(vararg args: Any) {
        this.arguments.addAll(args)
    }

    override fun fullCmd(vararg cmd: Any): Array<Any> =
        arrayOf(*cmd, *arguments.toTypedArray(), *execSpec.args.toTypedArray())

}

fun EnvContainer.cmd(
    vararg cmd: Any,
    block: CmdContainer.() -> Unit
) {
    val envName = "${this.name}_${cmd}_${childEnvInc}"
    exec.add(CmdContainer(envName, this)
        .apply {
            cmd(*cmd)
            block.invoke(this)
        }
    )
}
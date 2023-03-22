package com.github.klee0kai.crosscompile.bashtask.cmd

import com.github.klee0kai.crosscompile.bashtask.EnvContainer

open class CmdContainer(
    val execCmd: List<Any>,
    name: String,
    env: EnvContainer
) : EnvContainer(name, env) {

    open val arguments = mutableListOf<Any>()

    open fun addArguments(vararg args: Any) {
        this.arguments.addAll(args)
    }

    override fun cmd(vararg cmd: Any) {
        super.cmd(*execCmd.toTypedArray(), *cmd, *arguments.toTypedArray())
    }

}

fun EnvContainer.cmd(
    vararg cmd: Any,
    block: CmdContainer.() -> Unit
) {
    val envName = "${this.name}_${cmd}_${childEnvInc}"
    exec.add(CmdContainer(cmd.asList(), envName, this).apply(block))
}
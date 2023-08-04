package com.github.klee0kai.crosscompile.bashtask.cmd

import com.github.klee0kai.crosscompile.bashtask.EnvContainerImpl
import com.github.klee0kai.shlex.Shlex

open class CmdContainer(
    name: String,
    env: EnvContainerImpl
) : EnvContainerImpl(name, env) {

    open val arguments = mutableListOf<Any>()

    open fun execArgs(vararg args: String) {
        this.arguments.addAll(args)
    }

    open fun shArgs(vararg args: String) {
        this.arguments.addAll(args.flatMap { Shlex.split(it, shlexConfig) })
    }

    override fun fullExecCmd(vararg cmd: Any): Array<Any> =
        arrayOf(*cmd, *arguments.toTypedArray(), *execSpec.args.toTypedArray())

}

fun EnvContainerImpl.exec(
    vararg cmd: String,
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

fun EnvContainerImpl.sh(
    vararg cmd: String,
    block: CmdContainer.() -> Unit
) {
    val envName = "${this.name}_${cmd}_${childEnvInc}"
    runQueue.add(CmdContainer(envName, this)
        .apply {
            sh(*cmd)
            block.invoke(this)
        }
    )
}
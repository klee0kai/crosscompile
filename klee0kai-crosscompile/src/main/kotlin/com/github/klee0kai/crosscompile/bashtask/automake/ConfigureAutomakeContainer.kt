package com.github.klee0kai.crosscompile.bashtask.automake

import com.github.klee0kai.crosscompile.bashtask.EnvContainerImpl
import com.github.klee0kai.crosscompile.bashtask.EnvContainer
import com.github.klee0kai.crosscompile.bashtask.cmd.CmdContainer
import com.github.klee0kai.crosscompile.toolchain.Toolchain

open class ConfigureAutomakeContainer(
    name: String,
    env: EnvContainerImpl
) : CmdContainer(name, env) {

    var installFolder: String?
        get() = (env["DESTDIR"] ?: env["INSTALL_PREFIX"])?.toString()
        set(value) {
            env["DESTDIR"] = value
            env["INSTALL_PREFIX"] = value
            arguments.add("--prefix=${value}")
        }

}


fun EnvContainerImpl.configureAutomake(
    configureScript: String = "./Configure",
    block: ConfigureAutomakeContainer.() -> Unit = {}
) {
    val envName = "${this.name}_${configureScript}_${childEnvInc}"
    runQueue.add(
        ConfigureAutomakeContainer(envName, this).apply {
            exec(configureScript)
            block.invoke(this)
        }
    )
}

infix fun EnvContainer.use(toolchain: Toolchain) =
    toolchain.automakeConf(this)

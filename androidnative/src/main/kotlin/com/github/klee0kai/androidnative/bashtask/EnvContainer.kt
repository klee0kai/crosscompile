package com.github.klee0kai.androidnative.bashtask

import com.github.klee0kai.androidnative.toolchain.IToolchain
import org.gradle.api.model.ObjectFactory
import org.gradle.process.internal.DefaultExecSpec
import org.gradle.process.internal.ExecActionFactory
import java.io.File
import java.io.IOException

class EnvContainer(
    val objectFactory: ObjectFactory,
    val execAction: ExecActionFactory,
    val toolchain: IToolchain,
) : IEnvContainer {

    override val env: MutableMap<String, Any?>
        get() = execSpec.environment


    override var ignoreErr: Boolean = false

    override var workFolder: String
        get() = execSpec.workingDir.absolutePath
        set(value) {
            execSpec.workingDir = File(value)
        }

    val execSpec = objectFactory.newInstance(DefaultExecSpec::class.java)
    private val exec = mutableListOf<IExec>()

    constructor(env: EnvContainer) : this(env.objectFactory, env.execAction, env.toolchain) {
        ignoreErr = env.ignoreErr
        env.execSpec.copyTo(execSpec)
    }

    override fun cmd(vararg cmd: Any) {
        exec.add(IExec {
            try {
                val execAction = execAction.newExecAction()
                execSpec.copyTo(execAction)
                if (toolchain.runWrapper.runWrapperPath?.exists() == true) {
                    val args = listOf("bash", toolchain.runWrapper.runWrapperPath?.absolutePath) + cmd
                    execAction.commandLine(*args.toTypedArray())
                } else {
                    execAction.commandLine(*cmd)
                }

                execAction.execute()
            } catch (e: Exception) {
                if (!ignoreErr) {
                    throw IOException("can't run ${cmd.joinToString(" ")}", e)
                }
            }
        })
    }

    override fun env(block: IEnvContainer.() -> Unit) {
        exec.add(EnvContainer(this).also(block))
    }

    override fun exec() {
        exec.forEach { it.exec() }
    }


}
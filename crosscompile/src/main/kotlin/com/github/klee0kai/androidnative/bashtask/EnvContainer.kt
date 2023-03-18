package com.github.klee0kai.androidnative.bashtask

import com.github.klee0kai.androidnative.script.IRunWrapper
import com.github.klee0kai.androidnative.script.RunOnLinuxWrapper
import com.github.klee0kai.androidnative.toolchain.IToolchain
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.process.ExecResult
import org.gradle.process.internal.DefaultExecSpec
import org.gradle.process.internal.ExecAction
import org.gradle.process.internal.ExecActionFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class EnvContainer(
    val name: String,
    val project: Project,
    val objectFactory: ObjectFactory,
    val execAction: ExecActionFactory,
    override val toolchain: IToolchain,
    override val runWrapper: IRunWrapper = RunOnLinuxWrapper(name),
) : IEnvContainer {

    override var ignoreErr: Boolean = false

    override var workFolder: String
        get() = execSpec.workingDir.absolutePath
        set(value) {
            execSpec.workingDir = File(value)
        }

    private val execSpec = objectFactory.newInstance(DefaultExecSpec::class.java)
    private val exec = mutableListOf<IExec>()

    private var childEnvIndex = 0

    constructor(name: String, env: EnvContainer) : this(
        name,
        env.project,
        env.objectFactory,
        env.execAction,
        env.toolchain,
        env.runWrapper.subWrapper(name),
    ) {
        ignoreErr = env.ignoreErr
        env.execSpec.copyTo(execSpec)
    }

    override fun cmd(vararg cmd: Any) {
        exec.add(IExec {
            val errStream = ByteArrayOutputStream()
            var result: ExecResult? = null
            try {
                val execAction = execAction.newExecAction()
                execSpec.copyTo(execAction)
                execAction.runWrapped(*cmd)

                execAction.isIgnoreExitValue = true;
                execAction.errorOutput = errStream;


//                result = execAction.execute()

//                if (result.exitValue != 0) {
//                    throw ExecException("Cmd ${cmd.joinToString(" ")} finished with exit code ${result.exitValue}")
//                }
            } catch (e: Exception) {
                if (!ignoreErr) {
                    val errStreamText = String(errStream.toByteArray())
                    throw IOException(
                        "can't run ${cmd.joinToString(" ")}\n ${e.causedMessage()} $errStreamText",
                        e
                    )
                }
            }
        })
    }

    override fun env(name: String?, block: IEnvContainer.() -> Unit) {
        val newName = name ?: "${this.name}_ch${childEnvIndex++}"
        exec.add(EnvContainer(newName, this).also(block))
    }

    override fun exec() {
        exec.forEach { it.exec() }
    }


    private fun Throwable.causedMessage(): String {
        return "$message \n caused: ${cause?.causedMessage()}"
    }

    private fun ExecAction.runWrapped(vararg cmd: Any) {
        if (runWrapper.runWrapperPath?.exists() == true) {
            val args = listOf("bash", runWrapper.runWrapperPath?.absolutePath) + cmd
            commandLine(*args.toTypedArray())
        } else {
            commandLine(*cmd)
        }
    }

}
package com.github.klee0kai.androidnative.bashtask

import com.github.klee0kai.androidnative.script.IRunWrapper
import com.github.klee0kai.androidnative.script.RunOnLinuxWrapper
import com.github.klee0kai.androidnative.toolchain.IToolchain
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.process.internal.DefaultExecSpec
import org.gradle.process.internal.ExecActionFactory
import java.io.File
import java.io.IOException

class EnvContainer(
    val name: String,
    val project: Project,
    val objectFactory: ObjectFactory,
    val execAction: ExecActionFactory,
    override val toolchain: IToolchain,
) : IEnvContainer {

    override val env: MutableMap<String, Any?>
        get() = execSpec.environment

    override val runWrapper: IRunWrapper = RunOnLinuxWrapper(name)


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
        env.toolchain
    ) {
        ignoreErr = env.ignoreErr
        env.execSpec.copyTo(execSpec)
    }

    override fun cmd(vararg cmd: Any) {
        exec.add(IExec {
            try {
                val execAction = execAction.newExecAction()
                execSpec.copyTo(execAction)
                if (runWrapper.runWrapperPath?.exists() == true) {
                    val args = listOf("bash", runWrapper.runWrapperPath?.absolutePath) + cmd
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

    override fun env(name: String?, block: IEnvContainer.() -> Unit) {
        val newName = name ?: "${this.name}_ch${childEnvIndex++}"
        exec.add(EnvContainer(newName, this).also(block))
    }

    override fun exec() {
        runWrapper.gen(project)
        exec.forEach { it.exec() }
    }


}
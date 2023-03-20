package com.github.klee0kai.crosscompile.bashtask

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.process.internal.DefaultExecSpec
import org.gradle.process.internal.ExecActionFactory
import org.gradle.process.internal.ExecException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class EnvContainer(
    val name: String,
    val project: Project,
    val objectFactory: ObjectFactory,
    val execAction: ExecActionFactory,
) : IEnvContainer {

    override val env: MutableMap<String, Any?>
        get() = execSpec.environment

    override var ignoreErr: Boolean = false

    override var workFolder: String
        get() = execSpec.workingDir.absolutePath
        set(value) {
            execSpec.workingDir = File(value)
        }

    override var installFolder: String?
        get() = (env["DESTDIR"] ?: env["INSTALL_PREFIX"])?.toString()
        set(value) {
            env["DESTDIR"] = value
            env["INSTALL_PREFIX"] = value
            execSpec.args("--prefix=${value}")
        }


    private val execSpec = objectFactory.newInstance(DefaultExecSpec::class.java)
    private val exec = mutableListOf<IExec>()

    private var childEnvIndex = 0

    constructor(name: String, env: EnvContainer) : this(
        name,
        env.project,
        env.objectFactory,
        env.execAction,
    ) {
        ignoreErr = env.ignoreErr
        env.execSpec.copyTo(execSpec)
    }

    override fun cmd(vararg cmd: Any) {
        exec.add(IExec {
            val errStream = ByteArrayOutputStream()
            val execAction = execAction.newExecAction()
            execSpec.copyTo(execAction)

            val fullCmd = arrayOf(*cmd, *execSpec.args.toTypedArray())
            try {
                execAction.commandLine(*fullCmd)

                execAction.isIgnoreExitValue = true;
                execAction.errorOutput = errStream;

                println(fullCmd.joinToString(" "))

                val result = execAction.execute()

                if (result.exitValue != 0) {
                    throw ExecException("Cmd ${fullCmd.joinToString(" ")} finished with exit code ${result.exitValue}")
                }
            } catch (e: Exception) {
                if (!ignoreErr) {
                    val errStreamText = String(errStream.toByteArray())
                    throw IOException(
                        "can't run ${fullCmd.joinToString(" ")}\n ${e.causedMessage()} $errStreamText", e
                    )
                } else {
                    print(String(errStream.toByteArray()))
                }
            }
        })
    }

    override fun createEnvFile(file: File) {
        exec.add(IExec {
            val sh = buildString {
                append("#!/bin/sh\n\n")

                env.keys.forEach { key ->
                    val value = env.getOrDefault(key, null)?.toString()
                    if (!value.isNullOrBlank()) append("${key}=\"${value}\"\n")
                }

                append("\n\n")
                append("$@ ${execSpec.args.joinToString(" ")}")
            }

            file.parentFile.mkdirs()
            file.writeText(sh)
        })
    }

    override fun container(name: String?, block: IEnvContainer.() -> Unit) {
        val newName = name ?: "${this.name}_ch${childEnvIndex++}"
        exec.add(EnvContainer(newName, this).also(block))
    }

    override fun exec() {
        exec.forEach { it.exec() }
    }


    private fun Throwable.causedMessage(): String {
        return "$message \n caused: ${cause?.causedMessage() ?: ""}"
    }

}
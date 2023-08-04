package com.github.klee0kai.crosscompile.bashtask

import com.github.klee0kai.shlex.Shlex
import com.github.klee0kai.shlex.ShlexConfig
import org.apache.tools.ant.util.TeeOutputStream
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.process.internal.DefaultExecSpec
import org.gradle.process.internal.ExecActionFactory
import org.gradle.process.internal.ExecException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.OutputStream

open class EnvContainerImpl(
    val name: String,
    val project: Project,
    val objectFactory: ObjectFactory,
    val execAction: ExecActionFactory,
) : EnvContainer {

    override val env: MutableMap<String, Any?>
        get() = execSpec.environment

    override var ignoreErr: Boolean = false

    override var configureException: Throwable? = null

    override var workFolder: String
        get() = execSpec.workingDir.absolutePath
        set(value) {
            execSpec.workingDir = File(value)
        }

    open val execSpec = objectFactory.newInstance(DefaultExecSpec::class.java)
    open val runQueue = mutableListOf<Run>()

    open var shlexConfig = ShlexConfig()

    open val errorOutput: (() -> OutputStream)? = null
    open var outputStream: (() -> OutputStream)? = null

    var childEnvInc = 0
        private set
        get() = field++


    constructor(name: String, env: EnvContainerImpl) : this(
        name,
        env.project,
        env.objectFactory,
        env.execAction,
    ) {
        ignoreErr = env.ignoreErr
        env.execSpec.copyTo(execSpec)
    }

    override fun exec(vararg cmd: String) {
        runQueue.add(Run {
            val localErrStream = ByteArrayOutputStream()
            val execAction = execAction.newExecAction()
            execSpec.copyTo(execAction)

            val fullCmd = fullExecCmd(*cmd)
            try {
                execAction.commandLine(*fullCmd)

                execAction.isIgnoreExitValue = true
                execAction.errorOutput = errorOutput?.let {
                    TeeOutputStream(TeeOutputStream(it(), System.err), localErrStream)
                } ?: TeeOutputStream(localErrStream, System.err)

                execAction.standardOutput = outputStream?.let { TeeOutputStream(it(), System.out) } ?: System.out

                println(fullCmd.joinToString(" "))

                val result = execAction.execute()

                if (result.exitValue != 0) {
                    throw ExecException("Cmd ${fullCmd.joinToString(" ")} finished with exit code ${result.exitValue}")
                }
            } catch (e: Exception) {
                if (!ignoreErr) {
                    val errStreamText = String(localErrStream.toByteArray())
                    throw IOException(
                        "can't run ${fullCmd.joinToString(" ")}\n ${e.causedMessage()} $errStreamText", e
                    )
                }
            }
        })
    }

    override fun sh(vararg cmd: String) {
        val execCmd = cmd.flatMap { Shlex.split(it, shlexConfig) }
        exec(*execCmd.toTypedArray())
    }

    override fun createEnvFile(file: File) {
        runQueue.add(Run {
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

    override fun container(name: String?, block: EnvContainerImpl.() -> Unit) {
        val newName = name ?: genChildContainerName()
        runQueue.add(EnvContainerImpl(newName, this).also(block))
    }

    override fun run() {
        configureException?.let { error(it) }
        runQueue.forEach { it.run() }
    }

    open fun fullExecCmd(vararg cmd: Any) = arrayOf(*cmd, *execSpec.args.toTypedArray())

    open fun genChildContainerName() = "${this.name}_ch${childEnvInc}"

    private fun Throwable.causedMessage(): String {
        return "$message \n caused: ${cause?.causedMessage() ?: ""}"
    }

}
package com.github.klee0kai.androidnative

import com.github.klee0kai.androidnative.toolchain.IToolchain
import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult
import org.gradle.process.internal.DefaultExecSpec
import org.gradle.process.internal.ExecActionFactory
import javax.inject.Inject

abstract class BashBuildTask @Inject constructor(
    @Input
    val objectFactory: ObjectFactory,
    @Input
    val execAction: ExecActionFactory,
    @Input
    val toolchain: IToolchain,
) : DefaultTask() {

    private val execCmds = mutableListOf<Array<out Any>>()

    @TaskAction
    fun exec() {
        execCmds.forEach { cmd ->
            val execSpec = objectFactory.newInstance(DefaultExecSpec::class.java)
            val execResult = objectFactory.property(ExecResult::class.java)
            val execAction = execAction.newExecAction()
            if (toolchain.runWrapper.runWrapperPath?.exists() == true) {
                val args = listOf("bash", toolchain.runWrapper.runWrapperPath?.absolutePath) + cmd
                execSpec.commandLine(*args.toTypedArray())
            } else {
                execSpec.commandLine(*cmd)
            }
            execSpec.copyTo(execAction)
            execResult.set(execAction.execute())
        }
    }

    fun bash(vararg args: Any) {
        execCmds.add(args)
    }

}
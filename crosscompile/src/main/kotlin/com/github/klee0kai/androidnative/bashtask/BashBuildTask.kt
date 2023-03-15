package com.github.klee0kai.androidnative.bashtask

import com.github.klee0kai.androidnative.toolchain.IToolchain
import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.ExecActionFactory
import javax.inject.Inject

open class BashBuildTask @Inject constructor(
    @Input
    val objectFactory: ObjectFactory,
    @Input
    val execAction: ExecActionFactory,
    @Input
    val libName: String,
    @Input
    val toolchain: IToolchain,
) : DefaultTask(), IEnvContainer {

    @get:Input
    override val env: MutableMap<String, Any?>
        get() = curEnv.env

    @get:Input
    override var workFolder: String
        get() = curEnv.workFolder
        set(value) {
            curEnv.workFolder = value
        }

    @get:Input
    override var ignoreErr: Boolean
        get() = curEnv.ignoreErr
        set(value) {
            curEnv.ignoreErr = value
        }

    private val curEnv = EnvContainer(objectFactory, execAction, toolchain)

    init {
        toolchain.applyTo(curEnv.execSpec)
    }

    @TaskAction
    override fun exec() {
        toolchain.genWrapperIfNeed(project)
        curEnv.exec()
    }

    override fun cmd(vararg cmd: Any) = curEnv.cmd(*cmd)

    override fun env(block: IEnvContainer.() -> Unit) = curEnv.env(block)


}


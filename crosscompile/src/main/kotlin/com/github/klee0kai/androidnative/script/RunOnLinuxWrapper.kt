package com.github.klee0kai.androidnative.script

import com.github.klee0kai.androidnative.utils.indexesSequence
import com.github.klee0kai.androidnative.utils.insertTo
import org.gradle.api.Project
import java.io.File

class RunOnLinuxWrapper private constructor(
    val name: String,
    private val container: RunOnLinuxWrapper?,
) : IRunWrapper {

    override var runWrapperPath: File? = container?.runWrapperPath

    private val template = "run_on_linux.sh"

    private var aliasSh: String = ""

    private var envSh: String = ""

    private val subEnvContainers = mutableListOf<RunOnLinuxWrapper>()

    constructor(name: String) : this(name, null)

    override fun alias(name: String, alias: String?) {
        if (alias == null) {
            return
        }
        aliasSh += "function $name() {\n $alias \$@ \n} \n\n"
    }

    override fun env(name: String, value: String?) {
        if (value == null) {
            return
        }
        envSh += "export $name=\"$value\"\n"
    }


    override fun subWrapper(name: String): IRunWrapper =
        container?.subWrapper(name) ?: RunOnLinuxWrapper(name, this).also { subEnvContainers.add(it) }

    override fun gen(project: Project) {
        runWrapperPath = File(project.buildDir, "scripts/${name}.sh")
        runWrapperPath?.parentFile?.mkdirs()

        val file = runWrapperPath ?: return
        var sh = String(javaClass.getResourceAsStream(template)?.readAllBytes() ?: return)

        val subEnv = subEnvContainers.joinToString("\n") {
            "function ${it.name}() {\n" +
                    "${it.container?.name}" +
                    " ${it.envSh}\n${it.aliasSh}\n" +
                    " \$@\n" +
                    "}\n" +
                    "\n"
        }

        sh = sh.insertTo(
            index = sh.indexesSequence("\n").take(4).last(),
            txt = "${envSh}\n${aliasSh}\n$subEnv"
        )

        with(file.outputStream()) {
            write(sh.toByteArray())
        }
    }
}


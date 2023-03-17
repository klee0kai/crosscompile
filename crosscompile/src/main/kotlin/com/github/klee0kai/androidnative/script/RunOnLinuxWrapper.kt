package com.github.klee0kai.androidnative.script

import com.github.klee0kai.androidnative.utils.indexesSequence
import com.github.klee0kai.androidnative.utils.insertTo
import org.gradle.api.Project
import java.io.File

class RunOnLinuxWrapper(
    val name: String
) : IRunWrapper {

    override var runWrapperPath: File? = null

    private val template = "run_on_linux.sh"

    private var aliasSh: String = ""

    private var envSh: String = ""

    override fun alias(name: String, alias: String?) {
        if (alias == null) {
            return
        }
        aliasSh += "function $name() {\n $alias \$@ \n } \n\n"
    }

    override fun env(name: String, value: String?) {
        if (value == null) {
            return
        }
        envSh += "export $name=\"$value\"\n"
    }

    override fun gen(project: Project) {
        runWrapperPath = File(project.buildDir, "scripts/${name}.sh")
        runWrapperPath?.parentFile?.mkdirs()

        val file = runWrapperPath ?: return
        var sh = String(javaClass.getResourceAsStream(template)?.readAllBytes() ?: return)

        sh = sh.insertTo(
            index = sh.indexesSequence("\n").take(4).last(),
            txt = "${envSh}\n${aliasSh}"
        )

        with(file.outputStream()) {
            write(sh.toByteArray())
        }
    }
}


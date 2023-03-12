package com.github.klee0kai.androidnative.script

import com.github.klee0kai.androidnative.utils.indexesSequence
import com.github.klee0kai.androidnative.utils.insertTo
import java.io.File

class RunOnLinuxWrapper(
    override val runWrapperPath: File
) : RunWrapper {

    private val template = "run_on_linux.sh"

    private var aliasSh: String = ""

    override fun alias(name: String, alias: String?) {
        if (alias == null) {
            return
        }
        aliasSh += "function ${name}() {\n $alias \$@ \n } \n\n"
    }

    override fun gen() {
        if (!runWrapperPath.exists()) {
            var sh = String(javaClass.getResourceAsStream(template)?.readAllBytes() ?: return)

            sh = sh.insertTo(
                index = sh.indexesSequence("\n").take(4).last(),
                txt = aliasSh
            )

            with(runWrapperPath.outputStream()) {
                write(sh.toByteArray())
            }
        }
    }
}


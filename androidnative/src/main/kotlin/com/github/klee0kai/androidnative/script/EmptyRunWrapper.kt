package com.github.klee0kai.androidnative.script

import org.gradle.api.Project
import java.io.File

object EmptyRunWrapper : RunWrapper {
    private val template = "run_on_linux.sh"
    override var runWrapperPath: File? = null

    override fun alias(name: String, alias: String?) = Unit


    override fun gen(project: Project) {
        runWrapperPath = File(project.buildDir, "scripts/${template}")

        val file = runWrapperPath ?: return
        if (!file.exists()) {
            val sh = String(javaClass.getResourceAsStream(template)?.readAllBytes() ?: return)

            with(file.outputStream()) {
                write(sh.toByteArray())
            }
        }
    }

}
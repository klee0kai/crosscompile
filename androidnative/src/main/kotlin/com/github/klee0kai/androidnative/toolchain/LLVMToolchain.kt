package com.github.klee0kai.androidnative.toolchain

import org.gradle.api.tasks.AbstractExecTask
import java.io.File

class LLVMToolchain(
    override val name: String,
    val clangFile: File?,
    val clangcppFile: File?,
    val addr2line: File?,
    val arFile: File?,
    val asFile: File?,
    val ldFile: File?,
    val nmFile: File?,
    val objcopyFile: File?,
    val objdumpFile: File?,
    val runlibFile: File?,
    val readelfFile: File?,
    val sizeFile: File?,
    val stringsFile: File?,
    val dwpFile: File?,
) : IToolchain {
    override fun applyToEnv(task: AbstractExecTask<*>) {

    }
}


package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.env.guessAndroidNdk
import com.github.klee0kai.androidnative.utils.pathPlus
import com.github.klee0kai.androidnative.utils.walkStarMasked
import org.gradle.api.Project
import org.gradle.api.tasks.AbstractExecTask
import java.io.File
import java.nio.file.Paths

class LLVMToolchain(
    override val name: String,
    val clangFile: File,
    val clangcppFile: File,
    val addr2line: File,
    val arFile: File,
    val asFile: File,
    val ldFile: File,
    val nmFile: File,
    val objcopyFile: File,
    val objdumpFile: File,
    val runlibFile: File,
    val readelfFile: File,
    val sizeFile: File,
    val stringsFile: File,
    val dwpFile: File,
) : IToolchain {
    override fun applyToEnv(task: AbstractExecTask<*>) {
        TODO("Not yet implemented")
    }
}


fun Project.findAndroidToolchains(
    androidNdk: String? = guessAndroidNdk()
): List<LLVMToolchain> {
    if (androidNdk == null)
        return emptyList()
    val files = Paths.get(androidNdk.pathPlus("toolchains/llvm/prebuilt/*/bin")).walkStarMasked().toList()
    val allAvailableToolchainNames = files.flatMap { binFolder -> binFolder.guessToolchainNamesFromBinFolder() }.toSet()



    return emptyList()
}

private fun File.guessToolchainNamesFromBinFolder(): Set<String> {
    val binFolder = this

    return binFolder.list()?.filter {
        it.split("-").size > 3
    }?.map {
        val groups = it.split("-")

        groups.subList(0, groups.size - 1)
            .joinToString("-")
    }?.toSet() ?: emptySet()
}
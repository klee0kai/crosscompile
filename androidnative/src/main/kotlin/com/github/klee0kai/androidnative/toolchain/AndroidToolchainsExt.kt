package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.env.guessAndroidNdk
import com.github.klee0kai.androidnative.script.RunOnLinuxWrapper
import com.github.klee0kai.androidnative.utils.pathPlus
import com.github.klee0kai.androidnative.utils.removeDoubles
import com.github.klee0kai.androidnative.utils.walkStarMasked
import org.gradle.api.Project
import java.io.File
import java.nio.file.Paths

private data class ToolchainPrefixes(
    val name: String,
    val prefixes: List<String>,
)


fun Project.findAndroidToolchains(
    androidNdk: String? = guessAndroidNdk()
): List<LLVMToolchain> {
    if (androidNdk == null)
        return emptyList()
    val toolchains = Paths.get(androidNdk.pathPlus("toolchains/llvm/prebuilt/*/bin"))
        .walkStarMasked()
        .flatMap { binFolder ->

            val allAvailableToolchainNames = binFolder.guessToolchainNamesFromBinFolder()

            val toolchainPrefixes = allAvailableToolchainNames.map { name ->
                ToolchainPrefixes(
                    name = name,
                    prefixes = (listOf("$name-")
                            + allAvailableToolchainNames.filter { name.startsWith(it) }.map { "$it-" }
                            + listOf(""))
                )
            }

            toolchainPrefixes.map {
                LLVMToolchain(
                    name = it.name,
                    path = binFolder.absolutePath,
                    runWrapper = RunOnLinuxWrapper(it.name),
                    clangFile = binFolder.findFirstFile(it.prefixes, "clang"),
                    clangcppFile = binFolder.findFirstFile(it.prefixes, "clang++"),
                    addr2line = binFolder.findFirstFile(it.prefixes, "addr2line"),
                    arFile = binFolder.findFirstFile(it.prefixes, "ar"),
                    asFile = binFolder.findFirstFile(it.prefixes, "as"),
                    ldFile = binFolder.findFirstFile(it.prefixes, "ld"),
                    nmFile = binFolder.findFirstFile(it.prefixes, "nm"),
                    objcopyFile = binFolder.findFirstFile(it.prefixes, "objcopy"),
                    objdumpFile = binFolder.findFirstFile(it.prefixes, "objdump"),
                    runlibFile = binFolder.findFirstFile(it.prefixes, "runlib"),
                    readelfFile = binFolder.findFirstFile(it.prefixes, "readelf"),
                    sizeFile = binFolder.findFirstFile(it.prefixes, "size"),
                    stringsFile = binFolder.findFirstFile(it.prefixes, "strings"),
                    dwpFile = binFolder.findFirstFile(it.prefixes, "dwp"),
                )
            }
        }

    return toolchains.toList().removeDoubles { it1, it2 -> it1.name == it2.name }
}

private fun File.findFirstFile(prefixs: List<String>, fileName: String): File? {
    val folder = this
    return prefixs.map {
        File(folder, "${it}${fileName}")
    }.firstOrNull {
        it.exists()
    }
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
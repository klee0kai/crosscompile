package com.github.klee0kai.crosscompile.env

import com.github.klee0kai.crosscompile.toolchain.LLVMToolchain
import com.github.klee0kai.crosscompile.toolchain.ToolchainNaming
import com.github.klee0kai.crosscompile.utils.pathPlus
import com.github.klee0kai.crosscompile.utils.removeDoubles
import com.github.klee0kai.crosscompile.utils.walkStarMasked
import org.gradle.api.Project
import java.io.File
import java.nio.file.Paths

private val prebuildArchMap = mapOf(
    "armv7a" to "arm",
    "aarch64" to "arm64",
    "i686" to "x86",
    "x64_86" to "x64_86",
)


fun Project.findAndroidToolchains(
    androidSdk: String? = guessAndroidSdk(),
    androidNdk: String? = guessAndroidNdk(androidSdk),
): List<LLVMToolchain> {
    if (androidNdk == null)
        return emptyList()
    val toolchains = Paths.get(androidNdk.pathPlus("toolchains/llvm/prebuilt/*/bin"))
        .walkStarMasked()
        .flatMap { binFolder ->

            val allAvailableToolchainNames = binFolder.guessToolchainNamesFromBinFolder()

            val toolchainNames = allAvailableToolchainNames.map { name ->
                ToolchainNaming(
                    name = name,
                    prefixes = (listOf("$name-")
                            + allAvailableToolchainNames.filter { name.startsWith(it) }.map { "$it-" }
                            + listOf(""))
                )
            }

            toolchainNames.map { tlName ->
                val prebuildLibsArch = prebuildArchMap.getOrDefault(tlName.targetArch, tlName.targetArch)
                val androidAbi = tlName.targetAbi?.let { "android-${it}" } ?: "*"
                val prebuildLibsFolders =
                    Paths.get(androidNdk.pathPlus("platforms/${androidAbi}/arch-${prebuildLibsArch}"))
                        .walkStarMasked().toList()

                val inclFolders = listOf(File(binFolder.parentFile, "sysroot/usr/include"))
                val inclArchFolders =
                    Paths.get(androidNdk.pathPlus("toolchains/llvm/prebuilt/*/sysroot/usr/include/${tlName.targetArch}-${tlName.host}-android/"))
                        .walkStarMasked().toList()

                LLVMToolchain(
                    name = tlName.name,
                    path = binFolder.absolutePath,
                    sysroot = inclFolders.first(),
                    includeFolders = inclFolders + inclArchFolders,
                    libs = prebuildLibsFolders,
                    clangFile = binFolder.findFirstFile(tlName.prefixes, "clang"),
                    clangcppFile = binFolder.findFirstFile(tlName.prefixes, "clang++"),
                    addr2line = binFolder.findFirstFile(tlName.prefixes, "addr2line"),
                    arFile = binFolder.findFirstFile(tlName.prefixes, "ar"),
                    asFile = binFolder.findFirstFile(tlName.prefixes, "as"),
                    ldFile = binFolder.findFirstFile(tlName.prefixes, "ld"),
                    nmFile = binFolder.findFirstFile(tlName.prefixes, "nm"),
                    objcopyFile = binFolder.findFirstFile(tlName.prefixes, "objcopy"),
                    objdumpFile = binFolder.findFirstFile(tlName.prefixes, "objdump"),
                    runlibFile = binFolder.findFirstFile(tlName.prefixes, "runlib"),
                    readelfFile = binFolder.findFirstFile(tlName.prefixes, "readelf"),
                    sizeFile = binFolder.findFirstFile(tlName.prefixes, "size"),
                    stringsFile = binFolder.findFirstFile(tlName.prefixes, "strings"),
                    dwpFile = binFolder.findFirstFile(tlName.prefixes, "dwp"),
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
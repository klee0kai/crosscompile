package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.bashtask.IEnvContainer
import java.io.File

open class LLVMToolchain(
    override val name: String,
    override val path: String,

    override val sysroot: File,
    override val includeFolders: List<File>,
    override val libs: List<File>,
    override val clangFile: File?,
    override val clangcppFile: File?,
    override val addr2line: File?,
    override val arFile: File?,
    override val asFile: File?,
    override val ldFile: File?,
    override val nmFile: File?,
    override val objcopyFile: File?,
    override val objdumpFile: File?,
    override val runlibFile: File?,
    override val readelfFile: File?,
    override val sizeFile: File?,
    override val stringsFile: File?,
    override val dwpFile: File?,
) : ILLVMToolchain {

    override fun applyBinAppAlias(envContainer: IEnvContainer) = envContainer.runWrapper.run {
        alias("clang", clangFile?.absolutePath)
        alias("clang++", clangcppFile?.absolutePath)
        alias("addr2line", addr2line?.absolutePath)
        alias("ar", arFile?.absolutePath)
        alias("as", asFile?.absolutePath)
        alias("ld", ldFile?.absolutePath)
        alias("nm", nmFile?.absolutePath)
        alias("objcopy", objcopyFile?.absolutePath)
        alias("objdump", objdumpFile?.absolutePath)
        alias("runlib", runlibFile?.absolutePath)
        alias("readelf", readelfFile?.absolutePath)
        alias("size", sizeFile?.absolutePath)
        alias("strings", stringsFile?.absolutePath)
        alias("dwp", dwpFile?.absolutePath)
    }

    override fun applyAutoToolConf(envContainer: IEnvContainer) = envContainer.runWrapper.run {
        env("PATH", "${path}:\$PATH")

        env("CC", clangFile?.absolutePath)
        env("CXX", clangcppFile?.absolutePath)
        env("CPP", clangcppFile?.absolutePath)
        env("AR", arFile?.absolutePath)
        env("AS", asFile?.absolutePath)
        env("LD", ldFile?.absolutePath)
        env("NM", nmFile?.absolutePath)
        env("OBJCOPY", objcopyFile?.absolutePath)
    }


    override fun toString(): String {
        return "llvm toolchain: $name path $path"
    }
}


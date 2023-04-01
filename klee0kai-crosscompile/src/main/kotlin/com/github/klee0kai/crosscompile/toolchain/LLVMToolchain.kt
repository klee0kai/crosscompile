package com.github.klee0kai.crosscompile.toolchain

import com.github.klee0kai.crosscompile.bashtask.IEnvContainer
import java.io.File

open class LLVMToolchain(
    override val name: String,
    override val path: String,
    val sysroot: File,
    val includeFolders: List<File>,
    val libs: List<File>,
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

    override fun automakeConf(envContainer: IEnvContainer) = envContainer.run {
        env.appendPath("PATH", path)

        env["CC"] = clangFile?.absolutePath
        env["CXX"] = clangcppFile?.absolutePath
        env["CPP"] = clangcppFile?.absolutePath
        env["AR"] = arFile?.absolutePath
        env["AS"] = asFile?.absolutePath
        env["LD"] = ldFile?.absolutePath
        env["NM"] = nmFile?.absolutePath
        env["OBJCOPY"] = objcopyFile?.absolutePath


        includeFolders.forEach {
            env.appendArgs("CFLAGS", "-I${it.absolutePath}")
            env.appendArgs("CPPFLAGS", "-I${it.absolutePath}")
            env.appendArgs("CXXFLAGS", "-I${it.absolutePath}")
        }

        libs.forEach {
            env.appendArgs("LDFLAGS", "-L${it.absolutePath}")
            env.appendPath("LIBRARY_PATH", it.absolutePath)
        }

    }


    override fun toString(): String {
        return "llvm toolchain: $name path $path"
    }
}

private fun MutableMap<String, Any?>.appendPath(key: String, value: Any) {
    this[key] = "${value}:${getOrDefault(key, "")}"
}

private fun MutableMap<String, Any?>.appendArgs(key: String, value: Any) {
    this[key] = "${getOrDefault(key, "")} $value"
}
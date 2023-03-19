package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.bashtask.IEnvContainer
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
        env["PATH"] = "${path}:${env.getOrDefault("PATH", "")}"

        env["CC"] = clangFile?.absolutePath
        env["CXX"] = clangcppFile?.absolutePath
        env["CPP"] = clangcppFile?.absolutePath
        env["AR"] = arFile?.absolutePath
        env["AS"] = asFile?.absolutePath
        env["LD"] = ldFile?.absolutePath
        env["NM"] = nmFile?.absolutePath
        env["OBJCOPY"] = objcopyFile?.absolutePath

        env.appendArgs("CFLAGS", "--sysroot=${sysroot.absolutePath}")
        env.appendArgs("CPPFLAGS", "--sysroot=${sysroot.absolutePath}")
        env.appendArgs("CXXFLAGS", "--sysroot=${sysroot.absolutePath}")

        env.appendArgs("LDFLAGS", "")
        env.appendArgs("OBJCFLAGS", "")
        env.appendArgs("OBJCXXFLAGS", "")
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
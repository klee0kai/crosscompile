package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.script.RunWrapper
import org.gradle.api.Project
import org.gradle.process.internal.DefaultExecSpec
import java.io.File

open class LLVMToolchain(
    override val name: String,
    override val path: String,
    override val runWrapper: RunWrapper,
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

    override fun genWrapperIfNeed(project: Project) {
        runWrapper.alias("clang", clangFile?.absolutePath)
        runWrapper.alias("clang++", clangcppFile?.absolutePath)
        runWrapper.alias("addr2line", addr2line?.absolutePath)
        runWrapper.alias("ar", arFile?.absolutePath)
        runWrapper.alias("as", asFile?.absolutePath)
        runWrapper.alias("ld", ldFile?.absolutePath)
        runWrapper.alias("nm", nmFile?.absolutePath)
        runWrapper.alias("objcopy", objcopyFile?.absolutePath)
        runWrapper.alias("objdump", objdumpFile?.absolutePath)
        runWrapper.alias("runlib", runlibFile?.absolutePath)
        runWrapper.alias("readelf", readelfFile?.absolutePath)
        runWrapper.alias("size", sizeFile?.absolutePath)
        runWrapper.alias("strings", stringsFile?.absolutePath)
        runWrapper.alias("dwp", dwpFile?.absolutePath)


        runWrapper.gen(project)
    }

    override fun applyTo(spec: DefaultExecSpec) {
        spec.apply {
            environment["PATH"] = "${path}:${environment.getOrDefault("PATH", "")}"

            environment["CC"] = clangFile?.absolutePath
            environment["CXX"] = clangcppFile?.absolutePath


//            environment["CROSS_COMPILE"] = "armv7a-linux"
            environment["CPP_FLAGS"] = "-Wno-everything"
        }
    }
}


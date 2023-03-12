package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.script.RunWrapper
import org.gradle.api.Project
import org.gradle.process.internal.DefaultExecSpec
import java.io.File

class AndroidLLVMToolchain(
    name: String,
    path: String,
    val sdkPath: String?,
    val ndkPath: String?,
    runWrapper: RunWrapper,
    clangFile: File?,
    clangcppFile: File?,
    addr2line: File?,
    arFile: File?,
    asFile: File?,
    ldFile: File?,
    nmFile: File?,
    objcopyFile: File?,
    objdumpFile: File?,
    runlibFile: File?,
    readelfFile: File?,
    sizeFile: File?,
    stringsFile: File?,
    dwpFile: File?
) : LLVMToolchain(
    name, path, runWrapper, clangFile, clangcppFile, addr2line, arFile,
    asFile, ldFile, nmFile, objcopyFile, objdumpFile, runlibFile, readelfFile, sizeFile, stringsFile, dwpFile
) {

    override fun genWrapperIfNeed(project: Project) {
        super.genWrapperIfNeed(project)
    }

    override fun applyTo(spec: DefaultExecSpec) {
        super.applyTo(spec)
        spec.apply {
            environment["ANDROID_SDK"] = sdkPath
            environment["ANDROID_SDK_ROOT"] = sdkPath

            environment["ANDROID_NDK"] = ndkPath
            environment["ANDROID_NDK_ROOT"] = ndkPath
            environment["ANDROID_NDK_HOME"] = ndkPath
        }
    }

}
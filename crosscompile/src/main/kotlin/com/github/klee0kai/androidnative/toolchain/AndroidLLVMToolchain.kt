package com.github.klee0kai.androidnative.toolchain

import com.github.klee0kai.androidnative.bashtask.IEnvContainer
import java.io.File

class AndroidLLVMToolchain(
    name: String,
    path: String,
    val sdkPath: String?,
    val ndkPath: String?,
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
    name, path, clangFile, clangcppFile, addr2line, arFile,
    asFile, ldFile, nmFile, objcopyFile, objdumpFile, runlibFile, readelfFile, sizeFile, stringsFile, dwpFile
) {


    override fun automakeConf(envContainer: IEnvContainer) {
        super.automakeConf(envContainer)
        envContainer.run {
            env["ANDROID_SDK"] = sdkPath
            env["ANDROID_SDK_ROOT"] = sdkPath

            env["ANDROID_NDK"] = ndkPath
            env["ANDROID_NDK_ROOT"] = ndkPath
            env["ANDROID_NDK_HOME"] = ndkPath
        }
    }

}
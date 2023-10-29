package com.github.klee0kai.crosscompile.toolchain2

import java.io.File

interface AutoMakeToolchain : Toolchain {

    override val name: String
    override val path: String

    var sysroot: File
    var includeFolders: List<File>
    var libs: List<File>
    var clangFile: File?
    var clangcppFile: File?
    var addr2line: File?
    var arFile: File?
    var asFile: File?
    var ldFile: File?
    var nmFile: File?
    var objcopyFile: File?
    var objdumpFile: File?
    var runlibFile: File?
    var readelfFile: File?
    var sizeFile: File?
    var stringsFile: File?
    var dwpFile: File?

    companion object {
//        fun createAutoMakeToolchain(name: String, block: AutoMakeToolchain.() -> Unit): AutoMakeToolchain {
//
//        }
    }

}


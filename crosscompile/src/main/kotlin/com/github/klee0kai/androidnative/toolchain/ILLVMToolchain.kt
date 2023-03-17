package com.github.klee0kai.androidnative.toolchain

import java.io.File

interface ILLVMToolchain : IToolchain {
    val sysroot: File
    val includeFolders: List<File>
    val libs: List<File>
    val clangFile: File?
    val clangcppFile: File?
    val addr2line: File?
    val arFile: File?
    val asFile: File?
    val ldFile: File?
    val nmFile: File?
    val objcopyFile: File?
    val objdumpFile: File?
    val runlibFile: File?
    val readelfFile: File?
    val sizeFile: File?
    val stringsFile: File?
    val dwpFile: File?
}
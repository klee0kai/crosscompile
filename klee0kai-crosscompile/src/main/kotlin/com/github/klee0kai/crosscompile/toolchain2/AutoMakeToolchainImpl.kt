package com.github.klee0kai.crosscompile.toolchain2

import com.github.klee0kai.crosscompile.bashtask.EnvContainer
import com.github.klee0kai.crosscompile.toolchain2.applicable.ApplicableToolchain
import java.io.File

class AutoMakeToolchainImpl(
    override val name: String,
    override val path: String,
    override val labels: Set<ToolchainLabel> = emptySet(),
    override var includeFolders: List<File> = emptyList(),
    override var sysroot: File,
    override var libs: List<File> = emptyList(),
    override var clangFile: File? = null,
    override var clangcppFile: File? = null,
    override var addr2line: File? = null,
    override var arFile: File? = null,
    override var asFile: File? = null,
    override var ldFile: File? = null,
    override var nmFile: File? = null,
    override var objcopyFile: File? = null,
    override var objdumpFile: File? = null,
    override var runlibFile: File? = null,
    override var readelfFile: File? = null,
    override var sizeFile: File? = null,
    override var stringsFile: File? = null,
    override var dwpFile: File? = null,
    override val details: String? = null,
) : AutoMakeToolchain, ApplicableToolchain {

    override fun automakeConf(envContainer: EnvContainer) = envContainer.run {

    }

    companion object {
//        fun createAutoMakeToolchain(name: String, block: AutoMakeToolchainImpl.() -> Unit): AutoMakeToolchainImpl {
//
//        }
    }

}


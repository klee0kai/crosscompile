package com.github.klee0kai.crosscompile.bashtask

import com.github.klee0kai.crosscompile.toolchain.AndroidNdk
import java.io.File

interface IEnvContainer : IRun {

    /**
     * Environment variables
     */
    val env: MutableMap<String, Any?>

    /**
     * Current work directory for executing app
     */
    var workFolder: String

    /**
     * Ignore app response code
     */
    var ignoreErr: Boolean

    /**
     * Exec app with arguments
     */
    fun exec(vararg cmd: String)

    /**
     *  Exec app with arguments
     *   Using Shell argument
     */
    fun sh(vararg cmd: String)

    /**
     * create environment file
     */
    fun createEnvFile(file: File)

    /**
     * Wrap to container.
     * Common use app arguments and environment variables
     */
    fun container(name: String? = null, block: EnvContainer.() -> Unit)

}


infix fun IEnvContainer.use(ndk: AndroidNdk) = ndk.apply(this)







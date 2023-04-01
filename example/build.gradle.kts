import com.github.klee0kai.crosscompile.*
import com.github.klee0kai.crosscompile.bashtask.BashBuildTask
import com.github.klee0kai.crosscompile.bashtask.automake.configureAutomake
import com.github.klee0kai.crosscompile.bashtask.automake.use
import com.github.klee0kai.crosscompile.bashtask.cmd.exec
import com.github.klee0kai.crosscompile.bashtask.cmd.sh
import com.github.klee0kai.crosscompile.bashtask.use
import com.github.klee0kai.crosscompile.env.findAndroidNdk
import com.github.klee0kai.crosscompile.toolchain.IToolchain
import com.github.klee0kai.crosscompile.utils.walkStarMasked
import java.io.FileOutputStream

plugins {
    id("com.github.klee0kai.crosscompile")
    id("com.dorongold.task-tree") version "2.1.1"
}

val toybox = "toybox"
val openssl = "openssl"
val toyboxSrc = File(project.buildDir, "toybox")
val opensslSrc = File(project.buildDir, "openssl")
val toyboxReportFile = File(project.buildDir, "report/toybox.txt")
val opensslReportFile = File(project.buildDir, "report/openssl.txt")
val androidApi = 30

crosscompile {

    toyboxLibs()

    opensslLibs()

}

fun CrossCompileExtension.toyboxLibs() {
    val toyboxSrcTask = bashBuild("${toybox}_src") {
        description = "Download $toybox source codes"
        doFirst { toyboxSrc.parentFile.mkdirs() }

        ignoreErr = true
        exec(
            "git clone --depth 1 --branch android-13.0.0_r1 https://android.googlesource.com/platform/external/toybox -o origin",
            toyboxSrc.absolutePath
        )
    }

    bashBuild(toybox, "cur_os") {
        dependsOn(toyboxSrcTask)
        buildToybox(null)
    }
    bashBuild(toybox, "android_x86") {
        dependsOn(toyboxSrcTask)
        buildToybox(android_i686(androidApi))
    }
    bashBuild(toybox, "android_x86_64") {
        dependsOn(toyboxSrcTask)
        buildToybox(android_x86_64(androidApi))
    }
    bashBuild(toybox, "android_arm7a") {
        dependsOn(toyboxSrcTask)
        buildToybox(android_arm7a(androidApi))
    }
    bashBuild(toybox, "android_aarch64") {
        dependsOn(toyboxSrcTask)
        buildToybox(android_aarch64(androidApi))
    }

    bashBuild("${toybox}_report") {
        description = "Report $openssl file headers"

        container {
            toyboxReportFile.deleteOnExit()
            outputStream = {
                toyboxReportFile.parentFile.mkdirs()
                FileOutputStream(toyboxReportFile, true)
            }
            workFolder = project.buildDir.absolutePath

            File("${project.buildDir}/libs/*/toybox").walkStarMasked()
                .forEach { exec("file", it.absolutePath) }
        }
    }

}

fun BashBuildTask.buildToybox(toolchain: IToolchain? = null) = container {
    if (toolchain != null) {
        use(findAndroidNdk())
        use(toolchain)
    }

    val toolchainName = toolchain?.name ?: "cur_os"
    val toyboxBuild = File(project.buildDir, "libs/toybox-${toolchainName}")
    doFirst { toyboxBuild.parentFile.mkdirs() }

    workFolder = toyboxSrc.absolutePath

    createEnvFile(toyboxBuild + "toybox_${toolchainName}.sh")

    sh("make clean") { ignoreErr = true }
    configureAutomake("./configure")
    sh("make")
    container {
        ignoreErr = true
        sh("cp toybox", toyboxBuild.absolutePath)
        sh("file toybox")
    }
}

fun CrossCompileExtension.opensslLibs() {
    bashBuild(openssl, "cur_os") {
        buildOpenssl(subName!!)
    }

    bashBuild(openssl, "android-x86") {
        buildOpenssl(subName!!, android_i686(androidApi))
    }

    bashBuild(openssl, "android-x86_64") {
        buildOpenssl(subName!!, android_x86_64(androidApi))
    }

    bashBuild(openssl, "android-arm") {
        buildOpenssl(subName!!, android_arm7a(androidApi))
    }
    bashBuild(openssl, "android-arm64") {
        buildOpenssl(subName!!, android_aarch64(androidApi))
    }


    bashBuild("${openssl}_report") {
        description = "Report $openssl file headers"

        container {
            opensslReportFile.deleteOnExit()
            outputStream = {
                opensslReportFile.parentFile.mkdirs()
                FileOutputStream(opensslReportFile, true)
            }
            workFolder = project.buildDir.absolutePath

            File(project.buildDir, "libs/*/build/bin/openssl").walkStarMasked()
                .forEach { exec("file", it.absolutePath) }
        }
    }
}

fun BashBuildTask.buildOpenssl(
    arch: String,
    toolchain: IToolchain? = null,
) = container {
    if (toolchain != null) {
        use(findAndroidNdk())
        use(toolchain)
    }

    val opensslBuild = File(project.buildDir, "libs/openssl-${arch}/build")
    doFirst { opensslBuild.parentFile.mkdirs() }
    container {
        ignoreErr = true
        sh(
            "git clone --depth 1 --branch OpenSSL_1_1_1-stable https://github.com/openssl/openssl.git -o, origin",
            opensslSrc.absolutePath
        )
    }


    exec("make", "clean") {
        workFolder = opensslSrc.absolutePath
        ignoreErr = true
    }
    configureAutomake(if (toolchain != null) "./Configure" else "./config") {
        workFolder = opensslSrc.absolutePath
        installFolder = opensslBuild.absolutePath

        shArgs("no-filenames no-afalgeng no-asm threads")
        if (toolchain != null) {
            val abi = toolchain.nameHelper.targetAbi ?: 1
            shArgs(arch, "-D__ANDROID_API__=${abi}")
        }

        createEnvFile(opensslBuild + "openssl_${name}.sh")
    }

    container {
        workFolder = opensslSrc.absolutePath
        sh("make -j8")
        sh("make install -j8")
    }


}

operator fun File.plus(s: String): File = File(absolutePath, s)

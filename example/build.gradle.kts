import com.github.klee0kai.crosscompile.*
import com.github.klee0kai.crosscompile.bashtask.BashBuildTask
import com.github.klee0kai.crosscompile.env.findAndroidNdk
import com.github.klee0kai.crosscompile.toolchain.IToolchain

plugins {
    id("com.github.klee0kai.crosscompile")
    id("com.dorongold.task-tree") version "2.1.1"
}

val toybox = "toybox"
val openssl = "openssl"
val toyboxSrc = File(project.buildDir, "toybox")
val opensslSrc = File(project.buildDir, "openssl")
val androidApi = 30

crosscompile {

    toyboxBuilds()

    opensslBuilds()

}

fun CrossCompileExtension.toyboxBuilds() {
    val toyboxSrcTask = bashBuild("${toybox}_src") {
        description = "Download $toybox source codes"
        doFirst { toyboxSrc.parentFile.mkdirs() }

        ignoreErr = true
        cmd(
            "git",
            "clone",
            "--depth", "1",
            "--branch", "android-13.0.0_r1",
            "https://android.googlesource.com/platform/external/toybox",
            "-o", "origin", toyboxSrc.absolutePath
        )
    }

    bashBuild(toybox, "cur_os") {
        dependsOn(toyboxSrcTask)
        buildToybox(null)
    }
    bashBuild(toybox, "android_x86") {
        dependsOn(toyboxSrcTask)
        conf(findAndroidNdk())
        buildToybox(android_i686(androidApi))
    }
    bashBuild(toybox, "android_x86_64") {
        dependsOn(toyboxSrcTask)
        conf(findAndroidNdk())
        buildToybox(android_x86_64(androidApi))
    }
    bashBuild(toybox, "android_arm7a") {
        dependsOn(toyboxSrcTask)
        conf(findAndroidNdk())
        buildToybox(android_arm7a(androidApi))
    }
    bashBuild(toybox, "android_aarch64") {
        dependsOn(toyboxSrcTask)
        conf(findAndroidNdk())
        buildToybox(android_aarch64(androidApi))
    }

}

fun BashBuildTask.buildToybox(toolchain: IToolchain? = null) = container {
    toolchain?.automakeConf(this)

    val toolchainName = toolchain?.name ?: "cur_os"
    val toyboxBuild = File(project.buildDir, "libs/toybox-${toolchainName}")
    doFirst { toyboxBuild.parentFile.mkdirs() }

    workFolder = toyboxSrc.absolutePath
    createEnvFile(toyboxBuild + "toybox_${toolchainName}.sh")
    cmd("make", "clean")
    cmd("./configure")
    cmd("make")

    container {
        ignoreErr = true
        cmd("cp", "toybox", toyboxBuild)
        cmd("file", "toybox")
    }
}

fun CrossCompileExtension.opensslBuilds() {
    bashBuild(openssl, "cur_os") {
        tryBuildOpensslAndroid(subName!!, isCrosscompile = false)
    }

    bashBuild(openssl, "android-x86") {
        conf(findAndroidNdk())
        automakeConf(android_i686(androidApi))
        tryBuildOpensslAndroid(subName!!, androidApi)
    }

    bashBuild(openssl, "android-x86_64") {
        conf(findAndroidNdk())
        automakeConf(android_x86_64(androidApi))
        tryBuildOpensslAndroid(subName!!, androidApi)
    }

    bashBuild(openssl, "android-arm") {
        conf(findAndroidNdk())
        automakeConf(android_arm7a(androidApi))
        tryBuildOpensslAndroid(subName!!, androidApi)
    }
    bashBuild(openssl, "android-arm64") {
        conf(findAndroidNdk())
        automakeConf(android_aarch64(androidApi))
        tryBuildOpensslAndroid(subName!!, androidApi)
    }
}

fun BashBuildTask.tryBuildOpensslAndroid(arch: String, api: Int? = null, isCrosscompile: Boolean = true) {

    val opensslBuild = File(project.buildDir, "libs/openssl-${arch}/build")
    doFirst { opensslBuild.parentFile.mkdirs() }
    container {
        ignoreErr = true
        cmd(
            "git",
            "clone",
            "--depth", "1",
            "--branch", "OpenSSL_1_1_1-stable",
            "https://github.com/openssl/openssl.git",
            "-o", "origin", opensslSrc.absolutePath
        )
    }
    container {
        workFolder = opensslSrc.absolutePath
        installFolder = opensslBuild.absolutePath

        createEnvFile(opensslBuild + "openssl_${name}.sh")
        cmd(if (isCrosscompile) "./Configure" else "./config") {
            addArguments(
                "no-filenames", "no-afalgeng", "no-asm", "threads",
            )
            if (isCrosscompile) {
                addArguments(arch, "-D__ANDROID_API__=${api}")
            }
        }
    }

    container {
        workFolder = opensslSrc.absolutePath
        cmd("make", "clean")
        cmd("make", "-j8")
        cmd("make", "install", "-j8")
    }


}

operator fun File.plus(s: String): File = File(absolutePath, s)

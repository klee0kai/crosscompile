import com.github.klee0kai.androidnative.android_aarch64
import com.github.klee0kai.androidnative.android_arm7a
import com.github.klee0kai.androidnative.android_i686
import com.github.klee0kai.androidnative.android_x86_64
import com.github.klee0kai.androidnative.bashtask.BashBuildTask

plugins {
    id("com.github.klee0kai.androidnative")
    id("com.dorongold.task-tree") version "2.1.1"
}

val toybox = "toybox"
val openssl = "openssl"
val toyboxSrc = File(project.buildDir, "toybox")
val opensslSrc = File(project.buildDir, "openssl")
val androidApi = 21

crosscompile {


    val toyboxSrcTask = bashBuild("${toybox}_src") {
        description = "Download $toybox source codes"
        doFirst { toyboxSrc.parentFile.mkdirs() }

        ignoreErr = true
        cmd(
            "git",
            "clone",
            "--depth", "1",
            "--branch", "0.8.9",
            "git@github.com:landley/toybox.git",
            "-o", "origin", toyboxSrc.absolutePath
        )
    }

    bashBuild(toybox, "cur_os") {
        dependsOn(toyboxSrcTask)
        trybuildToybox(subName!!)
    }
    bashBuild(toybox, "android_arm7a") {
        dependsOn(toyboxSrcTask)
        automakeConf(android_arm7a(androidApi))
        trybuildToybox(subName!!)
    }
    bashBuild(toybox, "android_aarch64") {
        dependsOn(toyboxSrcTask)
        automakeConf(android_aarch64(androidApi))
        trybuildToybox(subName!!)
    }

    bashBuild(openssl, "cur_os") {
        tryBuildOpensslAndroid(subName!!, isCrosscompile = false)
    }

    bashBuild(openssl, "android-x86") {
        automakeConf(android_i686(androidApi))
        tryBuildOpensslAndroid(subName!!, androidApi)
    }

    bashBuild(openssl, "android-x86_64") {
        automakeConf(android_x86_64(androidApi))
        tryBuildOpensslAndroid(subName!!, androidApi)
    }

    bashBuild(openssl, "android-arm") {
        automakeConf(android_arm7a(androidApi))
        tryBuildOpensslAndroid(subName!!, androidApi)
    }
    bashBuild(openssl, "android-arm64") {
        automakeConf(android_aarch64(androidApi))
        tryBuildOpensslAndroid(subName!!, androidApi)
    }

}

fun BashBuildTask.trybuildToybox(arch: String) = container {
    val toyboxBuild = File(project.buildDir, "libs/toybox-${arch}")
    doFirst { toyboxBuild.parentFile.mkdirs() }

    workFolder = toyboxSrc.absolutePath
    cmd("make", "clean")
    cmd("./configure")
    cmd("make")

    container {
        ignoreErr = true
        cmd("cp", "toybox", toyboxBuild)
        cmd("file", "toybox")
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
        ignoreErr = false
        workFolder = opensslSrc.absolutePath


        cmd(if (isCrosscompile) "./Configure" else "./config") {
            addArguments(
                "no-filenames", "no-afalgeng", "no-asm", "threads",
                "--prefix=${opensslBuild.absolutePath}"
            )
            if (isCrosscompile) {
                addArguments(arch, "-D__ANDROID_API__=${api}")
            }
        }
        cmd("make", "clean")
        cmd("make", "-j8")
        cmd("make", "install", "-j8")
    }


}

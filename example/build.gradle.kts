import com.github.klee0kai.androidnative.android_aarch64
import com.github.klee0kai.androidnative.android_arm7a
import com.github.klee0kai.androidnative.android_i686
import com.github.klee0kai.androidnative.android_x86_64
import com.github.klee0kai.androidnative.bashtask.BashBuildTask

plugins {
    id("com.github.klee0kai.androidnative")
    id("com.dorongold.task-tree") version "2.1.1"
}



crosscompile {
    val toybox = "toybox"
    val openssl = "openssl"

    bashBuild(toybox) {
        trybuildToybox("cur_os")
    }
    bashBuild(toybox, android_arm7a(30)) {
        trybuildToybox("android_arm7a")
    }
    bashBuild(toybox, android_aarch64(30)) {
        trybuildToybox("android_aarch64")
    }

    bashBuild(openssl) {
        tryBuildOpensslAndroid()
    }

    bashBuild(openssl, android_i686(21)) {
        tryBuildOpensslAndroid("android-x86", 21)
    }

    bashBuild(openssl, android_x86_64(21)) {
        tryBuildOpensslAndroid("android-x86_64", 21)
    }

    bashBuild(openssl, android_arm7a(21)) {
        tryBuildOpensslAndroid("android-arm", 21)
    }
    bashBuild(openssl, android_aarch64(21)) {
        tryBuildOpensslAndroid("android-arm64", 21)
    }

}

fun BashBuildTask.trybuildToybox(arch: String) {
    val toyboxSrc = File(project.buildDir, "toybox")
    val toyboxBuild = File(project.buildDir, "libs/toybox-${arch}")
    toyboxBuild.parentFile.mkdirs()

    ignoreErr = true
    cmd("git clone --depth 1 --branch 0.8.9 git@github.com:landley/toybox.git -o origin ${toyboxSrc.absolutePath}")

    env {
        workFolder = toyboxSrc.absolutePath
        cmd("make clean")
        cmd("./configure")
        cmd("make")
        cmd("cp toybox $toyboxBuild")
        cmd("file toybox")
    }
}


fun BashBuildTask.tryBuildOpensslAndroid(arch: String? = null, api: Int? = null) {
    val opensslSrc = File(project.buildDir, "openssl")
    val opensslBuild = File(project.buildDir, "libs/openssl-${arch ?: "cur"}/build")
    opensslBuild.parentFile.mkdirs()

    env {
        ignoreErr = true
        cmd("git clone --depth 1 --branch OpenSSL_1_1_1-stable https://github.com/openssl/openssl.git -o origin ${opensslSrc.absolutePath}")
    }

    env {
        ignoreErr = false
        workFolder = opensslSrc.absolutePath

        val additionalArgs = if (arch != null) "$arch -D__ANDROID_API__=${api}" else ""
        val confArgs = "no-filenames no-afalgeng no-asm threads  $additionalArgs --prefix=${opensslBuild.absolutePath}"
        val configScript = if (arch != null) "./Configure" else "./config"
        cmd("$configScript $confArgs")
        cmd("make clean")
        cmd("make -j8")
        cmd("make install -j8")
    }


}

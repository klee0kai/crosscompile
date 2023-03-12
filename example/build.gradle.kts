import com.github.klee0kai.androidnative.android_aarch64
import com.github.klee0kai.androidnative.android_arm7a
import com.github.klee0kai.androidnative.android_i686
import com.github.klee0kai.androidnative.android_x86_64
import com.github.klee0kai.androidnative.bashtask.BashBuildTask

plugins {
    id("com.github.klee0kai.androidnative")
}



androidnative {

    val toybox_curOs = bashBuild {
        trybuildToybox("cur_os")
    }
    val toybox_arm7v = bashBuild(android_arm7a(30)) {
        trybuildToybox("android_arm7a")
    }
    val toybox_aArch64 = bashBuild(android_aarch64(30)) {
        trybuildToybox("android_aarch64")
    }

    toybox_aArch64.dependsOn(toybox_arm7v.dependsOn(toybox_curOs))

    val openssl_x86 = bashBuild(android_i686(21)) {
        tryBuildOpensslAndroid("android-x86", 21)
    }

    val openssl_x86_64 = bashBuild(android_x86_64(21)) {
        tryBuildOpensslAndroid("android-x86_64", 21)
    }

    val openssl_arm7v = bashBuild(android_arm7a(21)) {
        tryBuildOpensslAndroid("android-arm", 21)
    }
    val openssl_aArch64 = bashBuild(android_aarch64(21)) {
        tryBuildOpensslAndroid("android-arm64", 21)
    }

    openssl_aArch64.dependsOn(openssl_arm7v.dependsOn(openssl_x86.dependsOn(openssl_x86_64)))

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


fun BashBuildTask.tryBuildOpensslAndroid(arch: String, api: Int) {
    val opensslSrc = File(project.buildDir, "openssl")
    val opensslBuild = File(project.buildDir, "libs/openssl-${arch}/build")
    opensslBuild.parentFile.mkdirs()

    ignoreErr = true
    cmd("git clone --depth 1 --branch OpenSSL_1_1_1-stable https://github.com/openssl/openssl.git -o origin ${opensslSrc.absolutePath}")

    env {
        workFolder = opensslSrc.absolutePath
        cmd("make clean")
        cmd("./Configure no-filenames no-afalgeng no-asm threads  $arch -D__ANDROID_API__=${api} --prefix=${opensslBuild.absolutePath}")
        cmd("make -j8")
        cmd("make install -j8")
    }


}

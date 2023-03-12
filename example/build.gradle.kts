import com.github.klee0kai.androidnative.android_aarch64
import com.github.klee0kai.androidnative.android_arm7a
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


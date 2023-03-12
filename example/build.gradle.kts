import com.github.klee0kai.androidnative.android_aarch64

plugins {
    id("com.github.klee0kai.androidnative")
}



androidnative {

    bashBuild {
        println("hello from ${toolchain.name}")
        commandLine("echo", "hello")
    }

    bashBuild(android_aarch64(21)) {
        println("hello from ${toolchain.name}")
        commandLine("echo", "hello")
    }


}


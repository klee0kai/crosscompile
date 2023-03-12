import com.github.klee0kai.androidnative.android_aarch64

plugins {
    id("com.github.klee0kai.androidnative")
}



androidnative {

    bashBuild {
        println("hello from ${toolchain.name}")
        bash("echo", "run for ${toolchain.name}")
        bash("echo", "run2 for", toolchain.name)
        bash("type", "gcc")
    }

    bashBuild(android_aarch64(21)) {
        println("hello from ${toolchain.name}")
        bash("echo", "run for ${toolchain.name}")
        bash("echo", "run2 for", toolchain.name)
        bash("clang", "--version")
        bash("type", "clang")

    }


}


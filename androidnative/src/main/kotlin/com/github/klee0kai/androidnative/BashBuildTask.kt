package com.github.klee0kai.androidnative

import com.github.klee0kai.androidnative.toolchain.IToolchain
import org.gradle.api.tasks.Exec
import javax.inject.Inject

class BashBuildTask @Inject constructor(
    val toolchain: IToolchain
) : Exec() {

}
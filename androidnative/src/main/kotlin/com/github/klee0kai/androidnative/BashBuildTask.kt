package com.github.klee0kai.androidnative

import com.github.klee0kai.androidnative.toolchain.IToolchain
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import javax.inject.Inject

open class BashBuildTask @Inject constructor(
    @Input
    val toolchain: IToolchain
) : Exec()


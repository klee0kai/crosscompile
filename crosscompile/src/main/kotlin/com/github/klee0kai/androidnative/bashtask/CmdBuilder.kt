package com.github.klee0kai.androidnative.bashtask

class CmdBuilder {

    val arguments = mutableListOf<Any>()

    fun addArguments(vararg args: Any) {
        this.arguments.addAll(args)
    }

}
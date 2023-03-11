package com.github.klee0kai.androidnative.utils

import java.io.File

fun String.pathPlus(path: String?): String {
    return when {
        endsWith(File.separator) && path?.startsWith(File.separator) == true -> {
            plus(path.substring(1))
        }

        endsWith(File.separator) -> {
            plus(path)
        }

        else -> {
            plus(File.separator).plus(path)
        }
    }
}
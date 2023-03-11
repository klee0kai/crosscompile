package com.github.klee0kai.androidnative.env

import org.gradle.api.Project
import java.util.*


const val localProperties = "local.properties"
const val gradleProperties = "gradle.propertie"

fun Project.readProperty(
    key: String,
    fileName: String = localProperties,
): String? {
    val file = listOf(
        project.file(fileName),
        project.rootProject.file(fileName)
    ).firstOrNull {
        it.exists()
    } ?: return null

    val properties = Properties()
    properties.load(file.inputStream())
    return properties.getProperty(key)
}



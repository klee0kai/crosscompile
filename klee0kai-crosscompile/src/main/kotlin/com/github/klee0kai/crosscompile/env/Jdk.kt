package com.github.klee0kai.crosscompile.env

import org.gradle.api.Project
import java.io.File

fun Project.guessJdk(): String? {
    return listOf(
        //https://www.jetbrains.com/help/idea/gradle-jvm-selection.html#jdk_existing_project
        readProperty("org.gradle.java.home", fileName = gradleProperties),
        System.getenv()["JAVA_HOME"],
        System.getProperty("java.home")
    ).firstOrNull {
        it != null && File(it).exists()
    }
}
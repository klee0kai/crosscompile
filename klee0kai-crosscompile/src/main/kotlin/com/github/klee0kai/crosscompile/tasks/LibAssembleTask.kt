package com.github.klee0kai.crosscompile.tasks

import com.github.klee0kai.crosscompile.bashtask.BashBuildTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.language.base.plugins.LifecycleBasePlugin

fun Project.dependsOnAssembleTask(libArchTasks: BashBuildTask): Task? {
    if (libArchTasks.subName == null)
        return null

    val assembleTaskName = libArchTasks.groupName
    val assembleTask = tasks.findByName(assembleTaskName)
        ?: tasks.register(assembleTaskName, DefaultTask::class.java) {
            group = LifecycleBasePlugin.BUILD_GROUP
        }.get()

    assembleTask.dependsOn(libArchTasks)
    return assembleTask
}
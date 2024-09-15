package com.example.helloworld.listener

import com.example.helloworld.service.MyBackgroundService
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.ui.Messages

/**
 * Invoked on opening a project.
 */
internal class ProjectOpenStartupActivity : ProjectActivity, DumbAware {
    override suspend fun execute(project: Project) {
        val projectCountingService = project.getService(MyBackgroundService::class.java)

    }
}
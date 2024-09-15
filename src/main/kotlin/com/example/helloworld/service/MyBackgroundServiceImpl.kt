package com.example.helloworld.service

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.util.*
import kotlin.concurrent.fixedRateTimer

internal class MyBackgroundServiceImpl(private val project: Project) : MyBackgroundService {
    init {
        startBackgroundTask()
    }

    override fun startBackgroundTask() {
        val projectName = project.name
        println("Running background task...")
    }
}

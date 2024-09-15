package com.example.helloworld.service

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import java.util.*
import kotlin.concurrent.fixedRateTimer

interface MyBackgroundService {
    fun startBackgroundTask() {}
}

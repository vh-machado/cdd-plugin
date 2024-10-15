package com.example.cddplugin.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.serialization.json.Json

class CodeAnalyzer: VirtualFileReader() {
    fun analyzeCognitiveDrivenDevelopmentMetrics(project: Project, currentVirtualFile: VirtualFile) {
        println("Current file: $currentVirtualFile")

        val cddConfigText = readConfigFile(project)
        val codeText = readVirtualFile(currentVirtualFile)

        if (cddConfigText != null) {
            val groqChat = GroqChat()

            val responseMetrics: Map<String, Int>? = groqChat.fetchCognitiveDrivenDevelopmentAnalysis(cddConfigText, codeText)

            val cddConfigJson = Json.decodeFromString<Config>(cddConfigText)

            responseMetrics?.forEach { (rule, cost) ->
                println("$rule: $cost")

                if(cost > cddConfigJson.limit) {
                    showNotification(project, "CDD Limit Exceeded", "$rule exceeded the limit!")
                }
            }
        }
    }
}
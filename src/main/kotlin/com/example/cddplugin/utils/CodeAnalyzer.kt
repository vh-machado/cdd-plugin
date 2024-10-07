package com.example.cddplugin.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class CodeAnalyzer: VirtualFileReader() {
    fun analyzeCognitiveDrivenDevelopmentMetrics(project: Project, currentVirtualFile: VirtualFile) {
        println("Current file: $currentVirtualFile")

        val cddConfig = readConfigFile(project)
        val codeText = readVirtualFile(currentVirtualFile)

        val groqChat = GroqChat()
        groqChat.fetchCognitiveDrivenDevelopmentAnalysis(cddConfig, codeText)
    }
}
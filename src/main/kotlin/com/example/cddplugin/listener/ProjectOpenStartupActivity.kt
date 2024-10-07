package com.example.cddplugin.listener

import com.example.cddplugin.utils.CodeAnalyzer
import com.example.cddplugin.utils.VirtualFileReader
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFile

internal class ProjectOpenStartupActivity : ProjectActivity, DumbAware, VirtualFileReader() {
    override suspend fun execute(project: Project) {
        ProjectRootManager.getInstance(project).fileIndex.iterateContent(ContentIterator(fun (virtualFile:VirtualFile): Boolean {
            val codeAnalyzer = CodeAnalyzer()
            codeAnalyzer.analyzeCognitiveDrivenDevelopmentMetrics(project, virtualFile)

            return true
        }))
    }
}
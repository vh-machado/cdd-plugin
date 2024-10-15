package com.example.cddplugin.listener

import com.example.cddplugin.utils.CodeAnalyzer
import com.example.cddplugin.utils.VirtualFileReader
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.isFile

internal class ProjectOpenStartupActivity : ProjectActivity, DumbAware, VirtualFileReader() {
    override suspend fun execute(project: Project) {
        val projectFileIndex = ProjectRootManager.getInstance(project).fileIndex

        ReadAction.run<Throwable> {
            projectFileIndex.iterateContent(ContentIterator(fun(virtualFile: VirtualFile): Boolean {
                if (projectFileIndex.isInSource(virtualFile) && virtualFile.isFile) {
                    val codeAnalyzer = CodeAnalyzer()
                    codeAnalyzer.analyzeCognitiveDrivenDevelopmentMetrics(project, virtualFile)
                }

                return true
            }))
        }
    }
}
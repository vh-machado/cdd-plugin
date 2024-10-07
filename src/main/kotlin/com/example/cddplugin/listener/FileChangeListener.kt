package com.example.cddplugin.listener
import com.example.cddplugin.utils.CodeAnalyzer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

class FileChangeListener(private val project: Project): BulkFileListener {
    override fun after(events: List<VFileEvent>) {
        val editorManager = FileEditorManager.getInstance(project)
        val virtualFile = editorManager.selectedFiles.firstOrNull()

        if (virtualFile != null) {
            val codeAnalyzer = CodeAnalyzer()
            codeAnalyzer.analyzeCognitiveDrivenDevelopmentMetrics(project, virtualFile)
        }
    }
}
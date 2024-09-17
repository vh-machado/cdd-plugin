package com.example.helloworld.listener
import com.example.helloworld.utils.VirtualFileReader
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

class FileChangeListener(private val project: Project): BulkFileListener, VirtualFileReader() {
    override fun after(events: List<VFileEvent>) {
        val editorManager = FileEditorManager.getInstance(project)
        val virtualFile = editorManager.selectedFiles.firstOrNull()

        if (virtualFile != null) {
            readVirtualFile(project, virtualFile)
        }
    }
}
package com.example.helloworld.listener
import com.example.helloworld.utils.VirtualFileReader
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiIfStatement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.utils.vfs.getPsiFile

class FileChangeListener(private val project: Project): BulkFileListener, VirtualFileReader() {
    override fun after(events: List<VFileEvent>) {
        println("codigo atualizado")

        val editorManager = FileEditorManager.getInstance(project)
        val virtualFile = editorManager.selectedFiles.firstOrNull()
        var psiFile: PsiFile? = null

        if (virtualFile != null) {
            readVirtualFile(project, virtualFile)
        }
    }
}
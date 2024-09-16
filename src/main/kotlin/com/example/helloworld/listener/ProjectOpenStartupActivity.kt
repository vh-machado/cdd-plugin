package com.example.helloworld.listener

import com.example.helloworld.utils.VirtualFileReader
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiIfStatement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil


/**
 * Invoked on opening a project.
 */
internal class ProjectOpenStartupActivity : ProjectActivity, DumbAware, VirtualFileReader() {
    override suspend fun execute(project: Project) {
        //val editorManager = FileEditorManager.getInstance(project)
        //val virtualFile = editorManager.selectedFiles.firstOrNull()

        ProjectRootManager.getInstance(project).fileIndex.iterateContent(ContentIterator(fun (virtualFile:VirtualFile): Boolean {
            readVirtualFile(project, virtualFile)
            return true
        }))
    }
}
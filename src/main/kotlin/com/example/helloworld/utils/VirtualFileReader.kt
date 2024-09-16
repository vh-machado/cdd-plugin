package com.example.helloworld.utils

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiIfStatement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil

open class VirtualFileReader {
    protected fun readVirtualFile(project: Project, virtualFile: VirtualFile) {
        var psiFile: PsiFile? = null
        println(virtualFile)

        ReadAction.run<Throwable> {
            psiFile = PsiManager.getInstance(project).findFile(virtualFile)

            if (psiFile != null) {
                val ifStatements = PsiTreeUtil.collectElementsOfType(psiFile, PsiIfStatement::class.java)
                println("quantidade de if = " + ifStatements.size)
            }

        }
    }
}

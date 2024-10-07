package com.example.cddplugin.utils

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.isFile

open class VirtualFileReader {
    protected fun readVirtualFile(virtualFile: VirtualFile): String? {
        var file: String? = null

        if (virtualFile.isFile) {
            file = VfsUtil.loadText(virtualFile)
        }

        return file
    }

    protected fun readConfigFile(project: Project): String? {
        val relativeFilePath = "src/config.json"
        var file: String? = null

        ReadAction.run<Throwable> {
            val basePath: String? = project.basePath

            if (basePath != null) {
                val absoluteFilePath = "$basePath/$relativeFilePath"
                val pathVirtualFile: VirtualFile? = LocalFileSystem.getInstance().findFileByPath(absoluteFilePath)

                if (pathVirtualFile != null && pathVirtualFile.exists()) {
                    file = readVirtualFile(pathVirtualFile)
                } else {
                    println("Arquivo não encontrado: $relativeFilePath")
                }
            } else {
                println("Caminho base do projeto não encontrado")
            }
        }

        return file
    }
}

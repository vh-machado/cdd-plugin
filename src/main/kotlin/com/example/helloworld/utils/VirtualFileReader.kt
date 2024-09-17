package com.example.helloworld.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiIfStatement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil

open class VirtualFileReader {
    protected fun readVirtualFile(project: Project, virtualFile: VirtualFile) {
        println(virtualFile)

        ReadAction.run<Throwable> {
            val relativeFilePath = "src/config.json"

            val basePath: String? = project.basePath

            if (basePath != null) {
                val absoluteFilePath = "$basePath/$relativeFilePath"
                val pathVirtualFile: VirtualFile? = LocalFileSystem.getInstance().findFileByPath(absoluteFilePath)

                if (pathVirtualFile != null && pathVirtualFile.exists()) {
                    val inputStream = pathVirtualFile.inputStream
                    val objectMapper = jacksonObjectMapper().registerKotlinModule()

                    val data: Config =
                        inputStream.use { objectMapper.readValue(it, object : TypeReference<Config>() {}) }
                    val rules: List<Rule> = data.rules

                    val psiFile: PsiFile? = PsiManager.getInstance(project).findFile(virtualFile)

                    if(psiFile != null) {
                        if(rules.any { it.name == "IF_STATEMENT" }) {
                            val ifStatements = PsiTreeUtil.collectElementsOfType(psiFile, PsiIfStatement::class.java)
                            val rule: Rule = rules.find { it.name == "IF_STATEMENT" }!!
                            println("IF_STATEMENT => ${ifStatements.size} (amount) * ${rule.cost} (cost) = ${ifStatements.size * rule.cost}")
                        }

                        if(rules.any { it.name == "ANNOTATION" }) {
                            val annotations = PsiTreeUtil.collectElementsOfType(psiFile, PsiAnnotation::class.java)
                            val rule: Rule = rules.find { it.name == "ANNOTATION" }!!
                            println("ANNOTATION => ${annotations.size} (amount) * ${rule.cost} (cost) = ${annotations.size * rule.cost}")
                            println("")
                        }
                    }
                } else {
                    println("Arquivo não encontrado: $relativeFilePath")
                }
            } else {
                println("Caminho base do projeto não encontrado")
            }
        }
    }
}

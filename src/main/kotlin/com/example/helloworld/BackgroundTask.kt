package com.example.helloworld

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class BackgroundTask(project: Project?) : Task.Backgroundable(project, "Running Background Task") {
    override fun run(indicator: ProgressIndicator) {
        // Definir a barra de progresso
        indicator.text = "Executing background task..."

        // Exemplo de uma tarefa longa
        for (i in 1..5) {
            if (indicator.isCanceled) return
            Thread.sleep(1000)  // Simula um processo em segundo plano
            println("Step $i completed")
        }

        // Exibir mensagem quando a tarefa for concluída
        ApplicationManager.getApplication().invokeLater {
            Messages.showMessageDialog(
                "Background task completed!",
                "Info",
                Messages.getInformationIcon()
            )
        }
    }
}

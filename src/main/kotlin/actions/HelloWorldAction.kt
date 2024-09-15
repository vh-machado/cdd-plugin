package actions

import com.esotericsoftware.kryo.kryo5.serializers.FieldSerializer.NotNull
import com.example.helloworld.BackgroundTask
import com.example.helloworld.service.MyBackgroundService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.Messages
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.openapi.project.Project

class HelloWorldAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        println(event.getData(LangDataKeys.EDITOR)?.document?.text)
        println("Line Count: " + event.getData(LangDataKeys.EDITOR)?.document?.lineCount)

        val psiFile = event.getData(CommonDataKeys.PSI_FILE)

        val editor = event.getData(CommonDataKeys.EDITOR)
        val offset = editor?.caretModel?.offset

        var psiElement: PsiElement
        var message = ""

        if (offset == null) {
            message = "No PSI Element detected"
        } else {
            psiElement = psiFile?.findElementAt(offset)!!
            message = "PSI Element: $psiElement"
        }

        println(message)

        val project: Project? = event.project
        val projectService = project?.service<MyBackgroundService>()

        // Rodar a tarefa em background
        BackgroundTask(project).queue()

        /*
        val infoBuilder = StringBuilder()
        infoBuilder.append("Element at caret: ").append(psiElement).append("\n");

        if (psiElement != null) {
            val containingMethod = PsiTreeUtil.getParentOfType(psiElement, PsiMethod::class.java)
            infoBuilder
                .append("Containing method: ")
                .append(if (containingMethod != null) containingMethod.getName() else "none")
                .append("\n")
            if (containingMethod != null) {
                val containingClass = containingMethod.getContainingClass()
                infoBuilder
                    .append("Containing class: ")
                    .append(if (containingClass != null) containingClass.getName() else "none")
                    .append("\n")

                infoBuilder.append("Local variables:\n")
                containingMethod.accept(object: JavaRecursiveElementVisitor() {
                    override fun visitLocalVariable(variable: PsiLocalVariable) {
                        super.visitLocalVariable(variable)
                        infoBuilder.append(variable.getName()).append("\n")
                    }
                })
            }
            println(infoBuilder)
        }*/
        /*
        WriteAction.run<Throwable> {
            event.getData(LangDataKeys.EDITOR)?.document?.setText("a")
        }*/

        /*
        Messages.showMessageDialog(
            message,
            "PSI Element No Cursor",
            Messages.getInformationIcon()
        )*/
    }
}

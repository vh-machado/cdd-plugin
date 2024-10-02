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
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GroqMessage(val role: String, val content: String)

@Serializable
data class GroqRequest(val messages: List<GroqMessage>, val model: String)

@Serializable
data class GroqResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage,
    val system_fingerprint: String,
    val x_groq: XGroq
)

@Serializable
data class Choice(
    val index: Int,
    val message: Message,
    val logprobs: String?,
    val finish_reason: String
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class Usage(
    val queue_time: Double,
    val prompt_tokens: Int,
    val prompt_time: Double,
    val completion_tokens: Int,
    val completion_time: Double,
    val total_tokens: Int,
    val total_time: Double
)

@Serializable
data class XGroq(
    val id: String
)

fun groq() {
    runBlocking {
        try {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }

            val groqMessageContent: String = "Responda objetivamente apenas com um json, sem verbosidade, sem explicação e justificativa. Para cada regra, deve ser dado um custo de acordo com a ocorrência da regra. Utilize o seguinte arquivo de configuração:\n" +
                    "{\n" +
                    "  \"limit\": 10,\n" +
                    "  \"rules\": [\n" +
                    "    {\n" +
                    "      \"name\": \"IF_STATEMENT\",\n" +
                    "      \"cost\": 1\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"TRY_CATCH_STATEMENT\",\n" +
                    "      \"cost\": 1\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"SWITCH_STATEMENT\",\n" +
                    "      \"cost\": 1\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"CONDITION\",\n" +
                    "      \"cost\": 1\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"FOR_STATEMENT\",\n" +
                    "      \"cost\": 1\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"FOREACH_STATEMENT\",\n" +
                    "      \"cost\": 1\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"WHILE_STATEMENT\",\n" +
                    "      \"cost\": 1\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"METHOD_SIZE\",\n" +
                    "      \"cost\": 1\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"CONTEXT_COUPLING\",\n" +
                    "      \"cost\": 1,\n" +
                    "      \"parameters\": \"br.com.zup.lms\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n" +
                    "Calcule o custo total para cada regra no seguinte código:\\n\\n\n" +
                    "python\\n# Exemplo de código Python\\nif x > 5:\\n    print(\\\"Maior que 5\\\")\\nif y < 10:\\n    print(\\\"Menor que 10\\\")\\n"

            val groqMessage = GroqMessage("user", groqMessageContent)
            val groqRequest = GroqRequest(listOf(groqMessage), "llama3-8b-8192")

            val dotenv = dotenv()

            val response: HttpResponse = client.post(dotenv["GROQ_API_URL"]) {
                contentType(ContentType.Application.Json)
                bearerAuth(dotenv["GROQ_API_KEY"])
                setBody(groqRequest)
            }

            if (response.status.isSuccess()) {
                val groqResponse: GroqResponse = response.body()

                val content = groqResponse.choices[0].message.content
                println("Resposta da API: $content")
            } else {
                println("Erro na API: ${response.status}")
            }
        } catch (e: Exception) {
            println("Falha ao obter resposta: $e")
        }
    }
}

open class VirtualFileReader {
    protected fun readVirtualFile(project: Project, virtualFile: VirtualFile) {
        println(virtualFile)

        // teste
        groq()
        // teste

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
                            showNotification(project,"${virtualFile}: IF_STATEMENT => ${ifStatements.size} (amount) * ${rule.cost} (cost) = ${ifStatements.size * rule.cost}")
                        }

                        if(rules.any { it.name == "ANNOTATION" }) {
                            val annotations = PsiTreeUtil.collectElementsOfType(psiFile, PsiAnnotation::class.java)
                            val rule: Rule = rules.find { it.name == "ANNOTATION" }!!
                            println("ANNOTATION => ${annotations.size} (amount) * ${rule.cost} (cost) = ${annotations.size * rule.cost}")
                            showNotification(project,"${virtualFile}: ANNOTATION => ${annotations.size} (amount) * ${rule.cost} (cost) = ${annotations.size * rule.cost}")

                        }
                        println("")
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

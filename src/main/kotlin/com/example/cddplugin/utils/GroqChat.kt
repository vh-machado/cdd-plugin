package com.example.cddplugin.utils

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

class GroqChat {
    fun fetchCognitiveDrivenDevelopmentAnalysis(cddConfig: String?, codeText: String?): Map<String, Int>? {
        var responseMetrics: Map<String, Int>? = null

        runBlocking {
            try {
                val client = HttpClient(CIO) {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true })
                    }
                }

                /*val groqMessageContent: String = "Você é um plugin da IDE Intellij capaz de analisar código e a ocorrência de certas estruturas no código. Responda objetivamente apenas com um json, sem verbosidade, sem explicação e justificativa. Para cada regra, deve ser dado um custo de acordo com a ocorrência da regra. Utilize o seguinte arquivo de configuração:\n" +
                        cddConfig +
                        "O arquivo json de configuração fornecido contém objetos com os atributos:\n" +
                        "1. name: Nome da regra que define qual tipo de estrutura deve ser analisada no código\n" +
                        "2. cost: Custo para cada ocorrência dessa regra no código\n"+
                        "O arquivo json de configuração também apresenta o atributo limit, que delimita o limite para o total de custo de cada regra.\n"+
                        "Calcule o custo total para cada regra no seguinte código, se limitando a apenas as regras fornecidas no arquivo de configuração. Se o arquivo não possuir nenhuma das ocorrências, apenas zere o custo, mas ainda retorne o json, sem anotações ou observações.\n" +
                        "Código a ser analisado:\n\n"
                        codeText + "\n\n" +
                        "O json de resposta da análise deve conter o array de objetos a seguinte exata estrutura:\n" +
                        "1. name: Nome da regra analisada\n" +
                        "2. totalCost: Custo total calculado para as ocorrências da regra"
                */

                val groqMessageContent = """
                    Você é um plugin da IDE Intellij capaz de analisar código e a ocorrência de certas estruturas no código. Responda objetivamente apenas com um json, sem verbosidade, sem explicação e justificativa. Para cada regra, deve ser dado um custo de acordo com a ocorrência da regra. Utilize o seguinte arquivo de configuração:
                
                    $cddConfig
                
                    O arquivo json de configuração fornecido contém objetos com os atributos:
                    1. name: Nome da regra que define qual tipo de estrutura deve ser analisada no código.
                    2. cost: Custo para cada ocorrência dessa regra no código.
                
                    O arquivo json de configuração também apresenta o atributo limit, que delimita o limite para o total de custo de cada regra.
                
                    Calcule o custo total para cada regra no seguinte código, se limitando a apenas as regras fornecidas no arquivo de configuração. Se o arquivo não possuir nenhuma das ocorrências, apenas zere o custo, mas ainda retorne o json, sem anotações ou observações.
                
                    Código a ser analisado:
                
                    $codeText
                
                    O json de resposta da análise deve conter atributos com o nome de cada regra, e o valor de cada atributo deve ser o custo total calculado para a respectiva regra.
                
                    Exemplo da estrutura do json de resposta:
                
                    {
                      "IF_STATEMENT": 1,
                      "TRY_CATCH_STATEMENT": 0,
                      "SWITCH_STATEMENT": 0,
                      "CONDITION": 1,
                      "FOR_STATEMENT": 0,
                      "FOREACH_STATEMENT": 0,
                      "WHILE_STATEMENT": 0,
                      "METHOD_SIZE": 1,
                      "CONTEXT_COUPLING": 0
                    }
                """.trimIndent()

                val groqMessage = GroqMessage("user", groqMessageContent)
                val groqRequest = GroqRequest(listOf(groqMessage), "gemma2-9b-it")

                val dotenv = dotenv()

                val response: HttpResponse = client.post(dotenv["GROQ_API_URL"]) {
                    contentType(ContentType.Application.Json)
                    bearerAuth(dotenv["GROQ_API_KEY"])
                    setBody(groqRequest)
                }

                if (response.status.isSuccess()) {
                    val groqResponse: GroqResponse = response.body()

                    val content = groqResponse.choices[0].message.content
                        .replace("```json", "")
                        .replace("```", "")
                    //println("Resposta da API: $content")

                    responseMetrics = Json.decodeFromString<Map<String, Int>>(content)

                } else {
                    println("Erro na API: ${response.status}")
                }
            } catch (e: Exception) {
                println("Falha ao obter resposta: $e")
            }
        }

        return responseMetrics
    }
}
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
    fun fetchCognitiveDrivenDevelopmentAnalysis(cddConfig: String?, codeText: String?) {
        runBlocking {
            try {
                val client = HttpClient(CIO) {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true })
                    }
                }

                val groqMessageContent: String = "Responda objetivamente apenas com um json, sem verbosidade, sem explicação e justificativa. Para cada regra, deve ser dado um custo de acordo com a ocorrência da regra. Utilize o seguinte arquivo de configuração:\n" +
                        cddConfig +
                        "Calcule o custo total para cada regra no seguinte código:\\n\\n\n" +
                        codeText

                val messageContent: String = "Given a code snippet and a JSON file containing the costs of Cognitive Driven Development (CDD) metrics, I want you to analyze the code and calculate the cognitive cost of each metric. The metrics include (but are not limited to) coupling, inheritance, conditional complexity, among others.\n" +
                        "The input JSON contains the names of the metrics and their respective costs. Your task is to analyze the code and, based on the occurrences and characteristics of each metric, multiply the cost of each by the occurrences or degree of impact in the code.\n" +
                        "In the end, you should return a JSON with the names of the metrics and the total calculated cost for each of them, as follows:\n" +
                        "Input JSON:\n" +
                        cddConfig +
                        "Code to be analyzed:\n" +
                        codeText +
                        "Expected output:\n" +
                        "{\n" +
                        "    \"result\": {\n" +
                        "        \"coupling\": 20,\n" +
                        "        \"inheritance\": 0,\n" +
                        "        \"conditional_complexity\": 8\n" +
                        "    }\n" +
                        "}\n" +
                        "\n" +
                        "For each metric:\n" +
                        "1. Calculate the cost based on the occurrences or complexity detected in the code.\n" +
                        "2. Multiply by the cost provided in the input JSON.\n" +
                        "3. Return a JSON with the metric name and the total calculated cost."

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
}
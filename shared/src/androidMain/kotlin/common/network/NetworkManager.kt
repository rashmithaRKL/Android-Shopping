package common.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class NetworkManager {
    private val client = HttpClient()

    suspend fun getRequest(url: String): String {
        return client.get(url).bodyAsText()
    }

    suspend fun postRequest(url: String, body: Any): String {
        return client.post(url) {
            contentType(ContentType.Application.Json)
            body = body
        }.bodyAsText()
    }

    suspend fun deleteRequest(url: String): String {
        return client.delete(url).bodyAsText()
    }

    fun close() {
        client.close()
    }
}

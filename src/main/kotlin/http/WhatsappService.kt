package http

import com.google.gson.JsonObject
import model.OutgoingWaMessage
import retrofit2.http.Body
import retrofit2.http.POST

internal interface WhatsappService {
    @POST("messages")
    suspend fun sendMessage(
        @Body message: OutgoingWaMessage,
    ): JsonObject
}
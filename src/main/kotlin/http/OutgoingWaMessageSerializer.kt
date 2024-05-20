package http

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import model.OutgoingWaMessage

internal class OutgoingWaMessageSerializer : JsonSerializer<OutgoingWaMessage> {
    override fun serialize(
        src: OutgoingWaMessage?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?,
    ): JsonElement {
        require(src != null) { "OutgoingWaMessage cannot be null" }
        require(context != null) { "JsonSerializationContext cannot be null" }
        return when (src) {
            is OutgoingWaMessage.TextMessage -> context.serialize(src, OutgoingWaMessage.TextMessage::class.java)
            is OutgoingWaMessage.Interactive -> context.serialize(src, OutgoingWaMessage.Interactive::class.java)
            is OutgoingWaMessage.Template -> context.serialize(src, OutgoingWaMessage.Template::class.java)
        }
    }
}

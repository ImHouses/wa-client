package model

data class IncomingChatNotification(
    val messages: List<Message>,
    val statusUpdates: List<StatusUpdate>,
) {
    data class StatusUpdate(
        // The chat ID of the customer to keep track of the conversation.
        val recipientId: String,
        // The ID of the message sent to the customer.
        val messageId: String,
        val status: Status,
        val timestamp: Long,
        val errors: List<NotificationError>,
    )

    data class Message(
        val id: String,
        // The Chat ID of the customer to keep track of the conversation.
        val chatVendorTimestamp: Long,
        val timestamp: Long,
        val senderId: String,
        val senderName: String,
        val content: MessageContent,
        val errors: List<NotificationError>,
        val status: Status = Status.SENT,
    ) {
        sealed class MessageContent {
            data class Text(val content: String) : MessageContent()

            data class Image(val mediaId: String) : MessageContent()

            data class InteractiveResponse(val buttonId: String) : MessageContent()

            data class Location(
                val latitude: Double,
                val longitude: Double,
                val name: String?,
                val address: String?,
            ) : MessageContent()

            data class Audio(val mediaId: String) : MessageContent()

            object Unknown : MessageContent()
        }
    }

    enum class Status(val weight: Int) {
        SENT(1),
        DELIVERED(2),
        READ(3),
        UNKNOWN(-1),
    }
}

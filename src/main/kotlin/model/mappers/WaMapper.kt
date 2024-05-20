package model.mappers

import model.IncomingWaError
import model.IncomingWaMessage
import model.IncomingWaMessageChangeDetails
import model.IncomingWaMessageNotification
import okhttp3.internal.toLongOrDefault
import model.IncomingChatNotification
import model.IncomingChatNotification.Message
import model.NotificationError
import java.time.Instant

fun IncomingWaMessageNotification.toDomainModel(): IncomingChatNotification {
    val changes = entries.flatMap { it.changes }.map { it.value }
    val notifications =
        changes.map { details ->
            val messages =
                if (details.contacts != null && details.messages != null) {
                    details.messages.mapIndexed { index, messagePayload ->
                        toDomainModel(messagePayload, details.contacts[index])
                    }
                } else {
                    emptyList()
                }
            IncomingChatNotification(
                messages = messages,
                statusUpdates = details.statuses?.map { it.toDomainModel() } ?: emptyList(),
            )
        }
    return IncomingChatNotification(
        messages = notifications.flatMap { it.messages },
        statusUpdates = notifications.flatMap { it.statusUpdates },
    )
}

fun toDomainModel(
    incomingWaMessage: IncomingWaMessage,
    contact: IncomingWaMessageChangeDetails.Contact,
): Message {
    val content: Message.MessageContent? =
        when (incomingWaMessage.type) {
            "text" ->
                incomingWaMessage.text?.let {
                    Message.MessageContent.Text(content = incomingWaMessage.text.body)
                }
            "image" ->
                incomingWaMessage.image?.let {
                    Message.MessageContent.Image(mediaId = it.id)
                }
            "interactive" ->
                incomingWaMessage.interactive?.let {
                    Message.MessageContent.InteractiveResponse(buttonId = incomingWaMessage.interactive.buttonReply.id)
                }
            "location" ->
                incomingWaMessage.location?.let {
                    Message.MessageContent.Location(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        name = it.name,
                        address = it.address,
                    )
                }
            "audio" ->
                incomingWaMessage.audio?.let {
                    Message.MessageContent.Audio(mediaId = it.id)
                }
            else -> null
        }
    return Message(
        id = incomingWaMessage.id,
        senderId = incomingWaMessage.from,
        chatVendorTimestamp =
            incomingWaMessage
                .timestamp
                .toLongOrDefault(Instant.now().epochSecond),
        timestamp = Instant.now().epochSecond,
        senderName = contact.profile.name,
        content = content ?: Message.MessageContent.Unknown,
        errors = incomingWaMessage.errors?.map { it.toDomainModel() } ?: emptyList(),
    )
}

fun IncomingWaMessageChangeDetails.Status.toDomainModel(): IncomingChatNotification.StatusUpdate {
    return IncomingChatNotification.StatusUpdate(
        recipientId = recipientId,
        messageId = id,
        status =
            when (status) {
                "sent" -> IncomingChatNotification.Status.SENT
                "delivered" -> IncomingChatNotification.Status.DELIVERED
                "read" -> IncomingChatNotification.Status.READ
                else -> IncomingChatNotification.Status.UNKNOWN
            },
        timestamp = timestamp,
        errors = errors?.map { it.toDomainModel() } ?: emptyList(),
    )
}

fun IncomingWaError.toDomainModel(): NotificationError =
    NotificationError(
        code = code,
        title = title,
    )

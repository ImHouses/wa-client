package model

import com.google.gson.annotations.SerializedName

data class IncomingWaMessageNotification(
    @SerializedName("object")
    val webhook: String,
    @SerializedName("entry")
    val entries: List<IncomingWaMessageEntry>,
)

data class IncomingWaMessageEntry(
    val id: String,
    val changes: List<IncomingWaMessageChange>,
)

data class IncomingWaMessageChange(
    val value: IncomingWaMessageChangeDetails,
)

data class IncomingWaMessageChangeDetails(
    val contacts: List<Contact>?,
    val errors: List<IncomingWaError>?,
    val messages: List<IncomingWaMessage>?,
    val statuses: List<Status>?,
) {
    data class Contact(
        @SerializedName("wa_id")
        val waId: String,
        val profile: Profile,
    )

    data class Profile(val name: String)

    data class Status(
        val id: String,
        @SerializedName("recipient_id")
        val recipientId: String,
        val status: String,
        val timestamp: Long,
        val errors: List<IncomingWaError>?,
    )
}

/**
 * This is the message that we receive from Whatsapp.
 *
 * type field can be:
 * - audio
 * - button
 * - document - NOT SUPPORTED ❌
 * - text
 * - image
 * - interactive
 * - order - NOT SUPPORTED ❌
 * - sticker - NOT SUPPORTED ❌
 * - system – for customer number change messages
 * - unknown
 * - video - NOT SUPPORTED ❌
 *
 * reference: https://developers.facebook.com/docs/whatsapp/cloud-api/webhooks/components#messages-object
 */
data class IncomingWaMessage(
    val id: String,
    val from: String,
    val text: Text?,
    val audio: Audio?,
    val button: Button?,
    val context: Context?,
    val image: Image?,
    val interactive: Interactive?,
    val location: Location?,
    val errors: List<IncomingWaError>?,
    val type: String,
    val timestamp: String,
) {
    data class Text(val body: String)

    data class Audio(
        val id: String,
        @SerializedName("mime_type") val mimeType: String,
    )

    data class Button(val payload: String, val text: String)

    data class Context(
        val forwarded: Boolean,
        val from: String,
        val id: String,
    )

    data class Image(
        val caption: String,
        val sha256: String,
        val id: String,
        @SerializedName("mime_type")
        val mimeType: String,
    )

    data class Interactive(
        @SerializedName("button_reply")
        val buttonReply: ButtonReply,
    ) {
        data class ButtonReply(val id: String, val title: String)
    }

    data class Location(
        val latitude: Double,
        val longitude: Double,
        val name: String?,
        val address: String?,
    )
}

data class IncomingWaError(val code: Int, val title: String)

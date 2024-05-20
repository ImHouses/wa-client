package model

import com.google.gson.annotations.SerializedName

sealed class OutgoingWaMessage(
    @SerializedName("messaging_product")
    val messagingProduct: String = "whatsapp",
    @SerializedName("recipient_type")
    val recipientType: String = "individual",
    @SerializedName("to")
    val to: String,
    @SerializedName("type")
    val type: String,
) {
    data class TextMessage(
        val receiver: String,
        @SerializedName("text")
        val text: TextBody,
    ) : OutgoingWaMessage(to = receiver, type = "text") {
        data class TextBody(
            @SerializedName("body")
            val body: String,
        )
    }

    data class Interactive(
        val receiver: String,
        @SerializedName("interactive")
        val content: InteractiveContent,
    ) : OutgoingWaMessage(to = receiver, type = "interactive") {
        data class InteractiveContent(
            @SerializedName("body")
            val body: Body,
            @SerializedName("type")
            val type: String = "button",
            @SerializedName("action")
            val action: Action,
        )

        data class Body(
            @SerializedName("text") val text: String,
        )

        data class Action(
            @SerializedName("buttons")
            val buttons: List<Button>,
        ) {
            init {
                require(buttons.size in 1..3)
            }

            data class Button(
                @SerializedName("type")
                val type: String = "reply",
                @SerializedName("reply")
                val buttonContent: ButtonContent,
            ) {
                init {
                    require(type == "reply")
                }
            }

            data class ButtonContent(
                @SerializedName("title")
                val title: String,
                @SerializedName("id")
                val id: String,
            ) {
                init {
                    require(title.length <= 20)
                    require(id.length <= 256)
                }
            }
        }
    }

    data class Template(
        val receiver: String,
        @SerializedName("name")
        val name: String,
    ) : OutgoingWaMessage(to = receiver, type = "template") {
        data class Language(
            @SerializedName("policy")
            val policy: String = "deterministic",
            @SerializedName("code")
            val code: String = "es_MX",
        ) {
            init {
                require(policy == "deterministic")
                require(code == "es_MX")
            }
        }
    }
}

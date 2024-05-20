import com.google.gson.Gson
import com.google.gson.GsonBuilder
import http.OutgoingWaMessageSerializer
import http.WhatsappService
import model.OutgoingWaMessage
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WaClient {
    private var waService: WhatsappService? = null

    fun init(authToken: String, phoneNumber: String, isDebugMode: Boolean = false) {
        val baseUrl = "https://graph.facebook.com/v17.0/$phoneNumber/"

        val gson = getGson()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(getHttpClient(authToken))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        waService = retrofit.create(WhatsappService::class.java)
    }

    suspend fun sendMessage(outgoingWaMessage: OutgoingWaMessage) {
        require(waService != null) { "WhatsappService is not initialized" }
        waService?.sendMessage(outgoingWaMessage)
    }

    private fun getGson(): Gson =  GsonBuilder()
        .registerTypeAdapter(OutgoingWaMessage::class.java, OutgoingWaMessageSerializer())
        .create()

    private fun getHttpInterceptor(): HttpLoggingInterceptor {
        val logger = LoggerFactory.getLogger("WaClient")
        val httpInterceptorLogger = HttpLoggingInterceptor.Logger { message -> logger.info("[OkHttp] {}", message) }
        return HttpLoggingInterceptor(
            logger = httpInterceptorLogger,
        ).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private fun getWaAuthInterceptor(authToken: String): Interceptor {
        return Interceptor { chain ->
            val newRequest: Request =
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()
            chain.proceed(newRequest)
        }
    }

    private fun getHttpClient(authToken: String): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(getWaAuthInterceptor(authToken))
        .addInterceptor(getHttpInterceptor())
        .build()
}
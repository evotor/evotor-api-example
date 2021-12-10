package ru.fhs.evotor.remonline.network

import android.content.Context
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.fhs.evotor.remonline.prefs.AppSettings
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSession

object RetrofitBuilder {
    private const val BASE_URL = "https://fhs-sms.devigro.ru/evotor/"

    lateinit var apiService: ApiService

    fun init(context: Context) {
        apiService = getRetrofit(context).create(ApiService::class.java)
    }

    private fun getRetrofit(context: Context): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ChuckInterceptor(context))
            .addInterceptor(AuthInterceptor(AppSettings(context)))
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    class AuthInterceptor constructor(private val appSettings: AppSettings) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val token: String = appSettings.apiKey

            val request = original.newBuilder()
                .header("Authorization", "Bearer $token")
                .method(original.method(), original.body())
                .build()

            return chain.proceed(request)
        }
    }
}
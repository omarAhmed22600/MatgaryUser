package com.brandsin.user.network

import com.brandsin.user.database.ApiInterface
import com.brandsin.user.model.constants.Params
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


// private const val BASE_URL = "https://hagaty-app.com/"
// private const val BASE_URL = "https://backend.brandsin.net/"
// private const val BASE_URL = "https://dev.brandsin.net/"

fun getHeaderInterceptor(): Interceptor {
    return object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request =
                chain.request().newBuilder()
                    .build()
            return chain.proceed(request)
        }
    }
}

private fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .apply {
            if (/*BuildConfig.DEBUG*/true) {
                this.addInterceptor(
                    HttpLoggingInterceptor()
                        .setLevel(Level.BODY)
                )
            }
        }
        .readTimeout(100, TimeUnit.SECONDS)
        .connectTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(100, TimeUnit.SECONDS)
        .build()
}

private val retrofitBuilder = Retrofit.Builder()
    .client(createOkHttpClient())
    .baseUrl(Params.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create()) // CoroutineCallAdapterFactory()
    .build()

object RetrofitBuilder {
    val API_SERVICE: ApiInterface by lazy {
        retrofitBuilder.create(ApiInterface::class.java)
    }
}




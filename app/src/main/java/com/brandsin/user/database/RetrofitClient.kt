package com.brandsin.user.database

import com.brandsin.user.model.constants.Params
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    fun getApiClient(): Retrofit {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        /*    val okHttpClient = OkHttpClient.Builder() //.addInterceptor(httpLoggingInterceptor)
                    .addInterceptor(object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val original = chain.request()
                            val request = original.newBuilder()
                                .addHeader("X-API-KE", "mykey")
                                .method(original.method, original.body)
                                .build()
                            return chain.proceed(request)
                        }
                    })
                    .build()*/

        val okHttpClient = OkHttpClient.Builder().apply {
            if (/*BuildConfig.DEBUG*/false) {
                this.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
        }
            .addInterceptor(Interceptor { chain ->
                val request: Request =
                    chain.request().newBuilder().addHeader("X-API-KEY", "mykey").build()
                // chain.request().newBuilder().addHeader("X-API-KE", "mykey").build()
                chain.proceed(request)
            })
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(Params.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }
}
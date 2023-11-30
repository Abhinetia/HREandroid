package com.android.hre.api

import android.util.Base64
import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat


object RetrofitClient {

    private val AUTH = "Basic "+ Base64.encodeToString("belalkhan:123456".toByteArray(), Base64.NO_WRAP)


  private const val BASE_URL = "https://hre.netiapps.com/api/"

 //  private const val BASE_URL = "https://admin.hresolutions.in/api/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
                .addHeader("Authorization", AUTH)
                .method(original.method(), original.body())

            val request = requestBuilder.build()

            chain.proceed(request)
        }.build()
    val instance: Api by lazy{
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofit.create(Api::class.java)
    }


val client = OkHttpClient.Builder()
    .build()

    val gson = GsonBuilder()
        .setLenient()
        .create()

    val retrofit = Retrofit.Builder()
        //.baseUrl("https://admin.hresolutions.in/api/")
       .baseUrl("https://hre.netiapps.com/api/")   // https://hre.netiapps.com/api/ =  test api {https://admin.hresolutions.in/api/  = prod api }
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // Add RxJava3 call adapter factory

        .build()

    val api = retrofit.create(Api::class.java)



}
package com.example.monarch.model

import com.example.monarch.model.TimeUsage.ExperimentsInterface
import com.example.monarch.model.TimeUsage.TimeUsageQueryInterface
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {

    companion object {
        private fun retrofitInstance(): Retrofit {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()

            val gson = GsonBuilder()
                .setLenient()
                .create()

            return Retrofit.Builder()
                .baseUrl("http://a0809473.xsph.ru/api/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        val serviceTimeUsage: TimeUsageQueryInterface by lazy { retrofitInstance().create(
            TimeUsageQueryInterface::class.java) }

        val serviceExperiments: ExperimentsInterface by lazy { retrofitInstance().create(
            ExperimentsInterface::class.java) }
    }
}
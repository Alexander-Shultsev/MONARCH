package com.example.monarch.repository

import com.example.monarch.repository.dataClass.TimeUsage.TimeUsageInsert
import com.example.monarch.repository.dataClass.common.MyResponse
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface TimeUsageQuery {
//    @Headers("Content-Type: application/json")

    // добавление промежутка времени использования
    @POST("timeUsage/timeUsageQuery.php")
    suspend fun postTimeUsage(
        @Body body: TimeUsageInsert
    ) : Response<MyResponse>

    // добавление промежутка времени использования
    @FormUrlEncoded
    @POST("timeUsage/timeUsageQuery.php")
    suspend fun postTimeUsage1(
        @Field("startDateTime[]") startDateTime: ArrayList<String>,
        @Field("endDateTime[]") endDateTime: ArrayList<String>,
        @Field("appLabel[]") appLabel: ArrayList<String>,
        @Field("appNameId[]") appNameId: ArrayList<String>,
        @Field("fkUser[]") fkUser: ArrayList<Int>,
    ) : Response<MyResponse>

}
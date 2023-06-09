package com.example.monarch.repository.TimeUsage

import com.example.monarch.repository.dataClass.TimeUsage.TimeUsageDevice
import com.example.monarch.repository.dataClass.common.MyResponse
import retrofit2.Response
import retrofit2.http.*
import java.util.*
import kotlin.collections.ArrayList

interface TimeUsageQueryInterface {
//    @Headers("Content-Type: application/json")

    // добавление промежутка времени использования
    @FormUrlEncoded
    @POST("timeUsage/insertAll.php")
    suspend fun postTimeUsage(
        @Field("startDateTime[]") startDateTime: ArrayList<String>,
        @Field("endDateTime[]") endDateTime: ArrayList<String>,
        @Field("appLabel[]") appLabel: ArrayList<String>,
        @Field("appNameId[]") appNameId: ArrayList<String>,
        @Field("fkUser[]") fkUser: ArrayList<Int>,
    ) : Response<MyResponse>


    // получить данные с сервера о времени использования устройства агрегированные по приложениям
    @GET("timeUsage/select.php")
    suspend fun getTimeUsageDevice(
        @Query("date") date: String,
        @Query("fkUser") fkUser: Int,
    ) : Response<ArrayList<TimeUsageDevice>>
}
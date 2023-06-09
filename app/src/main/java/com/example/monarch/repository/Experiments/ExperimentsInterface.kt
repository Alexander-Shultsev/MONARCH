package com.example.monarch.repository.TimeUsage

import com.example.monarch.repository.dataClass.Experiments.Apps
import com.example.monarch.repository.dataClass.Experiments.ExperimentResults
import com.example.monarch.repository.dataClass.Experiments.Experiments
import com.example.monarch.repository.dataClass.common.MyResponse
import retrofit2.Response
import retrofit2.http.*
import java.util.*
import kotlin.collections.ArrayList

interface ExperimentsInterface {
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


    // получить список экспериментов
    @GET("experiment/selectExperiments.php")
    suspend fun getExperiments(
        @Query("fkUser") fkUser: Int,
    ) : Response<ArrayList<Experiments>>

    // получить список приложений для эксперимента
    @GET("experiment/selectAppForExperiment.php")
    suspend fun getAppForExperiment(
        @Query("fkUser") fkUser: Long,
        @Query("fkExperiment") fkExperiment: Long,
    ) : Response<ArrayList<Apps>>

    // получить список приложений для эксперимента
    @GET("experiment/selectExperimentResult.php")
    suspend fun getExperimentResult(
        @Query("fkUser") fkUser: Int,
        @Query("dateStart") dateStart: String,
        @Query("dateEnd") dateEnd: String,
        @Query("appNameId[]") appNameId: ArrayList<String>,
    ) : Response<ArrayList<ExperimentResults>>
}
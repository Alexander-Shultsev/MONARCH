package com.example.monarch.repository

import com.example.monarch.repository.model.TimeUsage
import retrofit2.Response
import retrofit2.http.*
import java.util.*
import kotlin.collections.ArrayList

interface Query {
    @Headers("Content-Type: application/json")

    @GET("posts")
    suspend fun getPosts() : Response<ArrayList<TimeUsage>>

//    @POST("posts")
//    suspend fun postPost(
//        @Body body: TimeUsage
//    ) : Response<TimeUsage2>

}
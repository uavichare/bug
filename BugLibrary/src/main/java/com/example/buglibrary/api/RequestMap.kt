package com.example.buglibrary.api

import com.example.buglibrary.data.Poi
import com.example.buglibrary.data.ResultResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface RequestMap {

    @POST("/poisroute/getnewpois")
    suspend fun fetchPois(
        @Body body: Map<String, String>
    ): Response<ResultResponse<Poi>>

    @POST
    suspend fun addFavourite(
        @Url url: String,
        @Body body: Map<String, String>
    ): Response<ResultResponse<Poi>>

    @POST
    suspend fun removeFavourite(
        @Url url: String,
        @Body body: Map<String, String>
    ): Response<ResultResponse<Poi>>

    @POST
    suspend fun fetchFav(
        @Url url: String,
        @Body body: Map<String, String>
    ): Response<ResultResponse<Poi>>
}
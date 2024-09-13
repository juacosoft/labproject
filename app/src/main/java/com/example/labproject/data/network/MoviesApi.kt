package com.example.labproject.data.network

import com.example.labproject.data.network.models.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {

    @GET("movie/top_rated?language=en-US")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int
    ): Response<MoviesResponse>
}
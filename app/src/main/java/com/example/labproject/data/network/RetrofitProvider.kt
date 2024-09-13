package com.example.labproject.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    const val API_KEY = "ea000af542ac38f2dfcbf04eeae3237d"


    private fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(AppMoviesInterceptor())
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getMoviesApi(): MoviesApi {
        return provideRetrofit().create(MoviesApi::class.java)
    }

}
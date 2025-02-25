package com.example.labproject.data.network

class MoviesRepository(
    private val moviesApi: MoviesApi
) {

    suspend fun getTopRatedMovies(page: Int) = moviesApi.getTopRatedMovies(page)

    suspend fun getPopularMovies(page: Int) = moviesApi.getPopularMovies(page)
}
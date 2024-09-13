package com.example.labproject.domain.entity

data class MovieEntity(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val backdrop_path: String,
    val release_date: String,
    val vote_average: Double
)

data class MoviesEntity(
    val page: Int,
    val results: List<MovieEntity>,
    val total_pages: Int,
    val total_results: Int
)
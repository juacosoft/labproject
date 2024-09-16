package com.example.labproject.ui.topratedlist.state

import com.example.labproject.domain.entity.MoviesEntity

sealed class TopRatedANDPopularMoviesState(
    val data: MoviesEntity? = null,
    val errorType: TopRatedMoviesErrorType ? = null
) {
    object Loading : TopRatedANDPopularMoviesState()
    object LoadingMore : TopRatedANDPopularMoviesState()
    class Success(data: MoviesEntity) : TopRatedANDPopularMoviesState(data = data)
    class Error(errorType: TopRatedMoviesErrorType) : TopRatedANDPopularMoviesState(errorType = errorType)
}

enum class TopRatedMoviesErrorType {
    NETWORK,
    EMPTY_RESULTS,
    UNKNOWN
}
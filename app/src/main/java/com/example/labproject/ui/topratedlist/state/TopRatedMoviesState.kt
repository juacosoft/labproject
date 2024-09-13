package com.example.labproject.ui.topratedlist.state

import com.example.labproject.domain.entity.MoviesEntity

sealed class TopRatedMoviesState(
    val data: MoviesEntity? = null,
    val errorType: TopRatedMoviesErrorType ? = null
) {
    object Loading : TopRatedMoviesState()
    object LoadingMore : TopRatedMoviesState()
    class Success(data: MoviesEntity) : TopRatedMoviesState(data = data)
    class Error(errorType: TopRatedMoviesErrorType) : TopRatedMoviesState(errorType = errorType)
}

enum class TopRatedMoviesErrorType {
    NETWORK,
    EMPTY_RESULTS,
    UNKNOWN
}
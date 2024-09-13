package com.example.labproject.domain.usecases

import com.example.labproject.domain.entity.MoviesEntity
import com.example.labproject.ui.topratedlist.state.TopRatedMoviesErrorType

sealed class DTopRatedMoviesResponse {
    object Loading : DTopRatedMoviesResponse()
    data class Success(val data: MoviesEntity) : DTopRatedMoviesResponse()
    data class Error(val errorType: TopRatedMoviesErrorType) : DTopRatedMoviesResponse()
}
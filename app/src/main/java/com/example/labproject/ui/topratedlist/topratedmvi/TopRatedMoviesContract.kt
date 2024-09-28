package com.example.labproject.ui.topratedlist.topratedmvi

import com.example.labproject.domain.entity.MovieEntity
import com.example.labproject.ui.topratedlist.state.TopRatedMoviesErrorType

interface TopRatedMoviesContract {
    data class UiState(
        val movies: List<MovieEntity> = emptyList(),
        val currentPage: Int = 0,
        val cantLoadMore: Boolean = false,
        val isLoading: Boolean = false,
        val isLoadingMore: Boolean = false,
        val error: TopRatedMoviesErrorType? = null
    )

    sealed class UiEvent {
        object LoadTopRatedMovies: UiEvent()
        object LoadMoreTopRatedMovies: UiEvent()

        data class OnClickItemMovie(val movie: MovieEntity): UiEvent()
    }

    sealed class Effect {
        data class ShowToast(val message: String): Effect()

        data class NavigateToDetail(val movie: MovieEntity): Effect()
    }
}
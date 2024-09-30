package com.example.labproject.ui.topratedlist.topratedmvi

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.viewModelScope
import com.example.labproject.data.network.RetrofitProvider
import com.example.labproject.domain.basic.BasicErrorType
import com.example.labproject.domain.basic.BasicResourceData
import com.example.labproject.domain.entity.MovieEntity
import com.example.labproject.domain.usecases.GetTopRatedMoviesUseCase
import com.example.labproject.ui.base.MVIBaseViewModel
import com.example.labproject.ui.topratedlist.state.TopRatedMoviesErrorType
import kotlinx.coroutines.launch

class TopRatedMoviesMVIViewModel: MVIBaseViewModel<TopRatedMoviesContract.UiState, TopRatedMoviesContract.UiEvent, TopRatedMoviesContract.Effect>()  {

    private val retrofitApi = RetrofitProvider.getMoviesApi()

    @VisibleForTesting
    var getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase

    var currentPage: Int = 0
        private set
    private var totalPages: Int = 1

    init {
        getTopRatedMoviesUseCase = GetTopRatedMoviesUseCase(retrofitApi)
    }

    override fun handleEvent(event: TopRatedMoviesContract.UiEvent) {
        when(event){
            TopRatedMoviesContract.UiEvent.LoadMoreTopRatedMovies -> loadMoreTopRatedMovies()
            TopRatedMoviesContract.UiEvent.LoadTopRatedMovies -> loadtopRatedMovies()
            is TopRatedMoviesContract.UiEvent.OnClickItemMovie -> clickOnItemMovie(event.movie)
        }
    }

    // region process business logic
    private fun loadtopRatedMovies() {
        viewModelScope.launch {
            currentPage = 1
            setState(TopRatedMoviesContract.UiState(isLoading = true))
            getTopRatedMoviesUseCase(page = currentPage).collect { response ->
                when(response){
                    is BasicResourceData.Error ->
                        setState(TopRatedMoviesContract.UiState(error = processError(response.errorType)))
                    is BasicResourceData.Success -> {
                        currentPage = response.data?.page ?: 1
                        totalPages = response.data?.total_pages ?: 1

                        setState(TopRatedMoviesContract.UiState(
                            movies = response.data?.results?: emptyList(),
                            currentPage = currentPage,
                            cantLoadMore = currentPage < totalPages
                        ))
                    }
                }
            }
        }
    }

    private fun loadMoreTopRatedMovies() {
        if (currentPage >= totalPages) return
        viewModelScope.launch {
            currentPage ++
            val currentList = uiState.value?.movies ?: emptyList()
            setState(TopRatedMoviesContract.UiState(isLoadingMore = true, movies = currentList))
            getTopRatedMoviesUseCase(page = currentPage).collect { response ->
                when(response) {
                    is BasicResourceData.Error -> {
                        setState(TopRatedMoviesContract.UiState(error = processError(response.errorType)))
                        currentPage--
                    }
                    is BasicResourceData.Success -> {
                        currentPage = response.data?.page ?: 1
                        totalPages = response.data?.total_pages ?: 1
                        val newMovies = response.data?.results ?: emptyList()
                        val cantLoadMore = currentPage < totalPages
                        setState(TopRatedMoviesContract.UiState(
                            movies = currentList + newMovies,
                            currentPage = currentPage,
                            cantLoadMore = cantLoadMore
                        ))
                    }
                }
            }
        }
    }

    // endregion business logic

    // region sending effects

    private fun clickOnItemMovie(movieEntity: MovieEntity) {
        viewModelScope.launch {
            val effect = TopRatedMoviesContract.Effect.NavigateToDetail(movie = movieEntity)
            sendEffect(effect)
        }
    }

    // endregion sending effects

    // region internal process data

    private fun processError(errorType: BasicErrorType?): TopRatedMoviesErrorType {
        return when(errorType){
            BasicErrorType.NETWORK -> TopRatedMoviesErrorType.NETWORK
            BasicErrorType.EMPTY_RESULTS -> TopRatedMoviesErrorType.EMPTY_RESULTS
            BasicErrorType.UNKNOWN -> TopRatedMoviesErrorType.UNKNOWN
            else -> TopRatedMoviesErrorType.UNKNOWN
        }
    }

    // endregion internal process data
}
package com.example.labproject.ui.topratedlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labproject.data.network.RetrofitProvider
import com.example.labproject.domain.basic.BasicErrorType
import com.example.labproject.domain.basic.BasicResourceData
import com.example.labproject.domain.usecases.GetTopRatedMoviesUseCase
import com.example.labproject.ui.topratedlist.state.TopRatedMoviesErrorType
import com.example.labproject.ui.topratedlist.state.TopRatedMoviesState
import com.example.labproject.ui.uttils.ImageCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TopRatedMoviesViewModel: ViewModel() {

    private val retrofitApi = RetrofitProvider.getMoviesApi()
    private var getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase

    private var _moviesState: MutableStateFlow<TopRatedMoviesState> = MutableStateFlow(TopRatedMoviesState.Loading)
    val moviesState = _moviesState.asStateFlow()

    private var currentPage: Int = 0
    private var totalPages: Int = 1

    init {
        getTopRatedMoviesUseCase = GetTopRatedMoviesUseCase(retrofitApi)
    }

    fun cantLoadMore(): Boolean {
        val isHasMorePages = currentPage < totalPages
        return isHasMorePages && moviesState.value !is TopRatedMoviesState.LoadingMore && moviesState.value !is TopRatedMoviesState.Loading
    }

    fun loadTopRatedMovies() {
        viewModelScope.launch {
            currentPage = currentPage + 1
            if (currentPage == 1) {
                _moviesState.value = TopRatedMoviesState.Loading
            } else {
                _moviesState.value = TopRatedMoviesState.LoadingMore
            }
            getTopRatedMoviesUseCase(currentPage).collect { response ->
                when(response) {
                    is BasicResourceData.Error -> {
                        currentPage = currentPage--
                        _moviesState.value = TopRatedMoviesState.Error(processError(response.errorType!!))
                    }
                    is BasicResourceData.Success -> {
                        currentPage = response.data?.page ?: 1
                        totalPages = response.data?.total_pages ?: 1
                        _moviesState.value = TopRatedMoviesState.Success(response.data!!)
                    }
                }
            }
        }
    }

    private fun processError(errorType: BasicErrorType): TopRatedMoviesErrorType {
        return when(errorType){
            BasicErrorType.NETWORK -> TopRatedMoviesErrorType.NETWORK
            BasicErrorType.EMPTY_RESULTS -> TopRatedMoviesErrorType.EMPTY_RESULTS
            BasicErrorType.UNKNOWN -> TopRatedMoviesErrorType.UNKNOWN
        }
    }

    fun clearCache() {
        ImageCache.clearCache()
    }
}
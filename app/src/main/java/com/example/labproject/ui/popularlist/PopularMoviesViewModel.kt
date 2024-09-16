package com.example.labproject.ui.popularlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labproject.data.network.RetrofitProvider
import com.example.labproject.domain.basic.BasicErrorType
import com.example.labproject.domain.basic.BasicResourceData
import com.example.labproject.domain.usecases.GetPopularMoviesUseCase
import com.example.labproject.ui.topratedlist.state.TopRatedANDPopularMoviesState
import com.example.labproject.ui.topratedlist.state.TopRatedMoviesErrorType
import com.example.labproject.ui.uttils.ImageCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PopularMoviesViewModel: ViewModel() {

    private val retrofitApi = RetrofitProvider.getMoviesApi()
    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase

    private var _movieState: MutableStateFlow<TopRatedANDPopularMoviesState> = MutableStateFlow(TopRatedANDPopularMoviesState.Loading)
    val moviesState = _movieState.asStateFlow()

    private var currentPage: Int = 0
    private var totalPages: Int = 1

    init {
        getPopularMoviesUseCase = GetPopularMoviesUseCase(retrofitApi)
    }

    fun loadPopularMovies() {
        viewModelScope.launch {
            currentPage = currentPage + 1
            if (currentPage == 1) {
                _movieState.value = TopRatedANDPopularMoviesState.Loading
            } else {
                _movieState.value = TopRatedANDPopularMoviesState.LoadingMore
            }
            getPopularMoviesUseCase(currentPage) .collect{ response ->
                when(response) {
                    is BasicResourceData.Error -> {
                        currentPage = currentPage--
                        _movieState.value = TopRatedANDPopularMoviesState.Error(processError(response.errorType!!))
                    }
                    is BasicResourceData.Success -> {
                        currentPage = response.data?.page ?: 1
                        totalPages = response.data?.total_pages ?: 1
                        _movieState.value = TopRatedANDPopularMoviesState.Success(response.data!!)
                    }
                }
            }
        }
    }

    fun cantLoadMore(): Boolean {
        val isHasMorePages = currentPage < totalPages
        return isHasMorePages && moviesState.value !is TopRatedANDPopularMoviesState.LoadingMore && moviesState.value !is TopRatedANDPopularMoviesState.Loading
    }

    fun processError(errorType: BasicErrorType): TopRatedMoviesErrorType {
        return when(errorType) {
            BasicErrorType.NETWORK -> TopRatedMoviesErrorType.NETWORK
            BasicErrorType.EMPTY_RESULTS -> TopRatedMoviesErrorType.EMPTY_RESULTS
            else -> TopRatedMoviesErrorType.UNKNOWN
        }
    }

    fun clearCache() {
        ImageCache.clearCache()
    }
}
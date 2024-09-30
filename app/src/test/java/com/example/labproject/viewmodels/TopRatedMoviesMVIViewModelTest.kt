package com.example.labproject.viewmodels

import com.example.labproject.domain.basic.BasicResourceData
import com.example.labproject.domain.entity.MovieEntity
import com.example.labproject.domain.entity.MoviesEntity
import com.example.labproject.domain.usecases.GetTopRatedMoviesUseCase
import com.example.labproject.ui.topratedlist.topratedmvi.TopRatedMoviesContract
import com.example.labproject.ui.topratedlist.topratedmvi.TopRatedMoviesMVIViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TopRatedMoviesMVIViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: TopRatedMoviesMVIViewModel

    @MockK
    private lateinit var getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = TopRatedMoviesMVIViewModel().apply {
            getTopRatedMoviesUseCase = this@TopRatedMoviesMVIViewModelTest.getTopRatedMoviesUseCase
        }
    }

    @Test
    fun `test load top-rated movies success`() = runTest {
        // Given
        val mockMoviesEntity = MoviesEntity(
            page = 1,
            results = listOf(mockk<MovieEntity>(), mockk<MovieEntity>()),
            total_pages = 1,
            total_results = 2
        )

        val mockResponse = BasicResourceData.Success(mockMoviesEntity)
        coEvery { getTopRatedMoviesUseCase(any()) } returns flowOf(mockResponse)

        // When
        viewModel.handleEvent(TopRatedMoviesContract.UiEvent.LoadTopRatedMovies)

        // Then
        assertEquals(2, viewModel.uiState.value?.movies?.size)
        assertEquals(1, viewModel.uiState.value?.currentPage)

        coVerify { getTopRatedMoviesUseCase(any()) }
    }
}
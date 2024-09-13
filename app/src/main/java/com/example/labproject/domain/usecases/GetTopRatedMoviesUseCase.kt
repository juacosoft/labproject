package com.example.labproject.domain.usecases

import android.util.Log
import com.example.labproject.data.network.MoviesApi
import com.example.labproject.data.network.MoviesRepository
import com.example.labproject.data.network.models.MoviesResponse
import com.example.labproject.domain.basic.BasicErrorType
import com.example.labproject.domain.basic.BasicResourceData
import com.example.labproject.domain.entity.MovieEntity
import com.example.labproject.domain.entity.MoviesEntity
import com.example.labproject.ui.topratedlist.state.TopRatedMoviesErrorType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetTopRatedMoviesUseCase(
   private val api: MoviesApi
) {

    private val moviesRepository = MoviesRepository(api)

    suspend operator fun invoke(page: Int): Flow<BasicResourceData<MoviesEntity>>  = flow {
        val moviesResponse = moviesRepository.getTopRatedMovies(page)
        if (moviesResponse.isSuccessful) {
            moviesResponse.body()?.let {
                emit(BasicResourceData.Success(mapMoviesResponseToEntity(it)))
            } ?: emit(BasicResourceData.Error(BasicErrorType.EMPTY_RESULTS))
        } else {
            emit(BasicResourceData.Error(BasicErrorType.NETWORK))
        }
    }.catch {
        it.printStackTrace()
        emit(BasicResourceData.Error(BasicErrorType.UNKNOWN))
    }



    private fun mapMoviesResponseToEntity(moviesResponse: MoviesResponse): MoviesEntity {
        return MoviesEntity(
            page = moviesResponse.page,
            results = moviesResponse.results.map { movieModel ->
                MovieEntity(
                    id = movieModel.id,
                    title = movieModel.title,
                    overview = movieModel.overview,
                    poster_path = movieModel.poster_path,
                    backdrop_path = movieModel.backdrop_path,
                    release_date = movieModel.release_date,
                    vote_average = movieModel.vote_average
                )
            },
            total_pages = moviesResponse.total_pages,
            total_results = moviesResponse.total_results
        )
    }


}
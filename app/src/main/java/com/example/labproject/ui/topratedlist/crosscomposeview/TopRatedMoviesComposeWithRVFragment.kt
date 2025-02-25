package com.example.labproject.ui.topratedlist.crosscomposeview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.labproject.R
import com.example.labproject.domain.entity.MovieEntity
import com.example.labproject.ui.topratedlist.TopRatedMoviesViewModel
import com.example.labproject.ui.topratedlist.state.TopRatedANDPopularMoviesState
import com.google.android.material.progressindicator.CircularProgressIndicator

class TopRatedMoviesComposeWithRVFragment : Fragment() {

    private val viewModel: TopRatedMoviesViewModel by lazy {
        ViewModelProvider(this).get(TopRatedMoviesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    TopRatedMoviesView()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.moviesState.value !is TopRatedANDPopularMoviesState.Success) {
            viewModel.loadTopRatedMovies()
        }
    }

    @Composable
    private fun TopRatedMoviesView() {

        val topRatedMoviesState by viewModel.moviesState.collectAsState()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = stringResource(id = R.string.str_top_rated_movies))
            }
            when(topRatedMoviesState) {
                is TopRatedANDPopularMoviesState.Error -> {
                    Log.d("FragmenttopRated", "observeflowData: ${topRatedMoviesState.errorType}")
                }
                TopRatedANDPopularMoviesState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AndroidView(factory = {
                                val component = CircularProgressIndicator(it)
                                component.isIndeterminate = true
                                component
                            })
                        }
                    }
                }
                is TopRatedANDPopularMoviesState.Success, TopRatedANDPopularMoviesState.LoadingMore -> {
                    item {
                        val movies = topRatedMoviesState.data?.results ?: emptyList()
                        RecyclerViewContent(movies) { movieSelected ->
                            Toast.makeText(requireContext(), movieSelected.title, Toast.LENGTH_SHORT).show()
                        }
                    }
                    item {
                        if (topRatedMoviesState is TopRatedANDPopularMoviesState.LoadingMore) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                AndroidView(factory = {
                                    val component = CircularProgressIndicator(it)
                                    component.isIndeterminate = true
                                    component

                                })
                            }

                        } else {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(onClick = {
                                    if (viewModel.cantLoadMore()) {
                                        viewModel.loadTopRatedMovies()
                                    }
                                }) {
                                    val nextPage = topRatedMoviesState.data?.page?.plus(1) ?: 0
                                    Text(text = stringResource(id = R.string.load_page, nextPage.toString()))
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @Composable
    private fun RecyclerViewContent(
        movies: List<MovieEntity>,
        onMovieClick: (MovieEntity) -> Unit
    ) {
        val movieList = remember { mutableStateListOf<MovieEntity>() }
        movieList.addAll(movies)

        AndroidView(
            factory = {
                TopRatedMoviesRecycler(it)
            },
            update = {
                it.setData(
                    items = movieList,
                    onMovieSelected = onMovieClick,
                )
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearCache()
    }
}
package com.example.labproject.ui.topratedlist.crosscomposeview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.labproject.ui.topratedlist.state.TopRatedMoviesState
import com.google.android.material.progressindicator.CircularProgressIndicator

class TopRatedMoviesComposeWithIVFragment: Fragment() {

    private val viewModel: TopRatedMoviesViewModel by lazy {
        ViewModelProvider(this).get(TopRatedMoviesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    TopRatedMoviesView()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearCache()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.moviesState.value !is TopRatedMoviesState.Success) {
            viewModel.loadTopRatedMovies()
        }
    }

    @Composable
    private fun TopRatedMoviesView() {
        val moviesState by viewModel.moviesState.collectAsState()
        val movieList = remember { mutableStateListOf<MovieEntity>() }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(id = R.string.str_top_rated_movies))
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                columns = GridCells.Fixed(3),
            ){
                when(moviesState) {
                    is TopRatedMoviesState.Error ->
                        Log.d("FragmenttopRated", "observeflowData: ${moviesState.errorType}")
                    TopRatedMoviesState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) {
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
                    is TopRatedMoviesState.Success, TopRatedMoviesState.LoadingMore -> {
                        val movies = moviesState.data?.results ?: emptyList()
                        movieList.addAll(movies)
                        items(movieList) { movieItem ->
                            AndroidView(
                                factory = { viewContext ->
                                    ItemMovieView(viewContext)
                                }, update = { component ->
                                    component.setItem(movieItem)
                                },
                                modifier = Modifier.clickable {
                                    Toast.makeText(
                                        context,
                                        "movie: ${movieItem.title}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                        item(
                            span = {
                                GridItemSpan(maxLineSpan)
                            }
                        ) {
                            if (moviesState is TopRatedMoviesState.LoadingMore) {
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
                            if (moviesState is TopRatedMoviesState.Success){
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(onClick = {
                                        if (viewModel.cantLoadMore()) {
                                            viewModel.loadTopRatedMovies()
                                        }
                                    }) {
                                        val nextPage = moviesState.data?.page?.plus(1) ?: 0
                                        Text(text = stringResource(id = R.string.load_page, nextPage.toString()))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.example.labproject.ui.topratedlist.crosscomposeview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.labproject.R
import com.example.labproject.ui.topratedlist.topratedmvi.TopRatedMoviesContract
import com.example.labproject.ui.topratedlist.topratedmvi.TopRatedMoviesMVIViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator

class TopRatedMoviesComposeWithIVFragment: Fragment() {

    private val viewModel: TopRatedMoviesMVIViewModel by viewModels()

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
        if (viewModel.uiState.value?.movies.isNullOrEmpty()) {
            viewModel.handleEvent(TopRatedMoviesContract.UiEvent.LoadTopRatedMovies)
        }
    }

    @Composable
    private fun TopRatedMoviesView() {
        val uiState by viewModel.uiState.collectAsState()
        val uiEffect by viewModel.effect.collectAsState(
            initial = null
        )
        val listState = rememberLazyGridState()
        Scaffold(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 0.dp),
            topBar = {
                Text(text = stringResource(id = R.string.str_top_rated_movies))
            }
        ) { contentPadding ->
            LazyVerticalGrid(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = contentPadding.calculateTopPadding()),
                columns = GridCells.Adaptive(100.dp),
            ) {
                when {
                    uiState?.isLoading == true -> item(span = { GridItemSpan(maxLineSpan) }) {
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

                    uiState?.error != null -> {
                        Log.d("FragmenttopRated", "observeflowData: ${uiState?.error}")
                    }

                    uiState?.movies.isNullOrEmpty() -> {
                        Log.d("FragmenttopRated", "observeflowData: ${uiState?.movies}")
                    }

                    else -> {

                        items(uiState!!.movies) { movieItem ->
                            AndroidView(
                                factory = { viewContext ->
                                    ItemMovieView(viewContext)
                                }, update = { component ->
                                    component.setItem(movieItem) {
                                        viewModel.handleEvent(
                                            TopRatedMoviesContract.UiEvent.OnClickItemMovie(
                                                movieItem
                                            )
                                        )
                                    }
                                }
                            )
                        }
                        if (uiState?.cantLoadMore == true) {
                            item(
                                span = {
                                    GridItemSpan(maxLineSpan)
                                }
                            ){
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(onClick = {
                                        viewModel.handleEvent(TopRatedMoviesContract.UiEvent.LoadMoreTopRatedMovies)
                                    }) {
                                        val nextPage = (uiState?.currentPage?:1) + 1
                                        Text(
                                            text = stringResource(
                                                id = R.string.load_page,
                                                nextPage.toString()
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        if (uiState?.isLoadingMore == true) {
                            item(
                                span = {
                                    GridItemSpan(maxLineSpan)
                                }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AndroidView(
                                        factory = {
                                            val component = CircularProgressIndicator(it)
                                            component.isIndeterminate = true
                                            component
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            uiEffect?.let { effect ->
                when(effect) {
                    is TopRatedMoviesContract.Effect.ShowToast -> Unit

                    is TopRatedMoviesContract.Effect.NavigateToDetail ->
                        Toast.makeText(context, effect.movie.title, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
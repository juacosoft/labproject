package com.example.labproject.ui.topratedlist.onlycompose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labproject.R
import com.example.labproject.domain.entity.MovieEntity
import com.example.labproject.ui.NavControllerProvider
import com.example.labproject.ui.topratedlist.topratedmvi.TopRatedMoviesContract
import com.example.labproject.ui.topratedlist.topratedmvi.TopRatedMoviesMVIViewModel
import com.example.labproject.ui.uttils.ImageCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopRatedMoviesOnlyCompose() {
    val viewModel: TopRatedMoviesMVIViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyGridState()
    val navController = NavControllerProvider.current

    LaunchedEffect(Unit) {
        if (uiState?.movies.isNullOrEmpty()) {
            viewModel.handleEvent(TopRatedMoviesContract.UiEvent.LoadTopRatedMovies)
        }
    }

    // region effects

    HandleEffects(viewModel)

    // endregion

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.str_top_rated_movies),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { contentPadding ->
        LazyVerticalGrid(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = contentPadding.calculateTopPadding()),
            columns = GridCells.Adaptive(100.dp),
        ) {
            // region state
            when {
                uiState?.isLoading == true -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
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
                        ItemMovie(item = movieItem) {
                            viewModel.handleEvent(
                                TopRatedMoviesContract.UiEvent.OnClickItemMovie(
                                    movieItem
                                )
                            )
                        }
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
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
            // endregion
        }
    }
}

@Composable
private fun ItemMovie(item: MovieEntity, onClick: () -> Unit) {
    val urlImage = "https://image.tmdb.org/t/p/w500${item.poster_path}"
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val localContext = LocalContext.current


    LaunchedEffect(urlImage) {
        val cachedImageBitmap = ImageCache.getBitmapFromCache(urlImage)
        if (cachedImageBitmap != null) {
            bitmap = cachedImageBitmap
        } else {
            bitmap = downloadImage(urlImage)
            bitmap?.let { ImageCache.addBitmapToCache(urlImage, it) }
        }
    }

    Box(
        modifier = Modifier
            .padding(5.dp)
            .clickable { onClick() },
    ) {
        Card(
            shape = RoundedCornerShape(7.dp),
            modifier = Modifier
                .size(width = 130.dp, height = 200.dp)
        ) {

            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            } ?: run {
                val bitmapFromResource = BitmapFactory.decodeResource(
                    localContext.resources,
                    R.drawable.placeholder_shape
                )
                bitmapFromResource?.let {
                    Image(
                        bitmap = bitmapFromResource.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
        }
        FilledIconButton(
            onClick = {  },
            modifier = Modifier.align(Alignment.TopEnd).size(20.dp),
            colors = IconButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground,
                disabledContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                containerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun HandleEffects(viewModel: TopRatedMoviesMVIViewModel) {
    val uiEffect by viewModel.effect.collectAsState(
        initial = null
    )
    val context = LocalContext.current
    LaunchedEffect(uiEffect) {
        when(uiEffect) {
            is TopRatedMoviesContract.Effect.ShowToast -> Unit

            is TopRatedMoviesContract.Effect.NavigateToDetail ->
                Toast.makeText(context, (uiEffect as TopRatedMoviesContract.Effect.NavigateToDetail).movie.title, Toast.LENGTH_SHORT).show()

            else -> Unit
        }
    }
}

private suspend fun downloadImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
    val stream = URL(url).openStream()
    val bitmap = BitmapFactory.decodeStream(stream)
    stream.close()
    bitmap
}
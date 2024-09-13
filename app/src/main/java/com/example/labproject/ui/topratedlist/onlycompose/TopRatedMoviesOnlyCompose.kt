package com.example.labproject.ui.topratedlist.onlycompose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.labproject.ui.topratedlist.TopRatedMoviesViewModel
import com.example.labproject.ui.topratedlist.state.TopRatedMoviesState
import com.example.labproject.ui.uttils.ImageCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun TopRatedMoviesOnlyCompose() {
    val viewmodel: TopRatedMoviesViewModel = viewModel()
    val moviesState by viewmodel.moviesState.collectAsState()
    val movieList = remember { mutableStateListOf<MovieEntity>() }
    val localContext = LocalContext.current
    LaunchedEffect(Unit) {
        viewmodel.loadTopRatedMovies()
    }

    DisposableEffect(Unit) {
        onDispose { viewmodel.clearCache() }
    }
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
            columns = GridCells.Fixed(3)
        ) {
            when(moviesState){
                is TopRatedMoviesState.Error -> Unit
                TopRatedMoviesState.Loading -> item(
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
                is TopRatedMoviesState.Success, TopRatedMoviesState.LoadingMore -> {
                    val movies = moviesState.data?.results ?: emptyList()
                    movieList.addAll(movies)
                    items(movieList){
                        ItemMovie(it) {
                            Toast.makeText(localContext, "movie: ${it.title}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    item(
                        span = {
                            GridItemSpan(maxLineSpan)
                        }
                    ) {
                        if(moviesState is TopRatedMoviesState.LoadingMore){
                            Box(
                                modifier = Modifier.padding(horizontal = 30.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        if(moviesState is TopRatedMoviesState.Success){
                            Box(
                                modifier = Modifier.padding(horizontal = 30.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(onClick = {
                                    if (viewmodel.cantLoadMore()) {
                                        viewmodel.loadTopRatedMovies()
                                    }
                                }) {
                                    val nextPage = moviesState.data?.page?.plus(1)
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
    }
}

private suspend fun downloadImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
    val stream = URL(url).openStream()
    val bitmap = BitmapFactory.decodeStream(stream)
    stream.close()
    bitmap
}
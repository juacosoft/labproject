package com.example.labproject.ui.topratedlist.xmlview

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.labproject.R
import com.example.labproject.databinding.ItemMovieBinding
import com.example.labproject.domain.entity.MovieEntity
import com.example.labproject.ui.uttils.ImageCache
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class TopRatedMovieAdapter(
    val onMovieSelected: (MovieEntity) -> Unit
): RecyclerView.Adapter<TopRatedMovieAdapter.TopRatedMovieViewHolder>() {

    private val moviesList: MutableList<MovieEntity> = mutableListOf()

    fun addItems(movies: List<MovieEntity>) {
        moviesList.addAll(movies)
        notifyItemInserted(moviesList.size - 1)
    }

    fun clearList() {
        moviesList.clear()
        notifyDataSetChanged()
    }

    inner class TopRatedMovieViewHolder(
        private var itemBinding: ItemMovieBinding
    ): RecyclerView.ViewHolder(itemBinding.root) {

        @OptIn(DelicateCoroutinesApi::class)
        fun bind(
            movie: MovieEntity
        ) {
            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.poster_path}"

            val cachedImageBitmap = ImageCache.getBitmapFromCache(imageUrl)
            if (cachedImageBitmap != null) {
                itemBinding.movieImage.setImageBitmap(cachedImageBitmap)
            } else {
                itemBinding.movieImage.setImageResource(R.drawable.placeholder_shape)
                GlobalScope.launch {
                    val bitmap = downloadImage(imageUrl)
                    if (bitmap != null) {
                        ImageCache.addBitmapToCache(imageUrl, bitmap)
                        withContext(Dispatchers.Main) {
                            itemBinding.movieImage.setImageBitmap(bitmap)
                        }
                    }
                }
            }
            itemBinding.movieImage.setOnClickListener {
                onMovieSelected(movie)
            }
        }

        private suspend fun downloadImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
            val stream = URL(url).openStream()
            val bitmap = BitmapFactory.decodeStream(stream)
            stream.close()
            bitmap
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopRatedMovieViewHolder {
        val itemBinding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopRatedMovieViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return moviesList.size
    }

    override fun onBindViewHolder(holder: TopRatedMovieViewHolder, position: Int) {
        holder.bind(moviesList[position])
    }


}
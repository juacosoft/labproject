package com.example.labproject.ui.topratedlist.crosscomposeview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.example.labproject.R
import com.example.labproject.domain.entity.MovieEntity
import com.example.labproject.ui.uttils.ImageCache
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class ItemMovieView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    init {
        View.inflate(context, R.layout.item_movie, this)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun setItem(
        item: MovieEntity,
        onClickListener: () -> Unit
    ){

        val imageView = findViewById<ImageView>(R.id.movie_image)
        val imageUrl = "https://image.tmdb.org/t/p/w500${item.poster_path}"
        val cachedImageBitmap = ImageCache.getBitmapFromCache(imageUrl)
        if (cachedImageBitmap != null) {
            imageView.setImageBitmap(cachedImageBitmap)
        } else {
            imageView.setImageResource(R.drawable.placeholder_shape)
            GlobalScope.launch {
                val bitmap = downloadImage(imageUrl)
                if (bitmap != null) {
                    ImageCache.addBitmapToCache(imageUrl, bitmap)
                    withContext(Dispatchers.Main) {
                        imageView.setImageBitmap(bitmap)
                    }
                }
            }
        }

        findViewById<CardView>(R.id.card_view_content).setOnClickListener {
            onClickListener()
        }


    }

    private suspend fun downloadImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
        val stream = URL(url).openStream()
        val bitmap = BitmapFactory.decodeStream(stream)
        stream.close()
        bitmap
    }
}
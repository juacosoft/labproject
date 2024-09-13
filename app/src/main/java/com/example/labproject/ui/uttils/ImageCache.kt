package com.example.labproject.ui.uttils

import android.graphics.Bitmap
import android.util.LruCache

object ImageCache {
    private const val MAX_CACHE_SIZE = 200

    private val cache: LruCache<String, Bitmap> = LruCache(MAX_CACHE_SIZE)

    fun getBitmapFromCache(url: String): Bitmap? {
        return cache.get(url)
    }

    fun addBitmapToCache(url: String, bitmap: Bitmap) {
        if (getBitmapFromCache(url) == null) {
            cache.put(url, bitmap)
        }
    }

    fun clearCache() {
        cache.evictAll()
    }
}
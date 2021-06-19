package com.example.ttk_20.utils

import android.graphics.Bitmap
import android.os.Looper
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.ttk_20.BuildConfig

fun ImageView.glideAvatar(url: String?) {
    Glide.with(this)
        .clear(this)
    Glide.with(this)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}

fun ImageView.glideAvatarLarge(url: String?) {
    Glide.with(this)
        .clear(this)
    Glide.with(this)
        .load("${BuildConfig.BASE_URL}v1/$url")
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .into(this)
}

fun ImageView.glideAvatarLargeWOBaseUrl(fullUrl: String?) {
    Glide.with(this)
        .load(fullUrl)
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}
fun ImageView.loadImage(resource:Int){
    Glide.with(this)
        .load(resource)
        .into(this)

}

fun ImageView.setBitmapFrom(url: String) {
    val imageView = this
    RetrofitImage.getBitmapFrom(url) {
        val bitmap: Bitmap? = if (it != null) it else {
            // create empty bitmap
            val w = 1
            val h = 1
            val conf = Bitmap.Config.ARGB_8888
            Bitmap.createBitmap(w, h, conf)
        }

        Looper.getMainLooper().run {
            imageView.setImageBitmap(bitmap!!)
        }
    }
}
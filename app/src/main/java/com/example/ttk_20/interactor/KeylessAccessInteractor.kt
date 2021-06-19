package com.example.ttk_20.interactor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.ttk_20.data.network.Api
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import timber.log.Timber
import javax.inject.Inject


class KeylessAccessInteractor @Inject constructor(
    private val api: Api
) {

    fun openDoor(): Completable = api.openDoor()

    fun checkDoorClosed(): Single<Boolean> = api.checkDoorClosed()

    fun getQrCode(): Single<Bitmap> = api.getQrCode()
        .map { body ->
            val bytes = body.bytes()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
}
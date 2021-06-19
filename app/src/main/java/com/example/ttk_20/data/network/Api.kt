package com.example.ttk_20.data.network

import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart

interface Api {

    companion object {
        const val API_PATH = "v1"
    }

    @GET("$API_PATH/open")
    fun openDoor() : Completable

    @GET("$API_PATH/locked")
    fun checkDoorClosed() : Single<Boolean>

    @GET("$API_PATH/code")
    fun getQrCode() : Single<ResponseBody>
}
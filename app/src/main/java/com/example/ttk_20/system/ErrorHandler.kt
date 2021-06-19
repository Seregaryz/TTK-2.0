package com.example.ttk_20.system

import android.annotation.SuppressLint
import com.example.ttk_20.data.error.ServerError
import com.example.ttk_20.model.schedulers.SchedulersProvider
import com.example.ttk_20.system.message.SystemMessageNotifier
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor(
    private val systemMessageNotifier: SystemMessageNotifier,
    private val schedulers: SchedulersProvider,
    private val resourceManager: ResourceManager
) {

    private val authErrorRelay = PublishRelay.create<Boolean>()
    private val logoutRelay = BehaviorRelay.create<SingleEvent<String>>()
    val logout: Observable<SingleEvent<String>> = logoutRelay.hide()

    init {
        subscribeOnAuthErrors()
    }

    fun proceed(error: Throwable, messageListener: (String) -> Unit = {}) {

        when (error) {
            is ServerError -> when (error.code) {
                401 -> authErrorRelay.accept(true)
            }
            is IOException -> messageListener.invoke("IOException")
            else -> messageListener.invoke("Unknown error")
        }
    }

    @SuppressLint("CheckResult")
    private fun subscribeOnAuthErrors() {
        authErrorRelay
            .throttleFirst(50, TimeUnit.MILLISECONDS)
            .observeOn(schedulers.ui())
            .subscribe { logout() }
    }

    private fun logout() {
        //userInteractor.logout()
        logoutRelay.acceptSingleEvent("Logout")
    }
}
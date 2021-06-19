package com.example.ttk_20.ui.qr

import com.example.ttk_20.interactor.KeylessAccessInteractor
import com.example.ttk_20.ui._base.BaseViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class QrCodeViewModel @Inject constructor(
    private val interactor: KeylessAccessInteractor
) : BaseViewModel() {

    private var disposable: Disposable? = null

    private val qrCodeRelay = BehaviorRelay.create<Boolean>()

    val qrCode: Observable<Boolean> = qrCodeRelay.hide()

    init {
       // getQrCode()
    }

    fun getQrCode() {
        disposable = interactor.getQrCode()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.e("Eeeeeeeeeeeeee ${it.byteCount}")
                qrCodeRelay.accept(true)
            }, { })
    }

}
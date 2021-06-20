package com.example.ttk_20.ui.keyless_access

import com.example.ttk_20.interactor.KeylessAccessInteractor
import com.example.ttk_20.ui._base.BaseViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class KeylessAccessViewModel @Inject constructor(
    private val interactor: KeylessAccessInteractor
) : BaseViewModel() {

    private var disposable: Disposable? = null

    private val successOpenRelay = BehaviorRelay.create<Boolean>()
    private val animationOffsetRelay = BehaviorRelay.create<Boolean>()

    val successOpen: Observable<Boolean> = successOpenRelay.hide()
    val animationOffset: Observable<Boolean> = animationOffsetRelay.hide()
        .distinctUntilChanged()

    fun openDoor() {
        disposable = interactor.openDoor()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                successOpenRelay.accept(true)
            }
            .subscribe({ }, { })
    }

    fun makeAnimationOffset() {
        disposable = interactor.checkDoorClosed()
            .delay(4, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                animationOffsetRelay.accept(it)
            }, { })
    }

    fun dispose() {
        disposable?.dispose()
    }

    override fun onCleared() {
        disposable?.dispose()
    }

}
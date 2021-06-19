package com.example.ttk_20.system

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

open class SingleEvent<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}

fun <T> BehaviorRelay<SingleEvent<T>>.acceptSingleEvent(content: T) {
    accept(SingleEvent(content))
}

fun <T> Observable<SingleEvent<T>>.subscribeToEvent(onNext: (T) -> Unit): Disposable =
    this.subscribe {
        it.getContentIfNotHandled()?.let { content -> onNext.invoke(content) }
    }


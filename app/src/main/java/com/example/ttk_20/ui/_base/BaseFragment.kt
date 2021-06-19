package com.example.ttk_20.ui._base

import android.os.Bundle
import android.os.Handler
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

private const val STATE_SCOPE_NAME = "state_scope_name"
private const val PROGRESS_TAG = "bf_progress"

open class BaseFragment(@LayoutRes containerLayoutId: Int) : Fragment(containerLayoutId) {

    private var instanceStateSaved: Boolean = false
    lateinit var fragmentInstanceKey: String
    private val disposeOnPauseDisposables = CompositeDisposable()
    private val viewHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        fragmentInstanceKey = savedInstanceState?.getString(STATE_SCOPE_NAME)
            ?: "${javaClass.simpleName}_${hashCode()}"
        onPreInit()
        super.onCreate(savedInstanceState)
    }

    protected open fun onPreInit() {
    }

    override fun onResume() {
        super.onResume()
        instanceStateSaved = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewHandler.removeCallbacksAndMessages(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        instanceStateSaved = true
        outState.putString(STATE_SCOPE_NAME, fragmentInstanceKey)
    }

    override fun onPause() {
        disposeOnPauseDisposables.clear()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (needCloseScope()) {
            //destroy this fragment with scope
            onCleared()
        }
    }

    //This is android, baby!
    private fun isRealRemoving(): Boolean =
        (isRemoving && !instanceStateSaved) //because isRemoving == true for fragment in backstack on screen rotation
                || ((parentFragment as? BaseFragment)?.isRealRemoving() ?: false)

    //It will be valid only for 'onDestroy()' method
    private fun needCloseScope(): Boolean =
        when {
            activity?.isChangingConfigurations == true -> false
            activity?.isFinishing == true -> true
            else -> isRealRemoving()
        }

    protected open fun onCleared() {}

    //fix for async views (like swipeToRefresh and RecyclerView)
    //if synchronously call actions on swipeToRefresh in sequence show and hide then swipeToRefresh will not hidden
    protected fun postViewAction(action: () -> Unit) {
        viewHandler.post(action)
    }

    protected fun Disposable.disposeOnPause() {
        disposeOnPauseDisposables.add(this)
    }

}
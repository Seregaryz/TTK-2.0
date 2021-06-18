package com.example.ttk_20.utils

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.ttk_20.R

const val defaultContainerId = R.id.container

fun FragmentManager.backTo(fragment: Class<*>?) {
    if (fragment == null) {
        popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    } else {
        popBackStack(fragment.canonicalName, 0)
    }
}

fun FragmentManager.navigateTo(
    fragment: Class<out Fragment>,
    args: Bundle? = null,
    setupFragmentTransaction: ((FragmentTransaction) -> Unit)? = null,
    containerId: Int = defaultContainerId
) {
    val fragmentTransaction = beginTransaction()
    setupFragmentTransaction?.invoke(fragmentTransaction)
    fragmentTransaction
        .replace(containerId, fragment, args)
        .addToBackStack(fragment.canonicalName)
        .setReorderingAllowed(true)
        .commit()
}

fun FragmentManager.replace(
    fragment: Class<out Fragment>,
    args: Bundle? = null,
    setupFragmentTransaction: ((FragmentTransaction) -> Unit)? = null,
    containerId: Int = defaultContainerId
) {
    if (backStackEntryCount > 0) {
        popBackStack()
        val fragmentTransaction = beginTransaction()
        setupFragmentTransaction?.invoke(fragmentTransaction)
        fragmentTransaction
            .replace(containerId, fragment, args)
            .addToBackStack(fragment.canonicalName)
            .setReorderingAllowed(true)
            .commit()
    } else {
        val fragmentTransaction = beginTransaction()
        setupFragmentTransaction?.invoke(fragmentTransaction)
        fragmentTransaction
            .replace(containerId, fragment, args)
            .setReorderingAllowed(true)
            .commit()
    }
}

fun FragmentManager.newRootScreen(
    fragment: Class<out Fragment>,
    args: Bundle? = null,
    setupFragmentTransaction: ((FragmentTransaction) -> Unit)? = null,
    containerId: Int = defaultContainerId
) {
    backTo(null)
    replace(fragment, args, setupFragmentTransaction, containerId)
}

fun FragmentManager.newRootChain(
    vararg fragments: Pair<Class<out Fragment>, Bundle?>,
    setupFragmentTransaction: ((FragmentTransaction) -> Unit)? = null,
    containerId: Int = defaultContainerId
) {
    backTo(null)
    if (fragments.isNotEmpty()) {
        replace(
            fragments[0].first,
            fragments[0].second,
            setupFragmentTransaction,
            containerId
        )
        for (i in 1 until fragments.size) {
            navigateTo(
                fragments[i].first,
                fragments[i].second,
                setupFragmentTransaction,
                containerId
            )
        }
    }
}

fun Fragment.registerOnBackPressedCallback(
    onBackPressed: OnBackPressedCallback.() -> Unit
) {
    requireActivity().onBackPressedDispatcher.addCallback(this) {
        onBackPressed()
    }
}

fun FragmentTransaction.setSlideAnimation() {
    setCustomAnimations(
        R.anim.slide_in_right,
        R.anim.slide_out_left,
        R.anim.slide_in_left,
        R.anim.slide_out_right
    )
}

fun Activity.hideSoftKeyboard(view: View) {
    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

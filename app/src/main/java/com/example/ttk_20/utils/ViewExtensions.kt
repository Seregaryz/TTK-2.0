package com.example.ttk_20.utils

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.ttk_20.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

fun View.updatePadding(
    left: Int = paddingLeft,
    top: Int = paddingTop,
    right: Int = paddingRight,
    bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

fun View.addSystemTopPadding(
    targetView: View = this,
    isConsumed: Boolean = false
) {
    doOnApplyWindowInsets { _, insets, initialPadding ->
        targetView.updatePadding(
            top = initialPadding.top + insets.systemWindowInsetTop
        )
        if (isConsumed) {
            WindowInsetsCompat.Builder(insets).setSystemWindowInsets(
                Insets.of(
                    insets.systemWindowInsetLeft,
                    0,
                    insets.systemWindowInsetRight,
                    insets.systemWindowInsetBottom
                )
            ).build()
        } else {
            insets
        }
    }

}

fun Fragment.addHardwareNavigationPadding(view: View) {
    if (activity?.window?.decorView?.isAttachedToWindow == true) {
        activity?.window?.decorView?.addSystemBottomPadding(view)
    }
}

fun Fragment.hideKeyboard() {
    activity?.apply {
        currentFocus?.apply {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}

fun Fragment.snackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    view?.let { Snackbar.make(it, message, duration).show() }
}

fun View.addSystemTopPadding(
    targetViews: List<View>,
    isConsumed: Boolean = false
) {
    doOnApplyWindowInsets { _, insets, initialPadding ->
        targetViews.forEach {
            it.updatePadding(
                top = initialPadding.top + insets.systemWindowInsetTop
            )
        }
        if (isConsumed) {
            WindowInsetsCompat.Builder(insets).setSystemWindowInsets(
                Insets.of(
                    insets.systemWindowInsetLeft,
                    0,
                    insets.systemWindowInsetRight,
                    insets.systemWindowInsetBottom
                )
            ).build()
        } else {
            insets
        }
    }
}

fun View.addSystemBottomPadding(
    targetView: View = this,
    isConsumed: Boolean = false
) {
    doOnApplyWindowInsets { _, insets, initialPadding ->
        targetView.updatePadding(
            bottom = initialPadding.bottom + insets.systemWindowInsetBottom
        )
        if (isConsumed) {
            WindowInsetsCompat.Builder(insets).setSystemWindowInsets(
                Insets.of(
                    insets.systemWindowInsetLeft,
                    insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight,
                    0
                )
            ).build()
//            insets.replaceSystemWindowInsets(
//                Rect(
//                    insets.systemWindowInsetLeft,
//                    insets.systemWindowInsetTop,
//                    insets.systemWindowInsetRight,
//                    0
//                )
//            )
        } else {
            insets
        }
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Window.configureSystemBars() {
    decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        decorView.systemUiVisibility = decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        statusBarColor = ContextCompat.getColor(context, android.R.color.transparent)
    } else {
        statusBarColor = ContextCompat.getColor(context, R.color.colorBackground)
    }


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        decorView.systemUiVisibility = decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }

    navigationBarColor = ContextCompat.getColor(context, R.color.colorBackground)
}

fun View.doOnApplyWindowInsets(block: (View, insets: WindowInsetsCompat, initialPadding: Rect) -> WindowInsetsCompat) {
    val initialPadding =
        recordInitialPaddingForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, initialPadding)
    }
    requestApplyInsetsWhenAttached()
}

private fun recordInitialPaddingForView(view: View) =
    Rect(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)

private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        ViewCompat.requestApplyInsets(this)
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                ViewCompat.requestApplyInsets(v)
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

fun TabLayout.addOnTabSelectedListener(listener: (position: Int) -> Unit) {
    addOnTabSelectedListener(
        object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                listener(tab.position)
            }
        }
    )
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun View.visible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

/**
 * Устанавливает лисенер, который возвращает информацию только о скролле по оси Y
 * @param action Лямбда, которая принимает в себя параметром скролл по оси Y
 * и вызывается при скролле
 */
inline fun NestedScrollView.onScrollYListener(crossinline action: (action: Int) -> Unit) {
    return setOnScrollChangeListener(
        NestedScrollView
            .OnScrollChangeListener { _, _, scrollY, _, _ ->
                action(scrollY)
            }
    )
}

fun AppBarLayout.disableScrolling() {
    val params = layoutParams as CoordinatorLayout.LayoutParams
    params.behavior = AppBarLayout.Behavior()
    (params.behavior as? AppBarLayout.Behavior)?.setDragCallback(object : DragCallback() {
        override fun canDrag(appBarLayout: AppBarLayout): Boolean {
            return false
        }
    })
}

package com.example.ttk_20.ui.custom_views

import android.content.Context
import android.graphics.Point
import android.text.TextUtils
import android.util.AttributeSet
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.example.ttk_20.R
import com.example.ttk_20.utils.visible

class CenteredTitleToolbar : Toolbar {

    private lateinit var _titleTextView: AppCompatTextView
    private var _screenWidth: Int = screenSize.x
    private var _centerTitle = false

    private val screenSize: Point
        get() {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val screenSize = Point()
            display.getSize(screenSize)

            return screenSize
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        if (!::_titleTextView.isInitialized) {
            initTitleTextView()
            _titleTextView.text = ""
        }
    }

    private val location = IntArray(2)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (_centerTitle) {

            with(_titleTextView) {
                getLocationOnScreen(location)
                translationX += (-location[0] + _screenWidth / 2 - width / 2)
            }
        }
    }

    override fun setTitle(title: CharSequence?) {
        if (!::_titleTextView.isInitialized) {
            initTitleTextView()
        }
        _titleTextView.text = title
        requestLayout()
    }

    override fun setTitle(titleRes: Int) {
        if (!::_titleTextView.isInitialized) {
            initTitleTextView()
        }
        _titleTextView.setText(titleRes)
        requestLayout()
    }

    private fun initTitleTextView() {
        _titleTextView = AppCompatTextView(context)
        _titleTextView.setLines(1)
        _titleTextView.ellipsize = TextUtils.TruncateAt.END
        TextViewCompat.setTextAppearance(_titleTextView, R.style.titleText)
        addView(_titleTextView)
    }

    fun setTitleCentered(centered: Boolean) {
        _centerTitle = centered
        requestLayout()
    }

    fun isTitleVisible(): Boolean = _titleTextView.isVisible

    /**
     * скрывает Титул тулбара
     */
    fun setTitleVisible(visible: Boolean) {
        _titleTextView.visible(visible)
        requestLayout()
    }

    override fun setTitleTextColor(color: Int) {
        super.setTitleTextColor(color)
        _titleTextView.setTextColor(color)
    }
}
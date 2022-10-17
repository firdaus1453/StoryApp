package com.firdaus1453.storyapp.util

import android.content.Context
import android.graphics.Canvas
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.firdaus1453.storyapp.R

class PasswordEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isFocused) {
            if (text.toString().length < 6 && text.toString().isNotEmpty()) {
                error = context.getString(R.string.error_min_6_char)
            }
        }
    }

    private fun init() {
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
    }
}

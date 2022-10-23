package com.firdaus1453.storyapp.util

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Patterns
import com.firdaus1453.storyapp.R
import com.google.android.material.textfield.TextInputLayout

class EmailEditText : TextInputLayout {

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
        if (editText?.isFocused == true) {
            error = if (editText?.text?.isNotEmpty() == true && !Patterns.EMAIL_ADDRESS.matcher(editText?.text.toString()).matches()) {
                context.getString(R.string.error_valid_email)
            } else {
                null
            }
        }
    }

    private fun init() {
        // not use
    }
}

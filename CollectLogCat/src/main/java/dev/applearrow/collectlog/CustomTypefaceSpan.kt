package dev.applearrow.collectlog

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

/**
 * Custom version of TypefaceSpan - provided for backward compatibility for earlier versions of
 * TypefaceSpan that didn't support specifying the Typeface in the constructor
 */
class CustomTypefaceSpan(val type: Typeface? = null) : TypefaceSpan(null) {

    override fun updateDrawState(drawState: TextPaint) {
        apply(drawState)
    }

    override fun updateMeasureState(paint: TextPaint) {
        apply(paint)
    }

    private fun apply(paint: Paint) {
        type?.let { paint.typeface = it }
    }
}
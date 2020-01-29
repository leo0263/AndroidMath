package com.agog.mathdisplay

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.agog.mathdisplay.parse.MTLineStyle
import com.agog.mathdisplay.parse.MTMathListBuilder
import com.agog.mathdisplay.render.MTFont
import com.agog.mathdisplay.render.MTTypesetter
import com.agog.mathdisplay.render.trim

object MTMathGenerator {

    private const val defaultWidth = 640
    private const val defaultHeight = 480
    private const val defaultMargin = 20
    private const val defaultFontSize = 20f
    var defaultFont: MTFont? = null

    @JvmStatic
    fun createBitmap(latexString: String): Bitmap? {
        if (defaultFont == null) initializeDefaultFont()

        return createBitmap(latexString, defaultFont, defaultWidth, defaultHeight, defaultMargin)
    }

    @JvmStatic
    fun createBitmap(latexString: String, fontColor: Int): Bitmap? {
        defaultFont?.color = fontColor
        return createBitmap(latexString, defaultFont, defaultWidth, defaultHeight, defaultMargin)
    }

    @JvmStatic
    fun createBitmap(latexString: String, font: MTFont): Bitmap? {
        return createBitmap(latexString, font, defaultWidth, defaultHeight, defaultMargin)
    }

    @JvmStatic
    fun createBitmap(
            latexString: String,
            fontParam: MTFont? = defaultFont,
            bitmapWidth: Int = defaultWidth,
            bitmapHeight: Int = defaultHeight,
            bitmapMargin: Int = defaultMargin
    ): Bitmap? {
        var font = fontParam
        if (defaultFont == null) {
            initializeDefaultFont()
            if (font == null) font = defaultFont
        }

        val mathList = MTMathListBuilder.buildFromString(latexString)

        if (mathList != null && font != null) {
            val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.translate(0.0f, bitmapHeight.toFloat())
            canvas.scale(1.0f, -1.0f)
            canvas.translate(100.0f, 100.0f) // We shift this to catch any coordinate system errors

            val display = MTTypesetter.createLineForMathList(mathList, font, MTLineStyle.KMTLineStyleText)
            //display.textColor = font.color
            display.draw(canvas)

            return bitmap.trim(margin = bitmapMargin)
        }

        return null
    }

    private fun initializeDefaultFont() {
        if (defaultFont == null) {
            defaultFont = MTFontManager.latinModernFontWithSize(defaultFontSize)
        }
    }
}

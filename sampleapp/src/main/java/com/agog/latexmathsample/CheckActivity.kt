package com.agog.latexmathsample

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.agog.mathdisplay.MTMathGenerator
import kotlinx.android.synthetic.main.activity_check.*

class CheckActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, CheckActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)

        checkLatexCompatibility()
    }

    private fun bitmapToDrawable(bitmap: Bitmap): Drawable {
        val drawable = BitmapDrawable(resources, bitmap)
        drawable.setBounds(0, 0, bitmap.width, bitmap.height)

        return drawable
    }

    private fun checkLatexCompatibility() {
        //MTMathGenerator.defaultFont = MTFontManager.latinModernFontWithSize(20f)
        //MTFontManager.setContext(this)

        val layoutPad = 16
        val layoutParams = LinearLayout.LayoutParams(0, 0)
        layoutParams.setMargins(layoutPad, layoutPad, layoutPad, layoutPad)

        val inputStream = resources.openRawResource(R.raw.checklatex)
        val lineList = mutableListOf<String>()

        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) } }

        lineList.forEach {
            if (it.isNotBlank()) {
                if (it[0] == '#') {
                    val tv = TextView(this)
                    tv.text = it.trim()
                    tv.setTextColor(Color.DKGRAY)
                    println("textSize ${tv.textSize}")
                    checkLatexLayout.addView(tv)
                } else {
                    val latexBitmap: Bitmap? = MTMathGenerator.createBitmap(it)

                    if (latexBitmap != null) {
                        val latexDrawable = bitmapToDrawable(latexBitmap)
                        val latexImageView = ImageView(this)
                        latexImageView.setImageDrawable(latexDrawable)
                        checkLatexLayout.addView(latexImageView)
                    } else {
                        val errorRenderTV = TextView(this)
                        errorRenderTV.text = "error rendering: $it"
                        errorRenderTV.setTextColor(Color.RED)
                        checkLatexLayout.addView(errorRenderTV)
                    }
                }
            }
        }
    }
}

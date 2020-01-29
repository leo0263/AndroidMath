package com.agog.latexmathsample

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
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
        val layoutPad = 16
        val layoutParams = LinearLayout.LayoutParams(0, 0)
        layoutParams.setMargins(layoutPad, layoutPad, layoutPad, layoutPad)

        val inputStream = resources.openRawResource(R.raw.checklatex)
        val lineList = mutableListOf<String>()

        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) } }

        // create test(s) from checklatex.txt file
        lineList.forEach {
            if (it.isNotBlank()) {
                if (it[0] == '#') {
                    val tv = TextView(this)
                    tv.text = it.trim()
                    tv.setTextColor(Color.DKGRAY)
                    println("textSize ${tv.textSize}")
                    checkLatexLayout.addView(tv)
                } else {
                    createLatexImageView(it)

                }
            }
        }

        runMultilineTest()
    }

    private fun createLatexImageView(strLatex: String) {
        val latexBitmap: Bitmap? = MTMathGenerator.createBitmap(strLatex, isDebugOn = true)

        if (latexBitmap != null) {
            val latexImageView = createImageView(latexBitmap)
            checkLatexLayout.addView(latexImageView)
        } else {
            val errorRenderTV = TextView(this)
            errorRenderTV.text = "error rendering: $strLatex"
            errorRenderTV.setTextColor(Color.RED)
            checkLatexLayout.addView(errorRenderTV)
        }
    }

    private fun runMultilineTest() {
        val multilineStr = "\\left|\\begin{matrix}\n" +
                "6 & 1 & 3\\\\ \n" +
                "2 & 3 & 2\\\\ \n" +
                "1 & 1 & 0\\end{matrix}\\right|\n" +
                "=\n" +
                "\\left|\\begin{matrix}\n" +
                "\\require{cancel}\\cancel{6} & \\require{cancel}\\cancel{1} & \\require{cancel}\\cancel{3}\\\\ \n" +
                "\\require{cancel}\\cancel{2} & \\require{cancel}\\cancel{3} & \\require{cancel}\\cancel{2}\\\\ \n" +
                "\\require{cancel}\\cancel{1} & \\require{cancel}\\cancel{1} & \\require{cancel}\\cancel{0}\\end{matrix}\\right|\n" +
                "\\begin{matrix}\\require{cancel}\\cancel{6} & \\require{cancel}\\cancel{1}\\\\ \n" +
                "\\require{cancel}\\cancel{2} & \\require{cancel}\\cancel{3} \\\\ \n" +
                "\\require{cancel}\\cancel{1} & \\require{cancel}\\cancel{1}\\end{matrix} "

        createLatexImageView(multilineStr)
    }

    // from this: https://stackoverflow.com/questions/8232608/fit-image-into-imageview-keep-aspect-ratio-and-then-resize-imageview-to-image-d
    // change it to have bitmap as param and return new ImageView instead
    @Throws(NoSuchElementException::class)
    private fun createImageView(bitmap: Bitmap): ImageView { // Get bitmap from the the ImageView.
        val imageView = ImageView(this)
        imageView.setImageBitmap(bitmap)

        val drawing = imageView.drawable
        val viewBitmap = (drawing as BitmapDrawable).bitmap

        // Get current dimensions AND the desired bounding box
        var width = viewBitmap.width
        var height = viewBitmap.height
        val bounding = dpToPx(120)
        val xScale = bounding.toFloat() / width
        val yScale = bounding.toFloat() / height
        val scale = if (xScale <= yScale) xScale else yScale

        // Create a matrix for the scaling and add the scaling data
        val matrix = Matrix()
        matrix.postScale(scale, scale)

        // Create a new bitmap and convert it to a format understood by the ImageView
        val scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        width = scaledBitmap.width // re-use
        height = scaledBitmap.height // re-use
        val result = BitmapDrawable(scaledBitmap)

        // Apply the scaled bitmap
        imageView.setImageDrawable(result)

        // Now change ImageView's dimensions to match the scaled image
        val params = LinearLayout.LayoutParams(0, 0)
        params.width = width
        params.height = height
        imageView.layoutParams = params

        return imageView
    }

    private fun dpToPx(dp: Int): Int {
        val density = applicationContext.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
}

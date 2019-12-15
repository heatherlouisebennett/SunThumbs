/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.aimicor.sunthumbs

import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.gpu.ContrastFilterTransformation
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation
import kotlinx.android.synthetic.main.activity_edit_photo.*

class EditPhotoActivity : AppCompatActivity() {
    private var blurValue: Int = 1
    private var vignetteValue: Float = 1f
    private var contrastValue: Float = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initViews()
    }

    private fun initViews() {
        val photoUrl = intent.getStringExtra(INTENT_PHOTO_URL)

        rgFilters.setOnCheckedChangeListener { _, checkedId ->
            run {
                updateProgress(checkedId)
            }
        }
        updateProgress(rgFilters.checkedRadioButtonId)

        sbValue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                applyFilters(progress, photoUrl)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
        applyFilters(0, photoUrl)
    }

    private fun updateProgress(checkedId: Int) {
        when (checkedId) {
            R.id.rbBlur -> sbValue.progress = ((blurValue - 1).toFloat() / 25 * 100).toInt()
            R.id.rbVignette -> sbValue.progress = (vignetteValue / 2 * 100).toInt()
            R.id.rbContrast -> sbValue.progress = (contrastValue / 4 * 100).toInt()
        }
    }

    private fun applyFilters(progress: Int, photoUrl: String?) {
        when {
            rbBlur.isChecked -> blurValue = ((progress.toFloat() / 100) * 25).toInt() + 1
            rbVignette.isChecked -> vignetteValue = (progress.toFloat() / 100) * 2
            rbContrast.isChecked -> contrastValue = (progress.toFloat() / 100) * 4
        }

        rbBlur.text = String.format("Blur %s", blurValue)
        rbVignette.text = String.format("Vignette %s", vignetteValue)
        rbContrast.text = String.format("Contrast %s", contrastValue)

        val listener = object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                progressBar.visibility = View.GONE
                return false
            }
        }

        Handler().post {
            val width = ivPhoto.width
            val url = if (photoUrl != null) "$photoUrl?w=${width}" else null //1
            Glide.with(this)
                    .load(photoUrl)
                    .addListener(listener)
                    .override(width, width) //1
                    .transform( //2
                            CenterCrop(),
                            BlurTransformation(blurValue), //3
                            ContrastFilterTransformation(contrastValue), //4
                            VignetteFilterTransformation(
                                    PointF(0.5f, 0.5f),
                                    floatArrayOf(0f, 0f, 0f),
                                    0f,
                                    vignetteValue)) //5
                    .diskCacheStrategy(DiskCacheStrategy.DATA) //6
                    .placeholder(ivPhoto.drawable) //7
                    .into(ivPhoto)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {

        private const val INTENT_PHOTO_URL = "INTENT_PHOTO_URL"

        fun newIntent(context: Context, photoUrl: String?): Intent {
            val intent = Intent(context, EditPhotoActivity::class.java)
            intent.putExtra(INTENT_PHOTO_URL, photoUrl)
            return intent
        }
    }
}

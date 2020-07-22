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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aimicor.sunthumbs.provider.PhotoDetail
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_edit_photo.*


class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo) //stitches the xml code to the activity

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val listener = object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                progressBar.visibility = View.GONE
                return false
            }
        }

      //  val photoDetail = intent.getStringExtra(INTENT_PHOTO_URL) //take object and converts into string
        val photoDetail = intent.getSerializableExtra(INTENT_PHOTO_URL) as PhotoDetail
        Handler().post {
            val width = ivPhoto.width
            Glide.with(this)
                    .load(photoDetail.url)
                    .addListener(listener)
                    .override(width, width) //1
                    .transform(CenterCrop())
                    .diskCacheStrategy(DiskCacheStrategy.DATA) //6
                    .placeholder(ivPhoto.drawable) //7
                    .into(ivPhoto)
        }
        photo_description.text = photoDetail.title
        photo_description.setOnClickListener {watchYoutubeVideo(photoDetail.youtube)  }



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

    private fun watchYoutubeVideo(id: String) =
        try {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("vnd.youtube:$id")))
        } catch (ex: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=$id")))
        }


    companion object {

        private const val INTENT_PHOTO_URL = "INTENT_PHOTO_URL"

        fun newIntent(context: Context, photoDetail: PhotoDetail?): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(INTENT_PHOTO_URL, photoDetail)
            return intent
        }
    }
}

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

package com.aimicor.sunthumb

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aimicor.sunthumb.photo.PhotoAdapter
import com.aimicor.sunthumb.provider.Api
import com.aimicor.sunthumb.provider.PhotoDetail
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var photoAdapter: PhotoAdapter? = null //? nullable type, var is variable. ie *can* overwrite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) //xml layout meta data - inflating xml graphics

        swipeRefresh.setOnRefreshListener(::getPhotoDetails)
        getPhotoDetails()
    }

    private fun getPhotoDetails() {
        CompositeDisposable().apply {
            add(Api.create().getPhotoDetails() //RX callback foreground and background specification
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { photoDetails ->
                    loadPhotoAdapter(photoDetails)
                    dispose()
                })
        }
    }

    private fun loadPhotoAdapter(photoDetails: List<PhotoDetail>) {
        photoAdapter = PhotoAdapter(photoDetails) //photo provider is a constructor with a list of url strings (takes in list of photos)
        photoAdapter?.setItemClickListener {
            //if photoAdapter is not null (?) then setItemClickListener (callback)
            startActivity(DetailActivity.newIntent(this@MainActivity, it)) // outputs photos on the screen or view
        }
        rvPhotos.layoutManager = GridLayoutManager(this, resources.getInteger(R.integer.span_count))
        rvPhotos.adapter = photoAdapter
        swipeRefresh.isRefreshing=false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when {
            item?.itemId == R.id.clear_cache -> { //super delegates to the parent when you select an item i.e cache from burger menu
                clearCache()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearCache() {
        Thread(Runnable {
            Glide.get(this).clearDiskCache() //1
        }).start()
        Glide.get(this).clearMemory() //2
    }
}

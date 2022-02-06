package com.aimicor.sunthumb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aimicor.sunthumb.provider.Api
import com.aimicor.sunthumb.provider.PhotoDetail
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {

    private val photoDetails: MutableLiveData<List<PhotoDetail>> by lazy {
        MutableLiveData<List<PhotoDetail>>().also {
            loadPhotoDetails()
        }
    }

    fun getPhotoDetails(): LiveData<List<PhotoDetail>> {
        return photoDetails
    }

   fun loadPhotoDetails() {
        CompositeDisposable().apply {
            add(
                Api.create().getPhotoDetails() //RX callback foreground and background specification
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { details ->
                        photoDetails.value = details
                        dispose()
                    })
        }
    }
}
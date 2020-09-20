package com.aimicor.sunthumbs.provider

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import java.io.Serializable


interface Api {

    @GET("db.json")
    @Headers("Cache-control: no-cache")
    fun getPhotoDetails(): Single<List<PhotoDetail>>

    companion object {
        fun create(): Api = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://heatherlouisebennett.github.io/SunThumbs/")
            .build().create(Api::class.java)
    }
}

data class PhotoDetail(
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String,
    @SerializedName ("youtube")
    val youtube: String
): Serializable

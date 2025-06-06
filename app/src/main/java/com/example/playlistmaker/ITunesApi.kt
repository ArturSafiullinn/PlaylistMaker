package com.example.playlistmaker
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("search?entity=song")
    fun search(@Query("term") text: String): retrofit2.Call<SearchResponse>
}
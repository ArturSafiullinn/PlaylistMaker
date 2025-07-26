package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.SearchResponse
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class NetworkClientImpl : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesApi = retrofit.create(ITunesApi::class.java)

    override fun searchTracks(query: String, callback: (Result<SearchResponse>) -> Unit) {
        iTunesApi.search(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    callback(Result.success(body))
                } else {
                    callback(Result.failure(Throwable("Error: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}
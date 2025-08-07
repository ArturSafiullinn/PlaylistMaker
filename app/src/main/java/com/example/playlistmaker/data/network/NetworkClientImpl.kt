package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.SearchResponse
import retrofit2.*

class NetworkClientImpl(
    private val iTunesApi: ITunesApi
) : NetworkClient {

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

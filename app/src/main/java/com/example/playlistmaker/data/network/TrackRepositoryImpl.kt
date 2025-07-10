package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.SearchResponse
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackRepositoryImpl(
    private val api: ITunesApi
) : TrackRepository {

    override fun searchTracks(query: String, callback: (Result<List<Track>>) -> Unit) {
        api.search(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val results = response.body()?.results ?: emptyList()
                    val tracks = results.map { it.toDomain() }
                    callback(Result.success(tracks))
                } else {
                    callback(Result.failure(Exception("Search failed: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
    
    private fun TrackDto.toDomain(): Track {
        return Track(
            trackId = trackId ?: 0,
            trackName = trackName.orEmpty(),
            artistName = artistName.orEmpty(),
            trackTime = trackTimeMillis?.let { formatTime(it) } ?: "",
            artworkUrl = artworkUrl100.orEmpty(),
            collectionName = collectionName.orEmpty(),
            releaseDate = releaseDate?.take(4).orEmpty(),
            genre = primaryGenreName.orEmpty(),
            country = country.orEmpty(),
            previewUrl = previewUrl.orEmpty()
        )
    }

    private fun formatTime(ms: Long): String {
        val minutes = ms / 60000
        val seconds = (ms % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }
}

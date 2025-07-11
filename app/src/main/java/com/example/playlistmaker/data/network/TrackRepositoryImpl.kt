package com.example.playlistmaker.data.network

import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTracks(query: String, callback: (Result<List<Track>>) -> Unit) {
        networkClient.searchTracks(query) { result ->
            result
                .mapCatching { response ->
                    response.results.map { dto ->
                        Track(
                            trackId = dto.trackId ?: 0,
                            trackName = dto.trackName.orEmpty(),
                            artistName = dto.artistName.orEmpty(),
                            trackTime = dto.trackTimeMillis?.let { SimpleDateFormat("mm:ss", Locale.getDefault()).format(it) } ?: "",
                            artworkUrl = dto.artworkUrl100.orEmpty(),
                            collectionName = dto.collectionName.orEmpty(),
                            releaseDate = dto.releaseDate?.take(4).orEmpty(),
                            genre = dto.primaryGenreName.orEmpty(),
                            country = dto.country.orEmpty(),
                            previewUrl = dto.previewUrl.orEmpty()
                        )
                    }
                }
                .let(callback)
        }
    }
}
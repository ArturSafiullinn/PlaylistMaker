import android.content.res.Resources
import com.example.playlistmaker.R
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.itunes.TracksSearchRequest
import com.example.playlistmaker.data.network.itunes.TracksSearchResponse
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val resources: Resources
) : TrackRepository {

    override fun searchTracks(query: String): Flow<Resource<List<Track>>> = flow {
        emit(Resource.Loading)

        val response = networkClient.doRequest(TracksSearchRequest(query))
        when (response.resultCode) {
            -1 -> emit(Resource.Error(resources.getString(R.string.error_no_internet)))
            200 -> {
                val data = (response as TracksSearchResponse).results.map { dto ->
                    Track(
                        trackId        = dto.trackId ?: 0,
                        trackName      = dto.trackName.orEmpty(),
                        artistName     = dto.artistName.orEmpty(),
                        trackTime      = formatDuration(dto.trackTimeMillis),
                        artworkUrl     = dto.artworkUrl100.orEmpty(),
                        collectionName = dto.collectionName.orEmpty(),
                        releaseDate    = dto.releaseDate?.take(4).orEmpty(),
                        genre          = dto.primaryGenreName.orEmpty(),
                        country        = dto.country.orEmpty(),
                        previewUrl     = dto.previewUrl.orEmpty()
                    )
                }
                emit(Resource.Success(data))
            }
            400 -> emit(Resource.Error(resources.getString(R.string.error_bad_request)))
            else -> emit(Resource.Error(resources.getString(R.string.error_server, response.resultCode)))
        }
    }

    private fun formatDuration(ms: Long?): String {
        if (ms == null) return resources.getString(R.string.default_track_time)
        val totalSec = ms / 1000
        val minutes = totalSec / 60
        val seconds = totalSec % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
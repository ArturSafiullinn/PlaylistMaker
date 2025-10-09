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
                val data = (response as TracksSearchResponse).results.mapNotNull { dto ->
                    val id = dto.trackId ?: return@mapNotNull null
                    Track(
                        trackId        = id,
                        trackName      = dto.trackName.orEmpty(),
                        artistName     = dto.artistName.orEmpty(),
                        trackTime      = dto.trackTime ?: 0L,
                        artworkUrl     = dto.artworkUrl100.orEmpty(),
                        collectionName = dto.collectionName,
                        releaseDate    = dto.releaseDate?.take(4),
                        genre          = dto.primaryGenreName,
                        country        = dto.country,
                        previewUrl     = dto.previewUrl
                    )
                }
                emit(Resource.Success(data))
            }
            400 -> emit(Resource.Error(resources.getString(R.string.error_bad_request)))
            else -> emit(Resource.Error(resources.getString(R.string.error_server, response.resultCode)))
        }
    }
}
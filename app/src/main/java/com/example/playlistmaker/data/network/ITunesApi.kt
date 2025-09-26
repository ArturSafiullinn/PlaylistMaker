import com.example.playlistmaker.data.dto.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("search?entity=song")
    suspend fun search(@Query("term") text: String): Response<SearchResponse>
}
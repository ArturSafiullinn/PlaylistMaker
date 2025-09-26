package com.example.playlistmaker.data.network

import ITunesApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.data.network.core.Response
import com.example.playlistmaker.data.network.itunes.TracksSearchRequest
import com.example.playlistmaker.data.network.itunes.TracksSearchResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkClientImpl(
    private val iTunesApi: ITunesApi,
    private val context: Context
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }

        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = 400 }
        }

        return withContext(Dispatchers.IO) {
            try {
                val resp = iTunesApi.search(dto.term)

                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body != null) {
                        return@withContext TracksSearchResponse().apply {
                            resultCode = 200
                            results = body.results
                        }
                    } else {
                        return@withContext Response().apply { resultCode = 500 }
                    }
                } else {
                    return@withContext Response().apply { resultCode = resp.code() }
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (t: Throwable) {
                return@withContext Response().apply { resultCode = 500 }
            }
        }
    }

    private fun isConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}
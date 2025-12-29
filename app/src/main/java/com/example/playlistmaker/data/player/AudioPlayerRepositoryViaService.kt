package com.example.playlistmaker.data.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.playlistmaker.domain.api.AudioPlayerRepository
import com.example.playlistmaker.service.PlayerService

class AudioPlayerRepositoryViaService(
    private val appContext: Context
) : AudioPlayerRepository {

    private data class PendingTrackInfo(
        val title: String,
        val artist: String
    )

    private var pendingTrackInfo: PendingTrackInfo? = null
    private var service: PlayerService? = null
    private var bound = false
    private var pendingPrepare: (() -> Unit)? = null
    private var pendingPlay: Boolean = false
    private var pendingPause: Boolean = false
    private var pendingStop: Boolean = false
    private var pendingRelease: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val b = binder as PlayerService.PlayerBinder
            service = b.getService()
            bound = true

            pendingTrackInfo?.let { (a, t) ->
                service?.bindTrackInfo(a, t)
                pendingTrackInfo = null
            }

            pendingPrepare?.invoke()
            pendingPrepare = null

            if (pendingPlay) {
                service?.play()
                pendingPlay = false
            }
            if (pendingPause) {
                service?.pause()
                pendingPause = false
            }
            if (pendingStop) {
                service?.stop()
                pendingStop = false
            }
            if (pendingRelease) {
                service?.release()
                pendingRelease = false
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            service = null
        }
    }

    fun bind() {
        val intent = Intent(appContext, PlayerService::class.java)

        appContext.startService(intent)

        appContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        if (bound) appContext.unbindService(connection)
        bound = false
        service = null
        pendingPrepare = null
        pendingTrackInfo = null
        pendingPlay = false
        pendingPause = false
        pendingStop = false
        pendingRelease = false
    }

    fun bindTrackInfo(artist: String, title: String) {
        val s = service
        if (s == null) {
            pendingTrackInfo = PendingTrackInfo(artist, title)
            return
        }
        s.bindTrackInfo(artist, title)
    }

    override fun prepare(url: String, onReady: () -> Unit, onCompletion: () -> Unit) {
        val s = service
        if (s == null) {
            pendingPrepare = { service?.prepare(url, onReady, onCompletion) }
            return
        }
        s.prepare(url, onReady, onCompletion)
    }

    override fun play() {
        val s = service
        if (s == null) {
            pendingPlay = true
            return
        }
        s.play()
    }

    override fun pause() {
        val s = service
        if (s == null) {
            pendingPause = true
            return
        }
        s.pause()
    }

    override fun stop() {
        val s = service
        if (s == null) {
            pendingStop = true
            return
        }
        s.stop()
    }

    override fun release() {
        val s = service
        if (s == null) {
            pendingRelease = true
            return
        }
        s.release()
    }

    override fun isPlaying(): Boolean = service?.isPlaying() == true
    override fun getCurrentPosition(): Int = service?.getCurrentPosition() ?: 0
}
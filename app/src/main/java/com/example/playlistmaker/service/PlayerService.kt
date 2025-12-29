package com.example.playlistmaker.service

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.playlistmaker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerService : Service() {

    companion object {
        const val NOTIF_CHANNEL_ID = "playlist_maker_player"
        const val NOTIF_ID = 1001

        const val EXTRA_ARTIST = "extra_artist"
        const val EXTRA_TITLE = "extra_title"
    }

    sealed interface State {
        object Default : State
        object Prepared : State
        data class Playing(val positionMs: Int) : State
        data class Paused(val positionMs: Int) : State
    }

    inner class PlayerBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

    private val binder = PlayerBinder()

    private var mediaPlayer: MediaPlayer? = null
    private var url: String? = null

    private var artist: String = ""
    private var title: String = ""

    private val _state = MutableStateFlow<State>(State.Default)
    val state: StateFlow<State> = _state

    private var timerJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var appInForeground: Boolean = true

    private val appLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            appInForeground = true
            hideNotification()
        }

        override fun onStop(owner: LifecycleOwner) {
            appInForeground = false
            if (isPlaying()) showNotification()
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
    }

    override fun onDestroy() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(appLifecycleObserver)
        stopTimer()
        releaseInternal()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder = binder


    fun bindTrackInfo(artist: String, title: String) {
        this.artist = artist
        this.title = title
        if (!appInForeground && isPlaying()) showNotification()
    }

    fun prepare(url: String, onReady: () -> Unit, onCompletion: () -> Unit) {
        this.url = url
        releaseInternal()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                _state.value = State.Prepared
                onReady()
            }
            setOnCompletionListener {
                stopTimer()
                _state.value = State.Prepared
                hideNotification()
                onCompletion()
            }
            setOnErrorListener { _, _, _ ->
                releaseInternal()
                hideNotification()
                true
            }
            prepareAsync()
        }
    }

    fun play() {
        val mp = mediaPlayer ?: return
        mp.start()
        _state.value = State.Playing(mp.currentPosition)
        startTimer()
        if (!appInForeground) showNotification()
    }

    fun pause() {
        val mp = mediaPlayer ?: return
        if (mp.isPlaying) mp.pause()
        stopTimer()
        _state.value = State.Paused(mp.currentPosition)
        hideNotification()
    }

    fun stop() {
        val mp = mediaPlayer ?: return
        runCatching { mp.stop() }
        stopTimer()
        _state.value = State.Prepared
        hideNotification()
    }

    fun release() {
        stopTimer()
        releaseInternal()
        hideNotification()
        stopSelf()
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0


    private fun startTimer() {
        stopTimer()
        timerJob = scope.launch {
            while (isActive && isPlaying()) {
                _state.value = State.Playing(getCurrentPosition())
                delay(300L)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun releaseInternal() {
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.setOnErrorListener(null)
        runCatching { mediaPlayer?.release() }
        mediaPlayer = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIF_CHANNEL_ID,
                "Playlist Maker",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val text = "$artist - $title"
        return NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Playlist Maker")
            .setContentText(text)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun showNotification() {
        startForeground(NOTIF_ID, buildNotification())
    }

    private fun hideNotification() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancel(NOTIF_ID)
    }
}
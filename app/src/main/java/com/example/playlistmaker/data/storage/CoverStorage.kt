package com.example.playlistmaker.data.storage

import android.content.Context
import android.net.Uri
import com.example.playlistmaker.presentation.ui.media.createPlaylist.CoverStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class CoverStorageImpl(private val appContext: Context) : CoverStorage {
    override suspend fun saveCover(src: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val dir = File(appContext.filesDir, "covers").apply { if (!exists()) mkdirs() }
            val outFile = File(dir, "playlist_${System.currentTimeMillis()}.jpg")
            appContext.contentResolver.openInputStream(src).use { input: InputStream? ->
                if (input == null) return@withContext null
                FileOutputStream(outFile).use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var n = input.read(buffer)
                    while (n >= 0) {
                        output.write(buffer, 0, n)
                        n = input.read(buffer)
                    }
                    output.flush()
                }
            }
            outFile.absolutePath // строка для БД
        } catch (_: Exception) { null }
    }
}

package com.example.playlistmaker.data.util

object IdsCsv {
    fun toCsv(ids: List<Long>): String? =
        if (ids.isEmpty()) null else ids.joinToString(",")

    fun fromCsv(csv: String?): List<Long> =
        csv?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
}
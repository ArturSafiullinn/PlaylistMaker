package com.example.playlistmaker

fun String.smartTrim(): String {
    return this
        .replace(Regex("^\\s+|\\s+$"), "") // убирает пробелы в начале и в конце
        .replace(Regex("[\\u00A0\\u2007\\u202F]"), " ") // заменяет неразрывные пробелы на обычные
}

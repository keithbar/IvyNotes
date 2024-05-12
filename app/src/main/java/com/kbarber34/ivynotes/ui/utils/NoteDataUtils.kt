package com.kbarber34.ivynotes.ui.utils

import com.kbarber34.ivynotes.data.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Returns a formatted string containing date and time when the given note was last modified
fun getNoteTimestampAsString(note: Note): String{
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return dateFormat.format(Date(note.modifiedTimestampInMillis))
}
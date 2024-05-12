package com.kbarber34.ivynotes.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "modified_timestamp")
    var modifiedTimestampInMillis: Long = 0,

    var contents: String = "",

    var favorited: Boolean = false
)
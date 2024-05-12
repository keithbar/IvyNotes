package com.kbarber34.ivynotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM Notes ORDER BY modified_timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM Notes WHERE favorited ORDER BY modified_timestamp DESC")
    fun getFavoriteNotes(): Flow<List<Note>>

    @Query("SELECT * FROM Notes WHERE id = :noteId")
    fun getNoteById(noteId: Int): Flow<Note>

    @Query("UPDATE Notes SET favorited = :favorited WHERE id = :id")
    suspend fun toggleFavorite(id: Int, favorited: Boolean)

    // saveNote() return note ID, useful for times when a note's
    // ID changes, such as a new note (ID 0) being added to DB
    @Upsert
    suspend fun saveNote(note: Note): Long

    @Delete
    suspend fun deleteNote(note: Note)
}
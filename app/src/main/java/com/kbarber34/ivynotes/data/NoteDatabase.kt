package com.kbarber34.ivynotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object{
        @Volatile
        private var Instance: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(
                    context, NoteDatabase::class.java, "note_database"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
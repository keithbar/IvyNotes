package com.kbarber34.ivynotes

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kbarber34.ivynotes.data.NoteDatabase
import com.kbarber34.ivynotes.data.UserPreferencesRepository

// Set up datastore, used for storing user preferences
private const val PREFERENCES = "preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES
)

class IvyNotesApplication: Application() {
    // Allow access to preferences across entire application
    lateinit var userPreferencesRepository: UserPreferencesRepository

    // Create instance of note database
    val noteDatabase: NoteDatabase by lazy{
        NoteDatabase.getDatabase(this)
    }

    override fun onCreate(){
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}
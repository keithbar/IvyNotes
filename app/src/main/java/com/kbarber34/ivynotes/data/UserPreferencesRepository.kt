package com.kbarber34.ivynotes.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.kbarber34.ivynotes.NEW_NOTE_ID
import com.kbarber34.ivynotes.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

// Class for interacting with the data store

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private companion object{
        val TEXT_SIZE = intPreferencesKey("text_size")
        val COLOR_SCHEME = intPreferencesKey("color_scheme")
        val SHOW_FAVORITES = booleanPreferencesKey("show_favorites")
        val SELECTED_FONT = intPreferencesKey("selected_font")
        val CURRENT_NOTE_ID = intPreferencesKey("current_note_id")

        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveTextSizePreference(textSize: Int){
        dataStore.edit{ preferences ->
            preferences[TEXT_SIZE] = textSize
        }
    }

    suspend fun saveColorSchemePreference(colorScheme: Int){
        dataStore.edit{ preferences ->
            preferences[COLOR_SCHEME] = colorScheme
        }
    }

    suspend fun saveSelectedFontPreference(selectedFont: Int){
        dataStore.edit{ preferences ->
            preferences[SELECTED_FONT] = selectedFont
        }
    }

    suspend fun saveShowFavoritesPreference(showFavorites: Boolean){
        dataStore.edit{ preferences ->
            preferences[SHOW_FAVORITES] = showFavorites
        }
    }

    suspend fun setCurrentNoteId(noteId: Int){
        dataStore.edit{ preferences ->
            preferences[CURRENT_NOTE_ID] = noteId
        }
    }

    // Exposed value for Text Size preference. Default is MEDIUM
    val textSize: Flow<Int> = dataStore.data
        .catch{
            if(it is IOException){
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            }else{
                throw it
            }
        }
        .map{ preferences ->
            preferences[TEXT_SIZE] ?: R.string.pref_text_size_medium
        }

    // Exposed value for Color Scheme preference
    val colorScheme: Flow<Int> = dataStore.data
        .catch{
            if(it is IOException){
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            }else{
                throw it
            }
        }
        .map{ preferences ->
            preferences[COLOR_SCHEME] ?: R.string.pref_color_scheme_default
        }

    // Exposed value for Show Favorites preference
    val showFavorites: Flow<Boolean> = dataStore.data
        .catch{
            if(it is IOException){
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            }else{
                throw it
            }
        }
        .map{ preferences ->
            preferences[SHOW_FAVORITES] ?: false
        }

    // Exposed value for Selected Font preference
    val selectedFont: Flow<Int> = dataStore.data
        .catch{
            if(it is IOException){
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            }else{
                throw it
            }
        }
        .map{ preferences ->
            preferences[SELECTED_FONT] ?: 0
        }

    val currentNoteId: Flow<Int> = dataStore.data
        .catch{
            if(it is IOException){
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            }else{
                throw it
            }
        }
        .map{ preferences ->
            preferences[CURRENT_NOTE_ID] ?: NEW_NOTE_ID
        }
}
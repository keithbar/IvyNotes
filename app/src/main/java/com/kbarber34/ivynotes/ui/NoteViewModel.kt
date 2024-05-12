package com.kbarber34.ivynotes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kbarber34.ivynotes.IvyNotesApplication
import com.kbarber34.ivynotes.NEW_NOTE_ID
import com.kbarber34.ivynotes.R
import com.kbarber34.ivynotes.data.Note
import com.kbarber34.ivynotes.data.NoteDao
import com.kbarber34.ivynotes.data.PreferencesColorScheme
import com.kbarber34.ivynotes.data.PreferencesFontFamily
import com.kbarber34.ivynotes.data.PreferencesTextSize
import com.kbarber34.ivynotes.data.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val noteDao: NoteDao
): ViewModel() {
    // State for keeping track of elements related to UI
    var notesUiState: Flow<NotesUiState> = combine(
        userPreferencesRepository.textSize,
        userPreferencesRepository.colorScheme,
        userPreferencesRepository.showFavorites,
        userPreferencesRepository.selectedFont,
        userPreferencesRepository.currentNoteId,
    ){ textSize, colorScheme, showFavorites, selectedFont, currentNoteId ->
        NotesUiState(
            PreferencesTextSize.validate(textSize),
            PreferencesColorScheme.validate(colorScheme),
            showFavorites,
            PreferencesFontFamily.validate(selectedFont),
            currentNoteId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NotesUiState()
    )

    /*
    ** DATABASE FUNCTIONS
    */

    // Get all saved notes, ordered by modified date
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    // Get all favorited notes, ordered by modified date
    fun getFavoriteNotes(): Flow<List<Note>> = noteDao.getFavoriteNotes()

    // Get one note given its ID
    fun getNoteById(noteId: Int): Flow<Note>{
        //create a new empty note if ID is 0
        if(noteId <= 0) return flowOf(Note())

        //otherwise, attempt to get note from DB
        else return noteDao.getNoteById(noteId)
    }

    // Save a note to the database
    suspend fun saveNote(note: Note) {
        note.modifiedTimestampInMillis = System.currentTimeMillis()
        noteDao.saveNote(note)
    }

    // Toggle the favorite status of the given note
    suspend fun toggleFavorite(note: Note){
        // Special case for new notes (ID 0)
        // noteDao.toggleFavorite() will fail for ID 0
        if(note.id <= 0){
            note.favorited = !note.favorited
            val newId = noteDao.saveNote(note)
            setCurrentNoteId(newId.toInt())
        }else {
            noteDao.toggleFavorite(
                id = note.id,
                favorited = !note.favorited
            )
        }
    }

    // Delete note from the database
    suspend fun deleteNote(note: Note){
        if (note.id > 0) noteDao.deleteNote(note)
    }

    /*
    ** PREFERENCES FUNCTIONS
    */

    fun saveTextSizePreference(textSize: Int){
        viewModelScope.launch{
            userPreferencesRepository.saveTextSizePreference(textSize)
        }
    }

    fun saveColorSchemePreference(colorScheme: Int){
        viewModelScope.launch{
            userPreferencesRepository.saveColorSchemePreference(colorScheme)
        }
    }

    fun saveSelectedFontPreference(selectedFont: Int){
        viewModelScope.launch{
            userPreferencesRepository.saveSelectedFontPreference(selectedFont)
        }
    }

    fun saveShowFavoritesPreference(showFavorites: Boolean){
        viewModelScope.launch{
            userPreferencesRepository.saveShowFavoritesPreference(showFavorites)
        }
    }

    // Set the ID of the note currently being edited
    fun setCurrentNoteId(noteId: Int){
        viewModelScope.launch {
            userPreferencesRepository.setCurrentNoteId(noteId)
        }
    }

    // Contains members that can be accessed without a specific view model instance
    companion object{
        // Provide factory for instantiating view model
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as IvyNotesApplication)
                NoteViewModel(
                    userPreferencesRepository = application.userPreferencesRepository,
                    noteDao = application.noteDatabase.noteDao()
                )
            }
        }
    }
}

// Used to hold values related to UI
data class NotesUiState(
    val textSize: Int = R.string.pref_text_size_medium,
    val colorScheme: Int = R.string.pref_color_scheme_default,
    val showFavorites: Boolean = false,
    val selectedFont: Int = R.string.pref_font_style_default,

    val currentNoteId: Int = NEW_NOTE_ID
)
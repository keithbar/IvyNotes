package com.kbarber34.ivynotes

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kbarber34.ivynotes.data.Note
import com.kbarber34.ivynotes.data.PreferencesFontFamily
import com.kbarber34.ivynotes.data.PreferencesTextSize
import com.kbarber34.ivynotes.ui.NoteViewModel
import com.kbarber34.ivynotes.ui.NotesUiState
import com.kbarber34.ivynotes.ui.theme.IvyNotesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class EditNoteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Get the view model and UI state to share among composables
            // Also get the ID of the note currently being edited, which is used
            // for properly handling the case of editing a new note (ID 0)
            val noteViewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory)
            val notesUiState = noteViewModel.notesUiState.collectAsState(NotesUiState()).value
            val noteId = notesUiState.currentNoteId

            IvyNotesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    IvyNotesNoteEdit(
                        noteId = noteId,
                        notesUiState = notesUiState,
                        noteViewModel = noteViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun IvyNotesNoteEdit(
    noteId: Int,
    notesUiState: NotesUiState,
    noteViewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory)
){
    // Get current activity, used to end activity and return to main menu
    val activity = LocalContext.current as Activity

    // Get coroutine scope for calling view model's suspend functions
    val coroutineScope = rememberCoroutineScope()

    // Get the note currently being edited
    val note by noteViewModel.getNoteById(noteId).collectAsState(Note())

    // Failsafe for event when recomposition occurs after note is deleted,
    // but before the activity ends, resulting in a null note object
    if (note == null) return

    // Flag to indicate that note has been edited
    // and should be saved upon exit
    var noteHasBeenEdited by remember{ mutableStateOf(false) }

    // Get font size and family for text rendering
    val fontSize = dimensionResource(
        PreferencesTextSize.getPrimaryDimen(notesUiState.textSize)
    ).value.sp
    val fontFamily = PreferencesFontFamily.getFontFamily(notesUiState.selectedFont)

    // Replace behavior of system back button
    BackHandler{
        if(noteHasBeenEdited) {
            coroutineScope.launch {
                noteViewModel.saveNote(note)
            }
        }
        // End the activity and return to the main menu
        activity.finish()
    }

    Scaffold(
        topBar = {
            IvyNotesNoteEditAppBar(
                note = note,
                noteViewModel = noteViewModel,
                activity = activity,
                coroutineScope = coroutineScope,
                fontFamily = fontFamily,
                noteHasBeenEdited = noteHasBeenEdited
            )
        }
    ){ innerPadding ->
        // Remember the value of the note's contents in order to
        // update the screen as the user types
        var text by remember { mutableStateOf(note.contents) }
        LaunchedEffect(note){
            text = note.contents
        }
        TextField(
            value = text,
            textStyle = TextStyle(
                fontSize = fontSize,
                fontFamily = fontFamily
            ),
            onValueChange = {
                text = it
                note.contents = it
                noteHasBeenEdited = true
            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IvyNotesNoteEditAppBar(
    note: Note,
    noteViewModel: NoteViewModel,
    activity: Activity,
    coroutineScope: CoroutineScope,
    fontFamily: FontFamily,
    noteHasBeenEdited: Boolean
){
    // Recompose when favorite status changes to update favorite icon
    var favorited by remember{ mutableStateOf(note.favorited) }

    // Flag to determine if dialog box should appear
    val openConfirmDeleteDialog = remember{ mutableStateOf(false) }
    if(openConfirmDeleteDialog.value){
        // Dialog box asking user to confirm deletion of current note
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_dialog),
                    fontFamily = fontFamily
                )
            },
            onDismissRequest = { openConfirmDeleteDialog.value = false },
            confirmButton = {
                TextButton(onClick = {
                    openConfirmDeleteDialog.value = false
                    coroutineScope.launch{
                        noteViewModel.deleteNote(note)
                    }
                    // End activity and return to main menu
                    activity.finish()
                }) {
                    Text(
                        text = stringResource(R.string.delete),
                        fontFamily = fontFamily
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { openConfirmDeleteDialog.value = false }) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontFamily = fontFamily
                    )
                }
            }
        )
    }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                fontFamily = fontFamily
            )
        },
        navigationIcon = {
            // Back button, save note and return to main menu when tapped
            IconButton(onClick = {
                if(noteHasBeenEdited) {
                    coroutineScope.launch {
                        noteViewModel.saveNote(note)
                    }
                }
                activity.finish()
            }){
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            // Button for toggling the note's favorite status
            IconButton(onClick = {
                coroutineScope.launch {
                    noteViewModel.toggleFavorite(note)
                }
            }){
                if (note.favorited){
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = stringResource(R.string.favorite_toggle)
                    )
                }
                else{
                    Icon(
                        painter = painterResource(R.drawable.baseline_star_outline_24),
                        contentDescription = stringResource(R.string.favorite_toggle)
                    )
                }
            }

            // Button to open note deletion dialog
            IconButton(onClick = {
                openConfirmDeleteDialog.value = true
            }){
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            }
        }
    )
}
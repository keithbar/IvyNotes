package com.kbarber34.ivynotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kbarber34.ivynotes.data.Note
import com.kbarber34.ivynotes.data.PreferencesFontFamily
import com.kbarber34.ivynotes.data.PreferencesTextSize
import com.kbarber34.ivynotes.ui.NoteViewModel
import com.kbarber34.ivynotes.ui.NotesUiState
import com.kbarber34.ivynotes.ui.theme.IvyNotesTheme
import com.kbarber34.ivynotes.ui.utils.getNoteTimestampAsString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Get the view model and UI state to share among composables
            val noteViewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory)
            val notesUiState = noteViewModel.notesUiState.collectAsState(NotesUiState()).value

            IvyNotesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    IvyNotesApp(
                        noteViewModel = noteViewModel,
                        notesUiState = notesUiState
                    )
                }
            }
        }
    }
}

// Primary composable for main activity.
// Calls other composables.
@Composable
fun IvyNotesApp(
    noteViewModel: NoteViewModel,
    notesUiState: NotesUiState
){
    // State that determines whether the navigation menu is open or closed
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Get coroutine scope for calling view model's suspend functions
    val scope = rememberCoroutineScope()

    // Get context for launching additional activities
    val context = LocalContext.current

    // Get the font family from the UI state for rendering text
    val fontFamily = PreferencesFontFamily.getFontFamily(notesUiState.selectedFont)

    // Navigation menu
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet{
                // Navigation menu header
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = dimensionResource(R.dimen.font_size_header).value.sp,
                    fontFamily = fontFamily,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_large))
                )

                // Menu item to toggle showing favorite notes
                NavigationDrawerItem(
                    label = {
                        if(notesUiState.showFavorites) {
                            Text(
                                text = stringResource(R.string.navigation_show_all),
                                fontSize = dimensionResource(R.dimen.font_size_nav_menu).value.sp,
                                fontFamily = fontFamily,
                                modifier = Modifier
                                    .padding(start = dimensionResource(R.dimen.padding_large))
                            )
                        }
                        else{
                            Text(
                                text = stringResource(R.string.navigation_show_favorites),
                                fontSize = dimensionResource(R.dimen.font_size_nav_menu).value.sp,
                                fontFamily = fontFamily,
                                modifier = Modifier
                                    .padding(start = dimensionResource(R.dimen.padding_large))
                            )
                        }
                    },
                    selected = false,
                    // When selected, close the menu and toggle the showFavorites value
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            noteViewModel.saveShowFavoritesPreference(!notesUiState.showFavorites)
                        }
                    }
                )

                // Menu item to navigate to preferences screen
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = stringResource(R.string.navigation_preferences),
                            fontSize = dimensionResource(R.dimen.font_size_nav_menu).value.sp,
                            fontFamily = fontFamily,
                            modifier = Modifier
                                .padding(start = dimensionResource(R.dimen.padding_large))
                        )
                    },
                    selected = false,
                    onClick = {
                        val intent = Intent(context, PreferencesActivity::class.java)
                        context.startActivity(intent)
                    }
                )

                //Menu item to navigate to help screen
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = stringResource(R.string.navigation_help),
                            fontSize = dimensionResource(R.dimen.font_size_nav_menu).value.sp,
                            fontFamily = fontFamily,
                            modifier = Modifier
                                .padding(start = dimensionResource(R.dimen.padding_large))
                        )
                    },
                    selected = false,
                    onClick = {
                        val intent = Intent(context, HelpActivity::class.java)
                        context.startActivity(intent)
                    }
                )

                // Spacer used to push the credits text to the bottom
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.credits),
                    fontFamily = fontFamily,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_small))
                )
            }
        }
    ) {
        IvyNotesNoteList(
            fontFamily = fontFamily,
            scope = scope,
            context = context,
            drawerState = drawerState,
            noteViewModel = noteViewModel,
            notesUiState = notesUiState
        )
    }
}

@Composable
fun IvyNotesNoteList(
    fontFamily: FontFamily,
    scope: CoroutineScope,
    context: Context,
    drawerState: DrawerState,
    noteViewModel: NoteViewModel,
    notesUiState: NotesUiState
){
    // Get a list of all notes in database
    val allNotes by noteViewModel.getAllNotes().collectAsState(listOf())

    // Get a list of all favorited notes in database
    // Collect as state in order to trigger recomposition when
    // the list of favorited notes changes
    val favoriteNotes by noteViewModel.getFavoriteNotes().collectAsState(listOf())

    // Select which set of notes to display
    val noteList =
        if(notesUiState.showFavorites) favoriteNotes
        else allNotes

    Scaffold(
        topBar = {
            IvyNotesNoteListAppBar(
                fontFamily = fontFamily,
                // Open or close navigation menu when nav button is tapped
                onNavigationClick = {
                    scope.launch{
                        drawerState.apply{
                            if(isClosed) open()
                            else close()
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // Button for creating a new note, anchored to corner of screen
            FloatingActionButton(
                // Navigate to edit screen
                onClick = {
                    val intent = Intent(context, EditNoteActivity::class.java)
                    noteViewModel.setCurrentNoteId(NEW_NOTE_ID)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Create,
                    contentDescription = stringResource(R.string.new_note)
                )
            }
        }
    ) { innerPadding ->

        // Column displaying the list of notes
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.spacing_note_list)
            )
        ) {
            // For each note with a unique ID, add it to the displayed list
            items(noteList, key = { note -> note.id }) { note ->
                IvyNotesNoteListItem(
                    note = note,
                    noteViewModel = noteViewModel,
                    notesUiState = notesUiState,
                    scope = scope,
                    fontFamily = fontFamily,
                    onNoteClick = {
                        // Navigate to the edit screen when a note is tapped
                        val intent = Intent(context, EditNoteActivity::class.java)
                        noteViewModel.setCurrentNoteId(note.id)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IvyNotesNoteListAppBar(
    fontFamily: FontFamily,
    onNavigationClick: () -> Unit
){
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                fontFamily = fontFamily,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigationClick
            ){
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu Button"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IvyNotesNoteListItem(
    note: Note,
    noteViewModel: NoteViewModel,
    notesUiState: NotesUiState,
    scope: CoroutineScope,
    fontFamily: FontFamily,
    onNoteClick: () -> Unit = {}
){
    val fontSize = dimensionResource(
        PreferencesTextSize.getPrimaryDimen(notesUiState.textSize)
    ).value.sp

    Card(
        onClick = onNoteClick
    ){
        Row{
            // Button for toggling the note's favorite status
            IconButton(
                onClick = {
                    scope.launch {
                        noteViewModel.toggleFavorite(note)
                    }
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ){
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_note_list_item))
                    .padding(end = dimensionResource(R.dimen.padding_note_list_item))
            ){
                // Show when the note was last modified
                Text(
                    text = getNoteTimestampAsString(note),
                    fontSize = fontSize,
                    fontFamily = fontFamily,
                )

                // Show a one line preview of the note
                Text(
                    text = note.contents,
                    fontSize = fontSize,
                    fontFamily = fontFamily,
                    maxLines = MAX_LINES_PREVIEW,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
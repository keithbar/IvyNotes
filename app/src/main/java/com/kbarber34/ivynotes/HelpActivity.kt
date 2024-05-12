package com.kbarber34.ivynotes

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kbarber34.ivynotes.data.PreferencesFontFamily
import com.kbarber34.ivynotes.data.PreferencesTextSize
import com.kbarber34.ivynotes.ui.NoteViewModel
import com.kbarber34.ivynotes.ui.NotesUiState
import com.kbarber34.ivynotes.ui.theme.IvyNotesTheme

class HelpActivity : ComponentActivity() {
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
                    IvyNotesHelp(
                        notesUiState = notesUiState
                    )
                }
            }
        }
    }
}

@Composable
fun IvyNotesHelp(
    notesUiState: NotesUiState
) {
    // Get font size and family for text rendering
    val fontSize = dimensionResource(
        PreferencesTextSize.getPrimaryDimen(notesUiState.textSize)
    ).value.sp
    val fontFamily = PreferencesFontFamily.getFontFamily(notesUiState.selectedFont)

    Scaffold(
        topBar = {
            IvyNotesHelpAppBar(fontFamily)
        }
    ) { innerPadding ->
        Text(
            text = stringResource(R.string.help_text),
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = fontSize
            ),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_extra_large))
                .verticalScroll(rememberScrollState())
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IvyNotesHelpAppBar(
    fontFamily: FontFamily
) {
    // Get current activity,
    // used to navigate back to main menu
    val activity = LocalContext.current as Activity

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.help),
                fontFamily = fontFamily
            )
        },
        navigationIcon = {
            // Button to end the activity and return to main menu
            IconButton(onClick = {
                activity.finish()
            }){
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}
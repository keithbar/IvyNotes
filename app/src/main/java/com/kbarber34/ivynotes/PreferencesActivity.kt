package com.kbarber34.ivynotes

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kbarber34.ivynotes.data.PreferencesColorScheme
import com.kbarber34.ivynotes.data.PreferencesFontFamily
import com.kbarber34.ivynotes.data.PreferencesTextSize
import com.kbarber34.ivynotes.ui.NoteViewModel
import com.kbarber34.ivynotes.ui.NotesUiState
import com.kbarber34.ivynotes.ui.theme.IvyNotesTheme

class PreferencesActivity : ComponentActivity() {
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
                    IvyNotesPreferences(
                        noteViewModel = noteViewModel,
                        notesUiState = notesUiState
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IvyNotesPreferences(
    noteViewModel: NoteViewModel,
    notesUiState: NotesUiState
){
    // Flags to determine if each selection box is expanded
    var textDisplaySizeExpanded by remember { mutableStateOf(false) }
    var backgroundColorExpanded by remember { mutableStateOf(false) }
    var fontFamilyExpanded by remember { mutableStateOf(false) }

    // Get font family for rendering text
    val fontFamily = PreferencesFontFamily.getFontFamily(notesUiState.selectedFont)

    Scaffold(
        topBar = {
            IvyNotesPreferencesAppBar(
                fontFamily = fontFamily
            )
        }
    ){ innerPadding ->
        // Displays a list of user preferences.
        // When a selection is made, save it to the datastore
        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.spacing_preferences)
            ),
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                .padding(top = dimensionResource(R.dimen.padding_medium))
        ){
            // Preference selection for text size
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.pref_text_size),
                    fontSize = dimensionResource(R.dimen.font_size_preferences).value.sp,
                    fontFamily = fontFamily,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                ExposedDropdownMenuBox(
                    expanded = textDisplaySizeExpanded,
                    onExpandedChange = {
                        textDisplaySizeExpanded = !textDisplaySizeExpanded
                    }
                ) {
                    // Display currently selected text size as default
                    TextField(
                        value = stringResource(notesUiState.textSize),
                        textStyle = TextStyle(
                            fontSize = dimensionResource(R.dimen.font_size_preferences_options).value.sp,
                            fontFamily = fontFamily
                        ),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = textDisplaySizeExpanded
                            )
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = textDisplaySizeExpanded,
                        onDismissRequest = { textDisplaySizeExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(PreferencesTextSize.SMALL.textSizeString),
                                fontFamily = fontFamily
                            ) },
                            onClick = {
                                noteViewModel.saveTextSizePreference(
                                    PreferencesTextSize.SMALL.textSizeString
                                )
                                textDisplaySizeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(PreferencesTextSize.MEDIUM.textSizeString),
                                fontFamily = fontFamily
                            ) },
                            onClick = {
                                noteViewModel.saveTextSizePreference(
                                    PreferencesTextSize.MEDIUM.textSizeString
                                )
                                textDisplaySizeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(PreferencesTextSize.LARGE.textSizeString),
                                fontFamily = fontFamily
                            ) },
                            onClick = {
                                noteViewModel.saveTextSizePreference(
                                    PreferencesTextSize.LARGE.textSizeString
                                )
                                textDisplaySizeExpanded = false
                            }
                        )
                    }
                }
            }
            Divider()
            // Preference selection for font
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.pref_font_style),
                    fontSize = dimensionResource(R.dimen.font_size_preferences).value.sp,
                    fontFamily = fontFamily,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                ExposedDropdownMenuBox(
                    expanded = fontFamilyExpanded,
                    onExpandedChange = {
                        fontFamilyExpanded = !fontFamilyExpanded
                    }
                ) {
                    // Display currently selected font style as default
                    TextField(
                        value = stringResource(notesUiState.selectedFont),
                        textStyle = TextStyle(
                            fontSize = dimensionResource(R.dimen.font_size_preferences_options).value.sp,
                            fontFamily = fontFamily
                        ),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = fontFamilyExpanded
                            )
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = fontFamilyExpanded,
                        onDismissRequest = { fontFamilyExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(
                                    PreferencesFontFamily.DEFAULT.fontString
                                ),
                                fontFamily = fontFamily
                            ) },
                            onClick = {
                                noteViewModel.saveSelectedFontPreference(
                                    PreferencesFontFamily.DEFAULT.fontString
                                )
                                fontFamilyExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(
                                    PreferencesFontFamily.SERIF.fontString
                                ),
                                fontFamily = fontFamily
                            ) },
                            onClick = {
                                noteViewModel.saveSelectedFontPreference(
                                    PreferencesFontFamily.SERIF.fontString
                                )
                                fontFamilyExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(
                                    PreferencesFontFamily.MONOSPACE.fontString
                                ),
                                fontFamily = fontFamily
                            ) },
                            onClick = {
                                noteViewModel.saveSelectedFontPreference(
                                    PreferencesFontFamily.MONOSPACE.fontString
                                )
                                fontFamilyExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(
                                    PreferencesFontFamily.CURSIVE.fontString
                                ),
                                fontFamily = fontFamily
                            ) },
                            onClick = {
                                noteViewModel.saveSelectedFontPreference(
                                    PreferencesFontFamily.CURSIVE.fontString
                                )
                                fontFamilyExpanded = false
                            }
                        )
                    }
                }
            }
            Divider()
            // Preference selection for color scheme
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.pref_color_scheme),
                    fontSize = dimensionResource(R.dimen.font_size_preferences).value.sp,
                    fontFamily = fontFamily,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                ExposedDropdownMenuBox(
                    expanded = backgroundColorExpanded,
                    onExpandedChange = {
                        backgroundColorExpanded = !backgroundColorExpanded
                    }
                ) {
                    // Display currently selected color scheme as default
                    TextField(
                        value = stringResource(notesUiState.colorScheme),
                        textStyle = TextStyle(
                            fontSize = dimensionResource(R.dimen.font_size_preferences_options).value.sp,
                            fontFamily = fontFamily
                        ),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = backgroundColorExpanded
                            )
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = backgroundColorExpanded,
                        onDismissRequest = { backgroundColorExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(
                                    PreferencesColorScheme.SYSTEM_DEFAULT.colorScheme
                                ),
                                fontFamily = fontFamily
                            ) },
                            onClick = {
                                noteViewModel.saveColorSchemePreference(
                                    PreferencesColorScheme.SYSTEM_DEFAULT.colorScheme
                                )
                                backgroundColorExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(
                                    PreferencesColorScheme.LIGHT_MODE.colorScheme
                                ),
                                fontFamily = fontFamily
                            ) },
                            onClick = {
                                noteViewModel.saveColorSchemePreference(
                                    PreferencesColorScheme.LIGHT_MODE.colorScheme
                                )
                                backgroundColorExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(
                                text = stringResource(
                                    PreferencesColorScheme.DARK_MODE.colorScheme
                                ),
                                fontFamily = fontFamily
                            ) },
                            onClick = {
                                noteViewModel.saveColorSchemePreference(
                                    PreferencesColorScheme.DARK_MODE.colorScheme
                                )
                                backgroundColorExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IvyNotesPreferencesAppBar(
    fontFamily: FontFamily
) {
    // Get current activity,
    // used to navigate away from activity back to main menu
    val activity = LocalContext.current as Activity

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.preferences),
                fontFamily = fontFamily
            )
        },
        navigationIcon = {
            // Button to end activity and return to main menu
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
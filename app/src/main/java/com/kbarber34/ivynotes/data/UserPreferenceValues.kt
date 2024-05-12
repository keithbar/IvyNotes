package com.kbarber34.ivynotes.data

import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.compose.ui.text.font.FontFamily
import com.kbarber34.ivynotes.R

// Possible values for the Text Size preference
enum class PreferencesTextSize(
    @StringRes val textSizeString: Int,
    @DimenRes val textSizePrimaryDimen: Int
){
    SMALL(
        textSizeString = R.string.pref_text_size_small,
        textSizePrimaryDimen = R.dimen.pref_font_size_small
    ),
    MEDIUM(
        textSizeString = R.string.pref_text_size_medium,
        textSizePrimaryDimen = R.dimen.pref_font_size_medium)
    ,
    LARGE(
        textSizeString = R.string.pref_text_size_large,
        textSizePrimaryDimen = R.dimen.pref_font_size_large
    );

    companion object{
        private val map = entries.associateBy(PreferencesTextSize::textSizeString)

        fun getPrimaryDimen(textSize: Int): Int{
            return map[textSize]?.textSizePrimaryDimen ?: R.dimen.pref_font_size_medium
        }

        // Ensure supplied size is a valid option, otherwise return default
        fun validate(textSize: Int): Int{
            return map[textSize]?.textSizeString ?: R.string.pref_text_size_medium
        }
    }
}

// Possible values for the Color Scheme preference
enum class PreferencesColorScheme(@StringRes val colorScheme: Int){
    SYSTEM_DEFAULT(R.string.pref_color_scheme_default),
    LIGHT_MODE(R.string.pref_color_scheme_light),
    DARK_MODE(R.string.pref_color_scheme_dark);

    companion object{
        private val map = entries.associateBy(PreferencesColorScheme::colorScheme)

        // Ensure supplied color scheme is a valid option, otherwise return default
        fun validate(colorScheme: Int): Int{
            return map[colorScheme]?.colorScheme ?: R.string.pref_color_scheme_default
        }
    }
}

//Possible values for the font preference
enum class PreferencesFontFamily(
    val fontString: Int,
    val fontFamily: FontFamily
){
    DEFAULT(
        fontString = R.string.pref_font_style_default,
        fontFamily = FontFamily.Default
    ),
    SERIF(
        fontString = R.string.pref_font_style_serif,
        fontFamily = FontFamily.Serif,
    ),
    MONOSPACE(
        fontString = R.string.pref_font_style_monospace,
        fontFamily = FontFamily.Monospace
    ),
    CURSIVE(
        fontString = R.string.pref_font_style_cursive,
        fontFamily = FontFamily.Cursive,
    );

    companion object {
        private val map = entries.associateBy(PreferencesFontFamily::fontString)

        fun getFontFamily(fontString: Int): FontFamily {
            return map[fontString]?.fontFamily ?: FontFamily.Default
        }

        // Ensure supplied font is a valid option, otherwise return default
        fun validate(fontString: Int): Int{
            return map[fontString]?.fontString ?: R.string.pref_font_style_default
        }
    }
}
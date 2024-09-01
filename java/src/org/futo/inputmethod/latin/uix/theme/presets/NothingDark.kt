package org.futo.inputmethod.latin.uix.theme.presets

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.theme.ThemeOption
import org.futo.inputmethod.latin.uix.theme.selector.ThemePreview

private val md_theme_dark_primary = Color(0xFFD0BCFF)
private val md_theme_dark_onPrimary = Color(0xFF381E72)
private val md_theme_dark_primaryContainer = Color(0xFF4F378B)
private val md_theme_dark_onPrimaryContainer = Color(0xFFEADDFF)
private val md_theme_dark_secondary = Color(0xFFFFFFFF)
private val md_theme_dark_onSecondary = Color(0xFF000000)
private val md_theme_dark_secondaryContainer = Color(0xFF4A4458)
private val md_theme_dark_onSecondaryContainer = Color(0xFFE8DEF8)
private val md_theme_dark_tertiary = Color(0xFFEFB8C8)
private val md_theme_dark_onTertiary = Color(0xFF492532)
private val md_theme_dark_tertiaryContainer = Color(0xFF633B48)
private val md_theme_dark_onTertiaryContainer = Color(0xFFFFD8E4)
private val md_theme_dark_error = Color(0xFFF2B8B5)
private val md_theme_dark_onError = Color(0xFF601410)
private val md_theme_dark_errorContainer = Color(0xFF8C1D18)
private val md_theme_dark_onErrorContainer = Color(0xFFF9DEDC)
private val md_theme_dark_outline = Color(0xFF938F99)
private val md_theme_dark_background = Color(0xFF000000)
private val md_theme_dark_onBackground = Color(0xFFE6E1E5)
private val md_theme_dark_surface = Color(0xFF000000)
private val md_theme_dark_onSurface = Color(0xFFE6E1E5)
private val md_theme_dark_surfaceVariant = Color(0xFF49454F)
private val md_theme_dark_onSurfaceVariant = Color(0xFFCAC4D0)
private val md_theme_dark_inverseSurface = Color(0xFFE6E1E5)
private val md_theme_dark_inverseOnSurface = Color(0xFF313033)
private val md_theme_dark_inversePrimary = Color(0xFF1B1A1F)
private val md_theme_dark_shadow = Color(0xFF000000)
private val md_theme_dark_surfaceTint = Color(0xFFFFFFFF)
private val md_theme_dark_outlineVariant = Color(0xFF49454F)
private val md_theme_dark_scrim = Color(0xFF000000)

//private val nothing_red = Color(0xFFD71921)
//private val nothing_white = Color(0xFFF1F1F1)
//private val nothing_black = Color(0xFF000000)
//private val nothing_grey = Color(0xFF0D0C11);
//private val nothing_grey_2 = Color(0xFF1B1A1F);
//private val nothing_grey_3 = Color(0xFF151419);

private val nothing_red = Color(0xFFD71921)
private val nothing_white = Color(0xFFF1F1F1)
private val nothing_black = Color(0xFF000000)
private val nothing_grey = Color(0xFF0D0C11);
private val nothing_grey_2 = Color(0xFF0D0C11);
private val nothing_grey_3 = Color(0xFF151419);

private val colorScheme = darkColorScheme(
    primary = nothing_red,
    onPrimary = nothing_white,
    primaryContainer = nothing_white,
    onPrimaryContainer = nothing_black,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = nothing_grey,
    onTertiary = nothing_white,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    outline = md_theme_dark_outline,
    background = md_theme_dark_surface,
    onBackground = md_theme_dark_onBackground,
    surface = nothing_grey_2,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surface,
    onSurfaceVariant = nothing_white,
    inverseSurface = md_theme_dark_inverseSurface,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

fun getNothingDarkColorScheme(): ColorScheme {
    return colorScheme
}

val NothingDark = ThemeOption(
    dynamic = false,
    key = "NothingDark",
    name = R.string.nothing_dark_theme_name,
    available = { true }
) {
    colorScheme
}

@Composable
@Preview
private fun PreviewTheme() {
    ThemePreview(NothingDark)
}
package app.ember.studio.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val THEME_DATASTORE_NAME = "theme_preferences"
internal const val PREF_SELECTED_THEME_OPTION = "theme_selected_option"
internal const val PREF_USE_DARK_THEME = "theme_use_dark_theme"
internal const val PREF_USE_DYNAMIC_COLOR = "theme_use_dynamic_color"
internal const val PREF_USE_AMOLED_BLACK = "theme_use_amoled_black"

internal val SelectedThemeOptionKey = intPreferencesKey(PREF_SELECTED_THEME_OPTION)
internal val UseDarkThemeKey = booleanPreferencesKey(PREF_USE_DARK_THEME)
internal val UseDynamicColorKey = booleanPreferencesKey(PREF_USE_DYNAMIC_COLOR)
internal val UseAmoledBlackKey = booleanPreferencesKey(PREF_USE_AMOLED_BLACK)

val Context.themePreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = THEME_DATASTORE_NAME
)

data class ThemePreferences(
    val selectedOptionIndex: Int = 0,
    val useDarkTheme: Boolean = true,
    val useDynamicColor: Boolean = false,
    val useAmoledBlack: Boolean = false
) {
    companion object {
        val DEFAULT = ThemePreferences()
    }
}

class ThemePreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val themePreferences: Flow<ThemePreferences> =
        dataStore.data
            .catch { throwable ->
                if (throwable is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw throwable
                }
            }
            .map(::mapThemePreferences)

    suspend fun setSelectedThemeOption(index: Int) {
        dataStore.edit { preferences ->
            preferences[SelectedThemeOptionKey] = index
        }
    }

    suspend fun setDarkTheme(useDarkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[UseDarkThemeKey] = useDarkTheme
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[UseDynamicColorKey] = enabled
        }
    }

    suspend fun setAmoledBlack(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[UseAmoledBlackKey] = enabled
        }
    }
}

internal fun mapThemePreferences(preferences: Preferences): ThemePreferences {
    val selectedIndex = preferences[SelectedThemeOptionKey] ?: ThemePreferences.DEFAULT.selectedOptionIndex
    val useDarkTheme = preferences[UseDarkThemeKey] ?: ThemePreferences.DEFAULT.useDarkTheme
    val useDynamicColor = preferences[UseDynamicColorKey] ?: ThemePreferences.DEFAULT.useDynamicColor
    val useAmoledBlack = preferences[UseAmoledBlackKey] ?: ThemePreferences.DEFAULT.useAmoledBlack
    return ThemePreferences(
        selectedOptionIndex = selectedIndex,
        useDarkTheme = useDarkTheme,
        useDynamicColor = useDynamicColor,
        useAmoledBlack = useAmoledBlack
    )
}

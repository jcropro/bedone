package app.ember.studio.theme

import androidx.datastore.preferences.core.mutablePreferencesOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ThemePreferencesRepositoryTest {

    @Test
    fun `mapThemePreferences returns defaults when no values stored`() {
        val preferences = mutablePreferencesOf()

        val mapped = mapThemePreferences(preferences)

        assertEquals(ThemePreferences.DEFAULT, mapped)
    }

    @Test
    fun `mapThemePreferences reads stored values`() {
        val preferences = mutablePreferencesOf(
            SelectedThemeOptionKey to 3,
            UseDarkThemeKey to false
        )

        val mapped = mapThemePreferences(preferences)

        assertEquals(3, mapped.selectedOptionIndex)
        assertFalse(mapped.useDarkTheme)
    }
}

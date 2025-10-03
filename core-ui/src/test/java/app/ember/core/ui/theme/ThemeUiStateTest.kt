package app.ember.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ThemeUiStateTest {

    private val lightScheme = lightColorScheme(primary = Color(0xFFFF7A1A))
    private val darkScheme = darkColorScheme(primary = Color(0xFFFD8843))

    private val sampleOption = ThemeOption(
        labelRes = 0,
        colorScheme = { useDarkTheme -> if (useDarkTheme) darkScheme else lightScheme }
    )

    @Test
    fun `selected option returns within bounds`() {
        val otherOption = ThemeOption(
            labelRes = 1,
            colorScheme = { useDarkTheme -> if (useDarkTheme) darkScheme else lightScheme }
        )
        val options = listOf(sampleOption, otherOption)
        val state = ThemeUiState(options = options, selectedOptionIndex = 1, useDarkTheme = false)

        assertSame(options[1], state.selectedOption)
    }

    @Test
    fun `selected option clamps negative index`() {
        val otherOption = ThemeOption(
            labelRes = 1,
            colorScheme = { useDarkTheme -> if (useDarkTheme) darkScheme else lightScheme }
        )
        val options = listOf(sampleOption, otherOption)
        val state = ThemeUiState(options = options, selectedOptionIndex = -2, useDarkTheme = true)

        assertSame(options.first(), state.selectedOption)
    }

    @Test
    fun `selected option clamps index beyond size`() {
        val otherOption = ThemeOption(
            labelRes = 1,
            colorScheme = { useDarkTheme -> if (useDarkTheme) darkScheme else lightScheme }
        )
        val options = listOf(sampleOption, otherOption)
        val state = ThemeUiState(options = options, selectedOptionIndex = 99, useDarkTheme = true)

        assertSame(options.last(), state.selectedOption)
    }

    @Test
    fun `theme state requires at least one option`() {
        assertFailsWith<IllegalArgumentException> {
            ThemeUiState(options = emptyList())
        }
    }

    @Test
    fun `theme option returns matching color scheme`() {
        val option = ThemeOption(
            labelRes = 0,
            colorScheme = { useDarkTheme -> if (useDarkTheme) darkScheme else lightScheme }
        )

        assertSame(lightScheme, option.colorScheme(false))
        assertSame(darkScheme, option.colorScheme(true))
    }

    @Test
    fun `default theme options are stable`() {
        val first = defaultThemeOptions()
        val second = defaultThemeOptions()

        assertTrue(first.isNotEmpty(), "Default options must not be empty")
        assertEquals(first.map { it.labelRes }, second.map { it.labelRes })
    }

    @Test
    fun `withSelectedOption clamps index and avoids redundant copies`() {
        val otherOption = ThemeOption(
            labelRes = 1,
            colorScheme = { useDarkTheme -> if (useDarkTheme) darkScheme else lightScheme }
        )
        val options = listOf(sampleOption, otherOption)
        val state = ThemeUiState(options = options, selectedOptionIndex = 0, useDarkTheme = false)

        val sameState = state.withSelectedOption(0)
        assertSame(state, sameState)

        val clampedNegative = state.withSelectedOption(-5)
        assertSame(options.first(), clampedNegative.selectedOption)
        assertNotSame(state, clampedNegative)

        val clampedHigh = state.withSelectedOption(42)
        assertSame(options.last(), clampedHigh.selectedOption)
        assertEquals(1, clampedHigh.selectedOptionIndex)
    }

    @Test
    fun `withDarkTheme toggles mode and returns same instance when unchanged`() {
        val state = ThemeUiState(options = listOf(sampleOption), selectedOptionIndex = 0, useDarkTheme = true)

        val sameState = state.withDarkTheme(true)
        assertSame(state, sameState)

        val toggled = state.withDarkTheme(false)
        assertNotSame(state, toggled)
        assertTrue(!toggled.useDarkTheme)
    }
}

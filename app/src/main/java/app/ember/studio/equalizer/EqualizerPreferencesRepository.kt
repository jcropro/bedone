package app.ember.studio.equalizer

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val EQUALIZER_DATASTORE_NAME = "equalizer_preferences"

private val EqEnabledKey = booleanPreferencesKey("equalizer_enabled")
private val VirtualizerStrengthKey = intPreferencesKey("virtualizer_strength")
private val PresetIndexKey = intPreferencesKey("equalizer_preset_index")
private val BandLevelsKey = stringPreferencesKey("equalizer_band_levels")
private val BassBoostStrengthKey = intPreferencesKey("equalizer_bass_boost_strength")
private val ReverbPresetKey = intPreferencesKey("equalizer_reverb_preset")

val Context.equalizerPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = EQUALIZER_DATASTORE_NAME
)

data class EqualizerPreferences(
    val isEnabled: Boolean = false,
    val selectedPresetIndex: Int = -1,
    val bandLevels: List<Int> = emptyList(),
    val bassBoostStrength: Int = 0,
    val reverbPreset: Int = 0,
    val virtualizerStrength: Int = 0
)

class EqualizerPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val preferences: Flow<EqualizerPreferences> =
        dataStore.data
            .catch { throwable ->
                if (throwable is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw throwable
                }
            }
            .map { prefs ->
                val presetIndex = prefs[PresetIndexKey] ?: -1
                val bandCsv = prefs[BandLevelsKey]
                val bands = bandCsv?.split(',')?.mapNotNull { it.toIntOrNull() } ?: emptyList()
                EqualizerPreferences(
                    isEnabled = prefs[EqEnabledKey] ?: false,
                    selectedPresetIndex = presetIndex,
                    bandLevels = bands,
                    bassBoostStrength = (prefs[BassBoostStrengthKey] ?: 0).coerceIn(0, 1000),
                    reverbPreset = prefs[ReverbPresetKey] ?: 0,
                    virtualizerStrength = (prefs[VirtualizerStrengthKey] ?: 0).coerceIn(0, 1000)
                )
            }

    suspend fun setEqualizerEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[EqEnabledKey] = enabled
        }
    }

    suspend fun setVirtualizerStrength(strength: Int) {
        val clamped = strength.coerceIn(0, 1000)
        dataStore.edit { prefs ->
            if (clamped == 0) {
                // Keep a 0 to indicate disabled.
                prefs[VirtualizerStrengthKey] = 0
            } else {
                prefs[VirtualizerStrengthKey] = clamped
            }
        }
    }

    suspend fun setPresetIndex(index: Int) {
        dataStore.edit { prefs ->
            prefs[PresetIndexKey] = index
        }
    }

    suspend fun setBandLevels(levels: List<Int>) {
        val csv = levels.joinToString(",")
        dataStore.edit { prefs ->
            if (levels.isEmpty()) {
                prefs.remove(BandLevelsKey)
            } else {
                prefs[BandLevelsKey] = csv
            }
        }
    }

    suspend fun setBassBoostStrength(strength: Int) {
        val clamped = strength.coerceIn(0, 1000)
        dataStore.edit { prefs ->
            prefs[BassBoostStrengthKey] = clamped
        }
    }

    suspend fun setReverbPreset(preset: Int) {
        dataStore.edit { prefs ->
            prefs[ReverbPresetKey] = preset
        }
    }
}

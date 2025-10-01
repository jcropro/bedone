package app.ember.studio.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val ONBOARDING_DATASTORE_NAME = "onboarding_preferences"

private val OnboardingCompleteKey = booleanPreferencesKey("onboarding_complete")

val Context.onboardingPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = ONBOARDING_DATASTORE_NAME
)

data class OnboardingPreferences(
    val isComplete: Boolean = false
)

class OnboardingPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val preferences: Flow<OnboardingPreferences> =
        dataStore.data
            .catch { throwable ->
                if (throwable is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw throwable
                }
            }
            .map { preferences ->
                OnboardingPreferences(
                    isComplete = preferences[OnboardingCompleteKey] ?: false
                )
            }

    suspend fun setOnboardingComplete(isComplete: Boolean) {
        dataStore.edit { mutable ->
            if (isComplete) {
                mutable[OnboardingCompleteKey] = true
            } else {
                mutable.remove(OnboardingCompleteKey)
            }
        }
    }
}

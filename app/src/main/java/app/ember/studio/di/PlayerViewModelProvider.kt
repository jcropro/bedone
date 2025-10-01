@file:androidx.annotation.OptIn(UnstableApi::class)

package app.ember.studio.di

import android.app.Application
import androidx.annotation.OptIn as AndroidOptIn
import androidx.media3.common.util.UnstableApi
import app.ember.studio.PlayerViewModel

/**
 * Lightweight service locator to allow instrumentation tests to inject
 * PlayerViewModel.Dependencies without bringing a full DI framework.
 */
@AndroidOptIn(UnstableApi::class)
@UnstableApi
object PlayerViewModelProvider {
    @Volatile
    var dependenciesFactory: ((Application) -> PlayerViewModel.Dependencies)? = null

    fun clear() { dependenciesFactory = null }
}


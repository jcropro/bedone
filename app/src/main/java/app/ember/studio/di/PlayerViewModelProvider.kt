package app.ember.studio.di

import android.app.Application
import app.ember.studio.PlayerViewModel

/**
 * Lightweight service locator to allow instrumentation tests to inject
 * PlayerViewModel.Dependencies without bringing a full DI framework.
 */
object PlayerViewModelProvider {
    @Volatile
    var dependenciesFactory: ((Application) -> PlayerViewModel.Dependencies)? = null

    fun clear() { dependenciesFactory = null }
}


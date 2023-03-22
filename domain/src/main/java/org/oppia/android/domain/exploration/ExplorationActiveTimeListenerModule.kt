package org.oppia.android.domain.exploration

import dagger.Module
import dagger.Provides

/** Binds multiple dependencies that implement [ExplorationActiveTimeListener] into a set. */
@Module
class ExplorationActiveTimeListenerModule {
  @Provides
  fun provideExplorationActiveTimeListener(
    lifecycleObserverListenerImpl: ExplorationActiveTimeListenerImpl
  ): ExplorationActiveTimeListener {
    return lifecycleObserverListenerImpl
  }
}

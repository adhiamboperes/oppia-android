package org.oppia.android.app.devoptions

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Provides dependencies corresponding to implementation of DeveloperOptionsStarter. */
@InstallIn(SingletonComponent::class)
@Module
interface DeveloperOptionsStarterModule {
  @Binds
  fun bindsDeveloperOptionsStarter(
    impl: DeveloperOptionsStarterImpl
  ): DeveloperOptionsStarter
}

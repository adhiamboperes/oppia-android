package org.oppia.android.app.devoptions

import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Provides optional dependencies corresponding to the DeveloperOptionsStarter. */
@InstallIn(SingletonComponent::class)
@Module
interface DeveloperOptionsModule {
  @BindsOptionalOf fun provideDeveloperOptionsStarter(): DeveloperOptionsStarter
}

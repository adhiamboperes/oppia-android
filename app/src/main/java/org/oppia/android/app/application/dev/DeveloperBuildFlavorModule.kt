package org.oppia.android.app.application.dev

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oppia.android.app.model.BuildFlavor

/** Module for providing the compile-time [BuildFlavor] of developer-only builds of the app. */
@InstallIn(SingletonComponent::class)
@Module
class DeveloperBuildFlavorModule {
  @Provides
  fun provideDeveloperBuildFlavor(): BuildFlavor = BuildFlavor.DEVELOPER
}

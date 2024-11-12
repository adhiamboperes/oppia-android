package org.oppia.android.app.application.beta

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oppia.android.app.model.BuildFlavor

/** Module for providing the compile-time [BuildFlavor] of beta builds of the app. */
@InstallIn(SingletonComponent::class)
@Module
class BetaBuildFlavorModule {
  @Provides
  fun provideBetaBuildFlavor(): BuildFlavor = BuildFlavor.BETA
}

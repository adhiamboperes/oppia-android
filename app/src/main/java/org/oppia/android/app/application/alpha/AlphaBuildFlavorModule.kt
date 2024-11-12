package org.oppia.android.app.application.alpha

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oppia.android.app.model.BuildFlavor

/** Module for providing the compile-time [BuildFlavor] of alpha builds of the app. */
@InstallIn(SingletonComponent::class)
@Module
class AlphaBuildFlavorModule {
  @Provides
  fun provideAlphaBuildFlavor(): BuildFlavor = BuildFlavor.ALPHA
}

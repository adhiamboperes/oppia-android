package org.oppia.android.app.application.ga

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oppia.android.app.model.BuildFlavor

/** Module for providing the compile-time [BuildFlavor] of generally available builds of the app. */
@InstallIn(SingletonComponent::class)
@Module
class GaBuildFlavorModule {
  @Provides
  fun provideGaBuildFlavor(): BuildFlavor = BuildFlavor.GENERAL_AVAILABILITY
}

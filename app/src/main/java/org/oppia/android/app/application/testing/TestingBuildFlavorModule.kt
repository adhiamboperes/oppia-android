package org.oppia.android.app.application.testing

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oppia.android.app.model.BuildFlavor

/**
 * Module for providing the compile-time [BuildFlavor] of test environment exclusive builds of the
 * app.
 */
@InstallIn(SingletonComponent::class)
@Module
class TestingBuildFlavorModule {
  @Provides
  fun provideTestingBuildFlavor(): BuildFlavor = BuildFlavor.TESTING
}

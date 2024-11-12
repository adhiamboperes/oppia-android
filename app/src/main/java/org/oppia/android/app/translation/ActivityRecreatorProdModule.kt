package org.oppia.android.app.translation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Module for providing a production-compatible [ActivityRecreator]. */
@InstallIn(SingletonComponent::class)
@Module
interface ActivityRecreatorProdModule {
  @Binds
  fun provideActivityRecreator(impl: ActivityRecreatorImpl): ActivityRecreator
}

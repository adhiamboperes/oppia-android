package org.oppia.android.app.translation.testing

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oppia.android.app.translation.ActivityRecreator

/** Module to provide a test-compatible version of [ActivityRecreator]. */
@InstallIn(SingletonComponent::class)
@Module
interface ActivityRecreatorTestModule {
  @Binds
  fun provideActivityRecreator(impl: TestActivityRecreator): ActivityRecreator
}

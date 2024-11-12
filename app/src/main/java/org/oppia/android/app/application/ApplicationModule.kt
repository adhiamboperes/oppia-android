package org.oppia.android.app.application

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oppia.android.app.activity.ActivityComponentImpl

/** Provides core infrastructure needed to support all other dependencies in the app. */
@InstallIn(SingletonComponent::class)
@Module(subcomponents = [ActivityComponentImpl::class])
interface ApplicationModule {
  @Binds
  fun provideContext(application: Application): Context
}

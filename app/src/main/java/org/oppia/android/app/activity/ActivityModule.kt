package org.oppia.android.app.activity

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oppia.android.app.fragment.FragmentComponentImpl

/** Root activity module. */
@InstallIn(SingletonComponent::class)
@Module(subcomponents = [FragmentComponentImpl::class])
class ActivityModule

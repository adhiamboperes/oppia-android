package org.oppia.android.app.fragment

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oppia.android.app.view.ViewComponentImpl

/** Root fragment module. */
@InstallIn(SingletonComponent::class)
@Module(subcomponents = [ViewComponentImpl::class])
class FragmentModule

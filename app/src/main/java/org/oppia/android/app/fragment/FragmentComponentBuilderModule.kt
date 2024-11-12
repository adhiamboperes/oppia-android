package org.oppia.android.app.fragment

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Module for providing [FragmentComponentBuilderInjector]. */
@InstallIn(SingletonComponent::class)
@Module
interface FragmentComponentBuilderModule {
  @Binds
  fun bindFragmentComponentBuilder(impl: FragmentComponentImpl.Builder): FragmentComponent.Builder
}

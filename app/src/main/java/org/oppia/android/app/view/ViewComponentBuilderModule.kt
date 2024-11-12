package org.oppia.android.app.view

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Module for providing [ViewComponent.Builder]. */
@InstallIn(SingletonComponent::class)
@Module
interface ViewComponentBuilderModule {
  @Binds
  fun bindViewComponentBuilder(impl: ViewComponentImpl.Builder): ViewComponent.Builder
}

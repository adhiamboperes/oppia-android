package org.oppia.android.app.shim

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface ViewBindingShimModule {

  @Binds
  fun provideViewBindingShim(viewBindingShim: ViewBindingShimImpl): ViewBindingShim
}

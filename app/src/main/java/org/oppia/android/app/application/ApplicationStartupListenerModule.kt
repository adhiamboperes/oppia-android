package org.oppia.android.app.application

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import org.oppia.android.domain.oppialogger.ApplicationStartupListener

/** Binds multiple dependencies that implement [ApplicationStartupListener] into a set. */
@Module
@InstallIn(SingletonComponent::class)
interface ApplicationStartupListenerModule {
  @Multibinds
  fun bindStartupListenerSet(): Set<ApplicationStartupListener>
}

package org.oppia.android.app.application

import dagger.Module
import dagger.multibindings.Multibinds
import org.oppia.android.domain.oppialogger.ApplicationLifecycleObserverListener

/** Binds multiple dependencies that implement [ApplicationLifecycleObserverListener] into a set. */
@Module
interface ApplicationLifecycleObserverListenerModule {
  @Multibinds
  fun bindLifecycleObserverListenerSet(): Set<ApplicationLifecycleObserverListener>
}

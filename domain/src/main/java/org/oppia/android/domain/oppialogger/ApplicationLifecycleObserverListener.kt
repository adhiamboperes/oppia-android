package org.oppia.android.domain.oppialogger

/** Set of listeners that get created when the application lifecycle changes. */
interface ApplicationLifecycleObserverListener {

  /** Gets called when the application comes to the Foreground. */
  fun onAppInForeground()

  /** Gets called when the application goes to the background. */
  fun onAppInBackground()
}

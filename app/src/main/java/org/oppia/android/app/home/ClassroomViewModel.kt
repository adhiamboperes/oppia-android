package org.oppia.android.app.home

import androidx.fragment.app.Fragment
import org.oppia.android.app.model.Classroom
import org.oppia.android.app.viewmodel.ObservableViewModel

// A [ClassroomListViewModel] will be created to bind this [ClassroomViewModel] to the HomeViewModel
class ClassroomViewModel(
  private val fragment: Fragment,
  private val internalProfileId: Int,
  val classroom: Classroom
) : ObservableViewModel() {
  private val routeToClassroomTopicsListener = fragment as RouteToClassroomTopicsListener

  // This is called in the onClick method of the xml via data binding
  fun clickOnClassroomTile() {
    routeToClassroomTopicsListener.routeToClassroomTopics(internalProfileId, classroom.classroomId)
  }
}

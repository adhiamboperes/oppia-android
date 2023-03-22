package org.oppia.android.domain.survey

import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import org.oppia.android.app.model.ProfileId
import org.oppia.android.domain.exploration.TopicLearningTimeController
import org.oppia.android.domain.profile.ProfileManagementController
import org.oppia.android.util.data.DataProvider
import org.oppia.android.util.system.OppiaClock

private const val SURVEY_LAST_SHOWN_DATE_LIMIT_DAYS = 30
private val SURVEY_AGGREGATE_TOPIC_LEARNING_TIME_LIMIT_MINUTES = TimeUnit.MINUTES.toMillis(5)

/**
 * Controller for retrieving survey gating criteria and deciding if a survey should be shown.
 */
class SurveyGatingController @Inject constructor(
  private val oppiaClock: OppiaClock,
  private val topicLearningTimeController: TopicLearningTimeController,
  private val profileManagementController: ProfileManagementController
) {

  private val eveningLimit = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 22)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
  }
  private val morningLimit = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 9)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
  }

  /**
   * Returns a boolean indicating whether a survey can be shown.
   */
  fun shouldShowSurvey(
    profileId: ProfileId,
    topicId: String
  ): Boolean {
    return isSurveyTimeOfDayWindowOpen() &&
      !isSurveyLastShownDateLimitExceeded(profileId) &&
      isAggregateTopicLearningTimeThresholdMet(profileId, topicId)
  }

  /** Update the survey last shown timestamp for the specified profile. */
  internal fun updateSurveyLastShownTimestamp(profileId: ProfileId) {
    profileManagementController.setSurveyLastShownTimestamp(profileId)
  }

  private fun isSurveyTimeOfDayWindowOpen(): Boolean {
    return oppiaClock.getCurrentCalendar()
      .after(morningLimit) && oppiaClock.getCurrentCalendar().before(eveningLimit)
  }

  private fun isSurveyLastShownDateLimitExceeded(profileId: ProfileId): Boolean {
    // getSurveyLastShownTimestamp(profileId)
    return true
  }

  private fun getSurveyLastShownTimestamp(
    profileId: ProfileId
  ): DataProvider<Long?> {
    return profileManagementController.fetchSurveyLastShownTimestamp(profileId)
  }

  private fun isAggregateTopicLearningTimeThresholdMet(
    profileId: ProfileId,
    topicId: String
  ): Boolean {
    //getTopicAggregateLearningTime(profileId, topicId)
    return true
  }

  private fun getTopicAggregateLearningTime(
    profileId: ProfileId,
    topicId: String
  ) {
    val learningTimeDataProvider =
      topicLearningTimeController.retrieveAggregateTopicLearningTimeDataProvider(profileId, topicId)
  }
}
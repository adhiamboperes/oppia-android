package org.oppia.android.domain.exploration

import javax.inject.Inject
import kotlinx.coroutines.Deferred
import org.oppia.android.app.model.ProfileId
import org.oppia.android.app.model.TopicLearningTime
import org.oppia.android.app.model.TopicLearningTimeDatabase
import org.oppia.android.data.persistence.PersistentCacheStore
import org.oppia.android.domain.oppialogger.OppiaLogger
import org.oppia.android.util.data.AsyncResult
import org.oppia.android.util.data.DataProvider
import org.oppia.android.util.data.DataProviders
import org.oppia.android.util.data.DataProviders.Companion.transformAsync
import org.oppia.android.util.system.OppiaClock

private const val CACHE_NAME = "topic_learning_time_database"
private const val RECORD_AGGREGATE_LEARNING_TIME_PROVIDER_ID =
  "record_aggregate_learning_time_provider_id"
private const val RETRIEVE_AGGREGATE_LEARNING_TIME_PROVIDER_ID =
  "retrieve_aggregate_learning_time_provider_id"

/** Controller for tracking the amount of active time a user has spent in a topic. */
class TopicLearningTimeController @Inject constructor(
  private val oppiaClock: OppiaClock,
  private val cacheStoreFactory: PersistentCacheStore.Factory,
  private val dataProviders: DataProviders,
  private val oppiaLogger: OppiaLogger
) {

  /** Statuses correspond to the exceptions such that if the deferred contains an error state,
   * a corresponding exception will be passed to a failed AsyncResult.
   */
  private enum class TopicLearningTimeActionStatus {
    SUCCESS
  }

  private val cacheStoreMap =
    mutableMapOf<ProfileId, PersistentCacheStore<TopicLearningTimeDatabase>>()

  /** Begin tracking the active learning time in an exploration.
   *
   * We define the active time loosely as the time spent in an exploration when the app is in the
   * foreground.
   *
   * This method is called when the [ExplorationProgressController.beginExplorationAsync] returns a
   * success.
   *
   * @returns the system timestamp at which the timer was started.
   */
  fun startExplorationTimer(): Long {
    val startExplorationTimestamp = oppiaClock.getCurrentTimeMs()
    return startExplorationTimestamp
  }

  /** Stops tracking the active learning time in an exploration because the app is i the background,
   * or the exploration has been exited.
   *
   * This method is called when the [ExplorationProgressController.finishExplorationAsync] returns a
   * success.
   *
   * @returns the system timestamp at which the timer was paused.
   */
  fun pauseExplorationTimer(): Long {
    val pauseExplorationTimestamp = oppiaClock.getCurrentTimeMs()
    return pauseExplorationTimestamp
  }

  /**
   * Returns the play duration which is a difference between the timer end and timer start
   * timestamps.
   */
  private fun getLearningSessionDuration(): Long {
    return pauseExplorationTimer() - startExplorationTimer()
  }

  /** Returns a [TopicLearningTime] [DataProvider] for a specific topicId, per-profile basis. */
  fun retrieveAggregateTopicLearningTime(
    profileId: ProfileId,
    topicId: String
  ): DataProvider<TopicLearningTime> {
    return retrieveCacheStore(profileId)
      .transformAsync(
        RETRIEVE_AGGREGATE_LEARNING_TIME_PROVIDER_ID
      ) { learningTimeDb ->
        AsyncResult.Success(
          learningTimeDb.aggregateTopicLearningTimeMap[topicId]
            ?: TopicLearningTime.getDefaultInstance()
        )
      }
  }

  private suspend fun getDeferredResult(
    deferred: Deferred<TopicLearningTimeActionStatus>
  ): AsyncResult<Any?> {
    return when (deferred.await()) {
      TopicLearningTimeActionStatus.SUCCESS -> AsyncResult.Success(null)
    }
  }

  private fun retrieveCacheStore(
    profileId: ProfileId
  ): PersistentCacheStore<TopicLearningTimeDatabase> {
    val cacheStore = if (profileId in cacheStoreMap) {
      cacheStoreMap[profileId]!!
    } else {
      val cacheStore =
        cacheStoreFactory.createPerProfile(
          CACHE_NAME,
          TopicLearningTimeDatabase.getDefaultInstance(),
          profileId
        )
      cacheStoreMap[profileId] = cacheStore
      cacheStore
    }

    // TODO update error log
    cacheStore.primeInMemoryAndDiskCacheAsync(
      updateMode = PersistentCacheStore.UpdateMode.UPDATE_IF_NEW_CACHE,
      publishMode = PersistentCacheStore.PublishMode.PUBLISH_TO_IN_MEMORY_CACHE
    ).invokeOnCompletion {
      if (it != null) {
        oppiaLogger.e(
          "TopicLearningTimeController",
          "Failed to prime cache ahead of data retrieval for xxx.",
          it
        )
      }
    }
    return cacheStore
  }
}
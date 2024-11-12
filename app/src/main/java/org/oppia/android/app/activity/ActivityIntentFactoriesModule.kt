package org.oppia.android.app.activity

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oppia.android.app.home.recentlyplayed.RecentlyPlayedActivity
import org.oppia.android.app.topic.TopicActivity

// TODO(#59): Split this to be per-activity.

/** Module for providing [ActivityIntentFactories] factories. */
@InstallIn(SingletonComponent::class)
@Module
interface ActivityIntentFactoriesModule {
  @Binds
  fun provideTopicActivityIntentFactory(
    impl: TopicActivity.TopicActivityIntentFactoryImpl
  ): ActivityIntentFactories.TopicActivityIntentFactory

  @Binds
  fun provideRecentlyPlayedActivityIntentFactory(
    impl: RecentlyPlayedActivity.RecentlyPlayedActivityIntentFactoryImpl
  ): ActivityIntentFactories.RecentlyPlayedActivityIntentFactory
}

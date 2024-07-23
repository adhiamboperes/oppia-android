package org.oppia.android.app.profile

import androidx.annotation.PluralsRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import org.oppia.android.R
import org.oppia.android.app.fragment.FragmentScope
import org.oppia.android.app.model.Profile
import org.oppia.android.app.model.ProfileAvatar
import org.oppia.android.app.model.ProfileChooserUiModel
import org.oppia.android.app.model.ProfileId
import org.oppia.android.app.translation.AppLanguageResourceHandler
import org.oppia.android.app.viewmodel.ObservableViewModel
import org.oppia.android.domain.oppialogger.OppiaLogger
import org.oppia.android.domain.profile.ProfileManagementController
import org.oppia.android.util.data.AsyncResult
import org.oppia.android.util.data.DataProviders.Companion.toLiveData
import org.oppia.android.util.locale.OppiaLocale
import org.oppia.android.util.system.OppiaClock
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/** The ViewModel for [ProfileChooserFragment]. */
@FragmentScope
class ProfileChooserViewModel @Inject constructor(
  fragment: Fragment,
  private val oppiaLogger: OppiaLogger,
  private val profileManagementController: ProfileManagementController,
  private val machineLocale: OppiaLocale.MachineLocale,
  private val appLanguageResourceHandler: AppLanguageResourceHandler,
  private val oppiaClock: OppiaClock
) : ObservableViewModel() {

  private val routeToAdminPinListener = fragment as RouteToAdminPinListener

  val profiles: LiveData<List<ProfileChooserUiModel>> by lazy {
    Transformations.map(
      profileManagementController.getProfiles().toLiveData(), ::processGetProfilesResult
    )
  }

  lateinit var adminPin: String
  lateinit var adminProfileId: ProfileId

  val usedColors = mutableListOf<Int>()

  /** Sorts profiles alphabetically by name and put Admin in front. */
  private fun processGetProfilesResult(
    profilesResult: AsyncResult<List<Profile>>
  ): List<ProfileChooserUiModel> {
    val profileList = when (profilesResult) {
      is AsyncResult.Failure -> {
        oppiaLogger.e(
          "ProfileChooserViewModel", "Failed to retrieve the list of profiles", profilesResult.error
        )
        emptyList()
      }
      is AsyncResult.Pending -> emptyList()
      is AsyncResult.Success -> profilesResult.value
    }.map {
      ProfileChooserUiModel.newBuilder().setProfile(it).build()
    }.toMutableList()

    profileList.forEach {
      if (it.profile.avatar.avatarTypeCase == ProfileAvatar.AvatarTypeCase.AVATAR_COLOR_RGB) {
        usedColors.add(it.profile.avatar.avatarColorRgb)
      }
    }

    val sortedProfileList = profileList.sortedBy {
      machineLocale.run { it.profile.name.toMachineLowerCase() }
    }.toMutableList()

    val adminProfile = sortedProfileList.find { it.profile.isAdmin } ?: return listOf()

    sortedProfileList.remove(adminProfile)
    adminPin = adminProfile.profile.pin
    adminProfileId = adminProfile.profile.id
    sortedProfileList.add(0, adminProfile)

    if (sortedProfileList.size < 10) {
      sortedProfileList.add(ProfileChooserUiModel.newBuilder().setAddProfile(true).build())
    }

    return sortedProfileList
  }

  fun onAdministratorControlsButtonClicked() {
    routeToAdminPinListener.routeToAdminPin()
  }

  fun setProfileLastUsed(timestamp: Long): String {
    val profileLastUsed: String =
      appLanguageResourceHandler.getStringInLocale(R.string.profile_last_used)
    val timeAgoTimeStamp = getTimeAgo(timestamp)
    return appLanguageResourceHandler.getStringInLocaleWithWrapping(
      R.string.profile_last_visited,
      profileLastUsed,
      timeAgoTimeStamp
    )
  }

  private fun getTimeAgo(lastVisitedTimestamp: Long): String {
    val timeStampMillis = ensureTimestampIsInMilliseconds(lastVisitedTimestamp)
    val currentTimeMillis = oppiaClock.getCurrentTimeMs()
    val resourceHandler = appLanguageResourceHandler
    if (timeStampMillis > currentTimeMillis || timeStampMillis <= 0) {
      return resourceHandler.getStringInLocale(R.string.last_logged_in_recently)
    }
    val timeDifferenceMillis = currentTimeMillis - timeStampMillis
    when {
      timeDifferenceMillis < TimeUnit.MINUTES.toMillis(1).toInt() -> {
        return resourceHandler.getStringInLocale(R.string.just_now)
      }
      timeDifferenceMillis < TimeUnit.MINUTES.toMillis(50) -> {
        return getPluralString(
          resourceHandler,
          R.plurals.minutes_ago,
          TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis).toInt()
        )
      }
      timeDifferenceMillis < TimeUnit.DAYS.toMillis(1) -> {
        return getPluralString(
          resourceHandler,
          R.plurals.hours_ago,
          TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis).toInt()
        )
      }
      timeDifferenceMillis < TimeUnit.DAYS.toMillis(2) -> {
        return resourceHandler.getStringInLocale(R.string.yesterday)
      }
      else -> return getPluralString(
        resourceHandler,
        R.plurals.days_ago,
        TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis).toInt()
      )
    }
  }

  private fun getPluralString(
    resourceHandler: AppLanguageResourceHandler,
    @PluralsRes pluralsResId: Int,
    count: Int
  ): String {
    return resourceHandler.getQuantityStringInLocaleWithWrapping(
      pluralsResId, count, count.toString()
    )
  }

  private fun ensureTimestampIsInMilliseconds(lastVisitedTimestamp: Long): Long {
    // TODO(#3842): Investigate & remove this check.
    return if (lastVisitedTimestamp < 1000000000000L) {
      // If timestamp is given in seconds, convert that to milliseconds.
      TimeUnit.SECONDS.toMillis(lastVisitedTimestamp)
    } else lastVisitedTimestamp
  }
}

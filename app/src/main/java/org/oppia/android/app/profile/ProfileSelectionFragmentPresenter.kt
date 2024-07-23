package org.oppia.android.app.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.oppia.android.R
import org.oppia.android.app.administratorcontrols.AdministratorControlsActivity
import org.oppia.android.app.classroom.ClassroomListActivity
import org.oppia.android.app.home.HomeActivity
import org.oppia.android.app.model.Profile
import org.oppia.android.app.model.ProfileChooserUiModel
import org.oppia.android.app.model.ProfileId
import org.oppia.android.databinding.ProfileSelectionFragmentBinding
import org.oppia.android.domain.oppialogger.OppiaLogger
import org.oppia.android.domain.oppialogger.analytics.AnalyticsController
import org.oppia.android.domain.profile.ProfileManagementController
import org.oppia.android.util.data.AsyncResult
import org.oppia.android.util.data.DataProviders.Companion.toLiveData
import org.oppia.android.util.parser.image.ImageLoader
import org.oppia.android.util.platformparameter.EnableMultipleClassrooms
import org.oppia.android.util.platformparameter.PlatformParameterValue
import javax.inject.Inject

/** The presenter for [ProfileSelectionFragment]. */
class ProfileSelectionFragmentPresenter @Inject constructor(
  private val fragment: Fragment,
  private val activity: AppCompatActivity,
  private val context: Context,
  private val chooserViewModel: ProfileChooserViewModel,
  private val profileManagementController: ProfileManagementController,
  private val oppiaLogger: OppiaLogger,
  private val analyticsController: AnalyticsController,
  @EnableMultipleClassrooms private val enableMultipleClassrooms: PlatformParameterValue<Boolean>,
  private val imageLoader: ImageLoader
) {
  private lateinit var binding: ProfileSelectionFragmentBinding

  /** Creates and returns the view for the [ProfileSelectionFragment]. */
  fun handleCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
    binding = ProfileSelectionFragmentBinding.inflate(
      inflater,
      container,
      /* attachToRoot= */ false
    )
    binding.apply {
      lifecycleOwner = fragment
    }

    logProfileChooserEvent()

    chooserViewModel.profiles.observe(
      fragment,
      { profileUiModelList ->
        val profileModelList =
          profileUiModelList.filter {
            it.modelTypeCase == ProfileChooserUiModel.ModelTypeCase.PROFILE
          }
        val profilesList: MutableList<Profile> = mutableListOf()
        profileModelList.forEach {
          profilesList.add(it.profile)
        }
        bindComposeView(profilesList)
      }
    )

    return binding.root
  }

  private fun bindComposeView(profiles: List<Profile>) {
    binding.profileListView.setContent {
      MaterialTheme { ProfileSelectionScreen(profiles) }
    }
  }

  /** Display a list of profiles and provide bindings to create a new profile. */
  @Composable
  fun ProfileSelectionScreen(profiles: List<Profile>) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
      PageHeaderText()

      val profileListSpanCount = if (profiles.size == 1) {
        context.resources.getInteger(R.integer.profile_chooser_first_time_span_count)
      } else {
        context.resources.getInteger(R.integer.profile_chooser_span_count)
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.Top,
      ) {
        profilesList(
          profiles,
          columnCount = profileListSpanCount,
          modifier = Modifier
        ) { profile ->
          ProfileView(profile = profile, profileClickListener(profile))
        }
      }
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      AdministratorControlsMenu()
      AddProfileView { addProfileClickListener() }
    }
  }

  private fun LazyListScope.profilesList(
    profiles: List<Profile>,
    columnCount: Int,
    modifier: Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    itemContent: @Composable BoxScope.(Profile) -> Unit
  ) {
    val size = profiles.count()
    // Calculate the number of rows needed.
    val rows = if (size == 0) 0 else (size + columnCount - 1) / columnCount

    // Generate items in the LazyList.
    items(rows, key = { it }) { rowIndex ->
      // Create a row with the specified horizontal arrangement and padding.
      Row(
        horizontalArrangement = horizontalArrangement,
        modifier = modifier
          .padding(
            horizontal = 12.dp,
            vertical = 8.dp
          )
      ) {
        // Populate the row with columns.
        for (columnIndex in 0 until columnCount) {
          val itemIndex = rowIndex * columnCount + columnIndex
          if (itemIndex < size) {
            Box(
              modifier = Modifier.weight(1F, fill = true),
              propagateMinConstraints = true
            ) {
              itemContent(profiles[itemIndex]) // Provide content for each item.
            }
          } else {
            Spacer(Modifier.weight(1F, fill = true)) // Add spacer if no more items.
          }
        }
      }
    }
  }

  private fun profileClickListener(profile: Profile): () -> Unit = {
    updateLearnerIdIfAbsent(profile)
    if (profile.pin.isEmpty()) {
      profileManagementController.loginToProfile(profile.id).toLiveData().observe(
        fragment,
        {
          if (it is AsyncResult.Success) {
            if (enableMultipleClassrooms.value) {
              activity.startActivity(
                ClassroomListActivity.createClassroomListActivity(activity, profile.id)
              )
            } else {
              activity.startActivity(
                HomeActivity.createHomeActivity(activity, profile.id)
              )
            }
          }
        }
      )
    } else {
      val pinPasswordIntent = PinPasswordActivity.createPinPasswordActivityIntent(
        activity,
        chooserViewModel.adminPin,
        profile.id.internalId
      )
      activity.startActivity(pinPasswordIntent)
    }
  }

  private fun addProfileClickListener() {
    if (chooserViewModel.adminPin.isEmpty()) {
      activity.startActivity(
        AdminPinActivity.createAdminPinActivityIntent(
          activity,
          chooserViewModel.adminProfileId.internalId,
          selectUniqueRandomColor(),
          AdminAuthEnum.PROFILE_ADD_PROFILE.value
        )
      )
    } else {
      activity.startActivity(
        AdminAuthActivity.createAdminAuthActivityIntent(
          activity,
          chooserViewModel.adminPin,
          -1,
          selectUniqueRandomColor(),
          AdminAuthEnum.PROFILE_ADD_PROFILE.value
        )
      )
    }
  }

  @Composable
  fun PageHeaderText() {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = stringResource(R.string.profile_selection_header),
        color = colorResource(id = R.color.component_color_shared_primary_text_color),
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = dimensionResource(
          id = R.dimen.profile_selection_activity_header_text_size
        ).value.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .padding(
            start = 24.dp,
            end = 24.dp,
            top = 32.dp
          ),
      )
    }
  }

  @Composable
  fun ProfileView(profile: Profile, onClick: () -> Unit) {
    Column(
      modifier = Modifier
        .clickable { onClick() },
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      Image(
        painter = painterResource(id = R.drawable.ic_profile_icon), // todo add image if available
        contentDescription = stringResource(
          id = R.string.profile_selection_profile_icon_description
        ),
        contentScale = ContentScale.Fit,
        modifier = Modifier
          .fillMaxWidth()
          .size(dimensionResource(id = R.dimen.profile_selection_activity_profile_icon_size))
          .clip(CircleShape)
          .border(
            BorderStroke(
              2.dp,
              colorResource(R.color.component_color_profile_icon_stroke_color)
            ),
            CircleShape
          )
          .requiredSize(
            dimensionResource(id = R.dimen.profile_selection_activity_profile_icon_size)
          )
          .background(
            Color(profile.avatar.avatarColorRgb)
          )
          .padding(top = 8.dp)
          .clip(CircleShape),
        colorFilter = ColorFilter.tint(
          colorResource(id = R.color.component_color_shared_white_background_color)
        )
      )

      Text(
        text = profile.name,
        color = colorResource(id = R.color.component_color_shared_primary_text_color),
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = dimensionResource(
          id = R.dimen.profile_selection_activity_nickname_text_size
        ).value.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 8.dp)
      )

      if (profile.isAdmin) {
        Text(
          text = stringResource(id = R.string.profile_selection_admin_label),
          color = colorResource(id = R.color.component_color_shared_primary_text_color),
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.Normal,
          fontStyle = FontStyle.Italic,
          fontSize = dimensionResource(
            id = R.dimen.profile_selection_activity_role_text_size
          ).value.sp,
          textAlign = TextAlign.Center
        )
      }

      if (profile.lastLoggedInTimestampMs > 0) {
        Text(
          text = chooserViewModel.setProfileLastUsed(profile.lastLoggedInTimestampMs),
          color = colorResource(id = R.color.component_color_shared_primary_text_color),
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.Normal,
          fontStyle = FontStyle.Italic,
          fontSize = dimensionResource(
            id = R.dimen.profile_selection_activity_last_login_size
          ).value.sp,
          textAlign = TextAlign.Center
        )
      }
    }
  }

  @Composable
  fun RowScope.AdministratorControlsMenu() {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(2f, true)
        .clickable { chooserViewModel.onAdministratorControlsButtonClicked() },
      horizontalArrangement = Arrangement.Start,
      verticalAlignment = Alignment.Bottom
    ) {
      Image(
        painter = painterResource(id = R.drawable.ic_settings_grey_48dp),
        contentDescription = stringResource(id = R.string.setting_icon_content_description),
        modifier = Modifier
          .size(24.dp)
          .padding(8.dp)
          .requiredSize(25.dp)
          .align(Alignment.CenterVertically),
        colorFilter = ColorFilter.tint(
          colorResource(R.color.component_color_profile_selection_settings_icon_background_color)
        )
      )

      Text(
        text = stringResource(id = R.string.profile_chooser_administrator_controls),
        color = colorResource(id = R.color.component_color_shared_primary_text_color),
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = dimensionResource(
          id = R.dimen.profile_selection_activity_prompt_text_size
        ).value.sp,
        modifier = Modifier
          .padding(8.dp)
          .align(Alignment.CenterVertically)
      )
    }
  }

  @Composable
  fun RowScope.AddProfileView(onClick: () -> Unit) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f, false)
        .padding(4.dp),
      verticalArrangement = Arrangement.Bottom,
      horizontalAlignment = Alignment.End
    ) {
      SubcomposeLayout { constraints ->
        // Measure the button.
        val buttonPlaceable = subcompose("FloatingActionButton") {
          FloatingActionButton(
            onClick = { onClick() },
            contentColor = colorResource(
              id = R.color.component_color_shared_white_background_color
            ),
            backgroundColor = colorResource(
              id = R.color.component_color_drawer_fragment_admin_controls_selected_text_color
            ),
            modifier = Modifier
              .size(48.dp)
              .requiredSize(48.dp),
          ) {
            Icon(
              Icons.Filled.Add,
              stringResource(id = R.string.profile_selection_add_icon_description)
            )
          }
        }.first().measure(constraints)

        // Measure TextView with the width of the button.
        val textPlaceable = subcompose("TextView") {
          Text(
            text = stringResource(id = R.string.profile_selection_add_profile_text),
            color = colorResource(id = R.color.component_color_shared_primary_text_color),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = dimensionResource(
              id = R.dimen.profile_selection_activity_prompt_text_size
            ).value.sp,
            modifier = Modifier
              .width(buttonPlaceable.width.toDp())
              .padding(bottom = 8.dp)
          )
        }.first().measure(
          constraints.copy(
            minWidth = buttonPlaceable.width,
            maxWidth = buttonPlaceable.width
          )
        )

        // Set the TextView's dimensions.
        layout(
          width = constraints.maxWidth,
          height = buttonPlaceable.height + textPlaceable.height + 16.dp.roundToPx(),
        ) {
          // Draw views to the end of the column.
          val xOffset = constraints.maxWidth - buttonPlaceable.width
          buttonPlaceable.placeRelative(xOffset, 0)
          textPlaceable.placeRelative(xOffset, buttonPlaceable.height + 16.dp.roundToPx())
        }
      }
    }
  }

  /** Randomly selects a color for the new profile that is not already in use. */
  private fun selectUniqueRandomColor(): Int {
    return COLORS_LIST.map {
      ContextCompat.getColor(context, it)
    }.minus(chooserViewModel.usedColors.toSet()).random()
  }

  fun routeToAdminPin() {
    if (chooserViewModel.adminPin.isEmpty()) {
      val profileId =
        ProfileId.newBuilder().setInternalId(chooserViewModel.adminProfileId.internalId).build()
      activity.startActivity(
        AdministratorControlsActivity.createAdministratorControlsActivityIntent(
          activity,
          profileId
        )
      )
    } else {
      activity.startActivity(
        AdminAuthActivity.createAdminAuthActivityIntent(
          activity,
          chooserViewModel.adminPin,
          chooserViewModel.adminProfileId.internalId,
          selectUniqueRandomColor(),
          AdminAuthEnum.PROFILE_ADMIN_CONTROLS.value
        )
      )
    }
  }

  private fun logProfileChooserEvent() {
    analyticsController.logImportantEvent(
      oppiaLogger.createOpenProfileChooserContext(),
      profileId = null // There's no profile currently logged in.
    )
  }

  private fun updateLearnerIdIfAbsent(profile: Profile) {
    if (profile.learnerId.isNullOrEmpty()) {
      // TODO(#4345): Block on the following data provider before allowing the user to log in.
      profileManagementController.initializeLearnerId(profile.id)
    }
  }

  private val COLORS_LIST = listOf(
    R.color.component_color_avatar_background_1_color,
    R.color.component_color_avatar_background_2_color,
    R.color.component_color_avatar_background_3_color,
    R.color.component_color_avatar_background_4_color,
    R.color.component_color_avatar_background_5_color,
    R.color.component_color_avatar_background_6_color,
    R.color.component_color_avatar_background_7_color,
    R.color.component_color_avatar_background_8_color,
    R.color.component_color_avatar_background_9_color,
    R.color.component_color_avatar_background_10_color,
    R.color.component_color_avatar_background_11_color,
    R.color.component_color_avatar_background_12_color,
    R.color.component_color_avatar_background_13_color,
    R.color.component_color_avatar_background_14_color,
    R.color.component_color_avatar_background_15_color,
    R.color.component_color_avatar_background_16_color,
    R.color.component_color_avatar_background_17_color,
    R.color.component_color_avatar_background_18_color,
    R.color.component_color_avatar_background_19_color,
    R.color.component_color_avatar_background_20_color,
    R.color.component_color_avatar_background_21_color,
    R.color.component_color_avatar_background_22_color,
    R.color.component_color_avatar_background_23_color,
    R.color.component_color_avatar_background_24_color
  )
}

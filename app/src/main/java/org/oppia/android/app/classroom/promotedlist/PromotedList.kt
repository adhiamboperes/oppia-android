package org.oppia.android.app.classroom.promotedlist

import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.oppia.android.R
import org.oppia.android.app.classroom.getDrawableResource
import org.oppia.android.app.home.promotedlist.PromotedStoryListViewModel
import org.oppia.android.app.home.promotedlist.PromotedStoryViewModel
import org.oppia.android.util.locale.OppiaLocale

/** Test tag for the header of the promoted story list. */
const val PROMOTED_STORY_LIST_HEADER_TEST_TAG = "TEST_TAG.promoted_story_list_header"

/** Test tag for the promoted story list. */
const val PROMOTED_STORY_LIST_TEST_TAG = "TEST_TAG.promoted_story_list"

/** Displays a list of promoted stories. */
@Composable
fun PromotedStoryList(
  promotedStoryListViewModel: PromotedStoryListViewModel,
  machineLocale: OppiaLocale.MachineLocale,
) {
  Row(
    modifier = Modifier
      .testTag(PROMOTED_STORY_LIST_HEADER_TEST_TAG)
      .fillMaxWidth()
      .padding(top = 24.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = promotedStoryListViewModel.getHeader(),
      color = colorResource(id = R.color.component_color_shared_primary_text_color),
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Medium,
      fontSize = 18.sp,
      modifier = Modifier
        .padding(
          start = dimensionResource(id = R.dimen.promoted_story_list_layout_margin_start),
        ),
    )
    if (promotedStoryListViewModel.getViewAllButtonVisibility() == View.VISIBLE) {
      Text(
        text = machineLocale.run { stringResource(id = R.string.view_all).toMachineUpperCase() },
        color = colorResource(id = R.color.component_color_home_activity_view_all_text_color),
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        modifier = Modifier
          .fillMaxHeight()
          .align(Alignment.CenterVertically)
          .padding(
            start = 8.dp,
            end = dimensionResource(id = R.dimen.promoted_story_list_layout_margin_end),
          )
          .clickable { promotedStoryListViewModel.clickOnViewAll() },
      )
    }
  }
  LazyRow(
    modifier = Modifier
      .testTag(PROMOTED_STORY_LIST_TEST_TAG)
      .padding(top = 12.dp),
    contentPadding = PaddingValues(
      start = dimensionResource(id = R.dimen.promoted_story_list_layout_margin_start),
      end = promotedStoryListViewModel.endPadding.dp,
    ),
  ) {
    items(promotedStoryListViewModel.promotedStoryList) {
      PromotedStoryCard(
        promotedStoryViewModel = it,
        machineLocale = machineLocale
      )
    }
  }
}

/** Displays a single promoted story card with an image, title, and handling click events. */
@Composable
fun PromotedStoryCard(
  promotedStoryViewModel: PromotedStoryViewModel,
  machineLocale: OppiaLocale.MachineLocale,
) {
  val cardLayoutWidth = promotedStoryViewModel.computeLayoutWidth()
  val cardColumnModifier =
    if (cardLayoutWidth == ViewGroup.LayoutParams.MATCH_PARENT) Modifier.fillMaxWidth()
    else Modifier.width(promotedStoryViewModel.computeLayoutWidth().dp)

  Card(
    modifier = Modifier
      .width(width = dimensionResource(id = R.dimen.promoted_story_card_layout_width))
      .padding(
        start = dimensionResource(R.dimen.promoted_story_card_layout_margin_start),
        end = dimensionResource(R.dimen.promoted_story_card_layout_margin_end),
        bottom = 8.dp,
      )
      .clickable { promotedStoryViewModel.clickOnStoryTile() },
    backgroundColor = colorResource(
      id = R.color.component_color_shared_screen_primary_background_color
    ),
    elevation = 4.dp,
  ) {
    Column(
      modifier = cardColumnModifier
    ) {
      Image(
        painter = painterResource(
          id = promotedStoryViewModel.promotedStory.lessonThumbnail.getDrawableResource()
        ),
        contentDescription = promotedStoryViewModel.storyTitle,
        modifier = Modifier
          .aspectRatio(16f / 9f)
          .background(
            Color(
              (
                0xff000000L or
                  promotedStoryViewModel.promotedStory.lessonThumbnail.backgroundColorRgb.toLong()
                ).toInt()
            )
          )
      )
      Text(
        text = promotedStoryViewModel.nextChapterTitle,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
        color = colorResource(id = R.color.component_color_shared_primary_text_color),
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        textAlign = TextAlign.Start,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Text(
        text = machineLocale.run { promotedStoryViewModel.topicTitle.toMachineUpperCase() },
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
        color = colorResource(
          id = R.color.component_color_shared_story_card_topic_name_text_color
        ),
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        textAlign = TextAlign.Start,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = machineLocale.run { promotedStoryViewModel.classroomTitle.toMachineUpperCase() },
        modifier = Modifier
          .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
          .border(
            width = 2.dp,
            color = colorResource(id = R.color.color_def_persian_blue),
            shape = RoundedCornerShape(50)
          )
          .padding(horizontal = 16.dp, vertical = 6.dp),
        color = colorResource(id = R.color.color_def_persian_blue),
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        textAlign = TextAlign.Start,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}
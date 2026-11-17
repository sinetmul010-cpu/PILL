package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.LocalSeniorColors

private const val DAILY_WATER_GOAL_CUPS = 8

@Composable
fun WaterIntakeCard(
  waterCups: Int,
  onAddCup: () -> Unit,
  onRemoveCup: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val seniorColors = LocalSeniorColors.current
  val haptics = LocalHapticFeedback.current

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.secondaryContainer,
    border = BorderStroke(1.5.dp, seniorColors.waterCupBorder),
    tonalElevation = 1.dp,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      // Header row: title + giant cup count number
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = stringResource(R.string.water_section_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
          )
          AnimatedVisibility(visible = waterCups >= DAILY_WATER_GOAL_CUPS) {
            Surface(
              shape = MaterialTheme.shapes.small,
              color = seniorColors.goalBadgeBg,
            ) {
              Text(
                text = stringResource(R.string.water_goal_reached),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = seniorColors.goalBadgeText,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
              )
            }
          }
        }

        Row(
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.testTag("water_count_display"),
        ) {
          AnimatedContent(
            targetState = waterCups,
            transitionSpec = {
              (fadeIn() + scaleIn(initialScale = 0.85f)).togetherWith(fadeOut() + scaleOut())
            },
            label = "water_count_anim",
          ) { count ->
            Text(
              text = count.toString(),
              style = MaterialTheme.typography.displayLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.secondary,
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = stringResource(R.string.water_cup_unit),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(bottom = 4.dp),
          )
        }
      }

      // 8 visual water cups (2 rows of 4 so each cup stays large and clear)
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        for (rowIndex in 0..1) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            for (colIndex in 0..3) {
              val cupIndex = rowIndex * 4 + colIndex + 1
              val isFilled = cupIndex <= waterCups
              val cd =
                if (isFilled) {
                  stringResource(R.string.water_cup_cd_filled, cupIndex)
                } else {
                  stringResource(R.string.water_cup_cd_empty, cupIndex)
                }
              WaterCupIcon(isFilled = isFilled, description = cd)
            }
          }
        }
      }

      // Main big "+1 cup" button
      Button(
        onClick = {
          haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
          onAddCup()
        },
        modifier =
          Modifier.fillMaxWidth()
            .heightIn(min = 76.dp)
            .minimumInteractiveComponentSize()
            .testTag("water_add_button"),
        shape = MaterialTheme.shapes.medium,
        colors =
          ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
          ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.WaterDrop,
          contentDescription = null,
          modifier = Modifier.size(30.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = stringResource(R.string.water_add_button),
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
        )
      }

      // Undo (-1 cup) button when count > 0
      AnimatedVisibility(visible = waterCups > 0) {
        OutlinedButton(
          onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onRemoveCup()
          },
          modifier =
            Modifier.fillMaxWidth()
              .heightIn(min = 56.dp)
              .minimumInteractiveComponentSize()
              .testTag("water_undo_button"),
          shape = MaterialTheme.shapes.medium,
          border = BorderStroke(1.5.dp, seniorColors.waterCupBorder),
          colors =
            ButtonDefaults.outlinedButtonColors(
              containerColor = MaterialTheme.colorScheme.surface,
              contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
        ) {
          Icon(
            imageVector = Icons.Rounded.Remove,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(R.string.water_undo_button),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
          )
        }
      }
    }
  }
}

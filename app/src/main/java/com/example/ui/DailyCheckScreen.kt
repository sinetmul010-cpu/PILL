package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.MedicationButton
import com.example.ui.components.WaterIntakeCard
import com.example.ui.theme.LocalSeniorColors

@Composable
fun DailyCheckScreen(
  uiState: DailyCheckUiState,
  onToggleMorning: () -> Unit,
  onToggleLunch: () -> Unit,
  onToggleDinner: () -> Unit,
  onAddWaterCup: () -> Unit,
  onRemoveWaterCup: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets.safeDrawing,
  ) { innerPadding ->
    when (uiState) {
      is DailyCheckUiState.Loading -> {
        Box(
          modifier = Modifier.fillMaxSize().padding(innerPadding),
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(56.dp),
            strokeWidth = 5.dp,
          )
        }
      }

      is DailyCheckUiState.Success -> {
        BoxWithConstraints(
          modifier =
            Modifier.fillMaxSize()
              .padding(innerPadding)
              .verticalScroll(rememberScrollState())
              .padding(horizontal = 20.dp, vertical = 20.dp),
          contentAlignment = Alignment.TopCenter,
        ) {
          val isExpanded = maxWidth >= 720.dp

          if (isExpanded) {
            Column(
              modifier = Modifier.widthIn(max = 1040.dp).fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
              DateHeaderCard(
                formattedDate = uiState.formattedDate,
                dayOfWeek = uiState.dayOfWeek,
                takenCount = uiState.takenCount,
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.Top,
              ) {
                MedicationListSection(
                  morningTaken = uiState.morningTaken,
                  lunchTaken = uiState.lunchTaken,
                  dinnerTaken = uiState.dinnerTaken,
                  onToggleMorning = onToggleMorning,
                  onToggleLunch = onToggleLunch,
                  onToggleDinner = onToggleDinner,
                  modifier = Modifier.weight(1f),
                )

                WaterIntakeCard(
                  waterCups = uiState.waterCups,
                  onAddCup = onAddWaterCup,
                  onRemoveCup = onRemoveWaterCup,
                  modifier = Modifier.weight(1f),
                )
              }
            }
          } else {
            Column(
              modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
              DateHeaderCard(
                formattedDate = uiState.formattedDate,
                dayOfWeek = uiState.dayOfWeek,
                takenCount = uiState.takenCount,
              )

              MedicationListSection(
                morningTaken = uiState.morningTaken,
                lunchTaken = uiState.lunchTaken,
                dinnerTaken = uiState.dinnerTaken,
                onToggleMorning = onToggleMorning,
                onToggleLunch = onToggleLunch,
                onToggleDinner = onToggleDinner,
              )

              WaterIntakeCard(
                waterCups = uiState.waterCups,
                onAddCup = onAddWaterCup,
                onRemoveCup = onRemoveWaterCup,
              )

              Spacer(modifier = Modifier.height(8.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun DateHeaderCard(
  formattedDate: String,
  dayOfWeek: String,
  takenCount: Int,
  modifier: Modifier = Modifier,
) {
  val seniorColors = LocalSeniorColors.current
  val allMedTaken = takenCount == 3

  Surface(
    modifier = modifier.fillMaxWidth().testTag("date_header_card"),
    shape = MaterialTheme.shapes.large,
    color = seniorColors.headerBackground,
    border = BorderStroke(1.5.dp, seniorColors.cardBorder),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.CalendarToday,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(24.dp),
        )
        Text(
          text = stringResource(R.string.today_label),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Text(
        text = "$formattedDate ($dayOfWeek)",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.testTag("today_date_text"),
      )

      Surface(
        shape = MaterialTheme.shapes.small,
        color =
          if (allMedTaken) seniorColors.checkedGreenTint else MaterialTheme.colorScheme.surface,
        border =
          BorderStroke(
            width = 1.dp,
            color = if (allMedTaken) seniorColors.checkedGreen else seniorColors.cardBorder,
          ),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          if (allMedTaken) {
            Icon(
              imageVector = Icons.Rounded.CheckCircle,
              contentDescription = null,
              tint = seniorColors.checkedGreen,
              modifier = Modifier.size(26.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
          }
          Text(
            text =
              if (allMedTaken) {
                stringResource(R.string.med_all_done)
              } else {
                stringResource(R.string.med_summary_format, takenCount)
              },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color =
              if (allMedTaken) seniorColors.checkedGreenDarkText
              else MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    }
  }
}

@Composable
private fun MedicationListSection(
  morningTaken: Boolean,
  lunchTaken: Boolean,
  dinnerTaken: Boolean,
  onToggleMorning: () -> Unit,
  onToggleLunch: () -> Unit,
  onToggleDinner: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val seniorColors = LocalSeniorColors.current

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(18.dp),
  ) {
    Text(
      text = stringResource(R.string.medication_section_title),
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.padding(horizontal = 4.dp),
    )

    MedicationButton(
      title = stringResource(R.string.med_morning),
      icon = Icons.Rounded.WbTwilight,
      uncheckedIconTint = seniorColors.morningIconTint,
      uncheckedIconBg = seniorColors.morningIconBg,
      isTaken = morningTaken,
      onClick = onToggleMorning,
      testTag = "med_morning_button",
    )

    MedicationButton(
      title = stringResource(R.string.med_lunch),
      icon = Icons.Rounded.WbSunny,
      uncheckedIconTint = seniorColors.lunchIconTint,
      uncheckedIconBg = seniorColors.lunchIconBg,
      isTaken = lunchTaken,
      onClick = onToggleLunch,
      testTag = "med_lunch_button",
    )

    MedicationButton(
      title = stringResource(R.string.med_dinner),
      icon = Icons.Rounded.NightsStay,
      uncheckedIconTint = seniorColors.dinnerIconTint,
      uncheckedIconBg = seniorColors.dinnerIconBg,
      isTaken = dinnerTaken,
      onClick = onToggleDinner,
      testTag = "med_dinner_button",
    )
  }
}

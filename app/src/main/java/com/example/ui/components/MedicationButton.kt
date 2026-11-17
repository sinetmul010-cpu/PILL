package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.LocalSeniorColors

@Composable
fun MedicationButton(
  title: String,
  icon: ImageVector,
  uncheckedIconTint: Color,
  uncheckedIconBg: Color,
  isTaken: Boolean,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier,
) {
  val seniorColors = LocalSeniorColors.current
  val haptics = LocalHapticFeedback.current

  val containerColor by
    animateColorAsState(
      targetValue = if (isTaken) seniorColors.checkedGreen else MaterialTheme.colorScheme.surface,
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "med_container_color",
    )

  val borderColor by
    animateColorAsState(
      targetValue = if (isTaken) seniorColors.checkedGreen else seniorColors.cardBorder,
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "med_border_color",
    )

  val titleColor by
    animateColorAsState(
      targetValue = if (isTaken) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "med_title_color",
    )

  val subtitleColor by
    animateColorAsState(
      targetValue =
        if (isTaken) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "med_subtitle_color",
    )

  val statusText =
    if (isTaken) stringResource(R.string.status_taken) else stringResource(R.string.status_not_taken)

  Surface(
    onClick = {
      haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
      onClick()
    },
    modifier =
      modifier
        .fillMaxWidth()
        .heightIn(min = 104.dp)
        .minimumInteractiveComponentSize()
        .testTag(testTag)
        .semantics {
          role = Role.Checkbox
          stateDescription = "$title $statusText"
        },
    shape = MaterialTheme.shapes.large,
    color = containerColor,
    border = BorderStroke(width = if (isTaken) 2.5.dp else 1.5.dp, color = borderColor),
    tonalElevation = if (isTaken) 4.dp else 1.dp,
    shadowElevation = if (isTaken) 4.dp else 1.dp,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f),
      ) {
        Box(
          modifier =
            Modifier.size(58.dp)
              .clip(CircleShape)
              .background(
                if (isTaken) MaterialTheme.colorScheme.onPrimary else uncheckedIconBg
              ),
          contentAlignment = Alignment.Center,
        ) {
          AnimatedContent(
            targetState = isTaken,
            transitionSpec = {
              (fadeIn() + scaleIn(initialScale = 0.8f)).togetherWith(fadeOut())
            },
            label = "med_leading_icon",
          ) { checked ->
            Icon(
              imageVector = if (checked) Icons.Rounded.Check else icon,
              contentDescription = null,
              tint = if (checked) seniorColors.checkedGreen else uncheckedIconTint,
              modifier = Modifier.size(34.dp),
            )
          }
        }

        Spacer(modifier = Modifier.width(18.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = titleColor,
          )
          Text(
            text = statusText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isTaken) FontWeight.Bold else FontWeight.Medium,
            color = subtitleColor,
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      if (isTaken) {
        Icon(
          imageVector = Icons.Rounded.CheckCircle,
          contentDescription = stringResource(R.string.status_taken),
          tint = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.size(40.dp),
        )
      } else {
        Surface(
          modifier = Modifier.size(38.dp),
          shape = CircleShape,
          color = Color.Transparent,
          border = BorderStroke(2.5.dp, seniorColors.cardBorder),
        ) {}
      }
    }
  }
}

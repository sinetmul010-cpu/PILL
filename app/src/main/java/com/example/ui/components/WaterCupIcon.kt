package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalSeniorColors

@Composable
fun WaterCupIcon(
  isFilled: Boolean,
  description: String,
  modifier: Modifier = Modifier,
) {
  val seniorColors = LocalSeniorColors.current
  val waterFillColor = seniorColors.waterFill
  val waterSurfaceColor = MaterialTheme.colorScheme.secondary
  val emptyCupColor = seniorColors.waterCupEmpty
  val borderColor =
    if (isFilled) MaterialTheme.colorScheme.secondary else seniorColors.waterCupBorder

  val animatedFill by
    animateFloatAsState(
      targetValue = if (isFilled) 0.78f else 0f,
      animationSpec =
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
      label = "water_cup_fill",
    )

  Canvas(
    modifier =
      modifier.size(width = 44.dp, height = 52.dp).semantics { contentDescription = description }
  ) {
    val w = size.width
    val h = size.height
    val topInset = w * 0.08f
    val bottomInset = w * 0.20f
    val padY = h * 0.08f
    val bottomY = h - padY

    val cupPath =
      Path().apply {
        moveTo(topInset, padY)
        lineTo(w - topInset, padY)
        lineTo(w - bottomInset, bottomY)
        lineTo(bottomInset, bottomY)
        close()
      }

    // Glass background
    drawPath(path = cupPath, color = emptyCupColor)

    // Animated water inside cup
    if (animatedFill > 0.01f) {
      clipPath(cupPath) {
        val cupHeight = bottomY - padY
        val waterTopY = bottomY - (cupHeight * animatedFill)
        drawRect(
          color = waterFillColor,
          topLeft = Offset(0f, waterTopY),
          size = androidx.compose.ui.geometry.Size(w, h - waterTopY),
        )
        // Top surface highlight line
        drawLine(
          color = waterSurfaceColor,
          start = Offset(0f, waterTopY),
          end = Offset(w, waterTopY),
          strokeWidth = 3.dp.toPx(),
        )
        // Subtle water shine bubble
        drawCircle(
          color = Color.White.copy(alpha = 0.55f),
          radius = w * 0.075f,
          center = Offset(w * 0.36f, waterTopY + (bottomY - waterTopY) * 0.38f),
        )
      }
    }

    // Glass outline
    drawPath(
      path = cupPath,
      color = borderColor,
      style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
    )

    // Cup rim highlight
    drawLine(
      color = borderColor,
      start = Offset(topInset * 0.6f, padY),
      end = Offset(w - topInset * 0.6f, padY),
      strokeWidth = 3.dp.toPx(),
      cap = StrokeCap.Round,
    )
  }
}

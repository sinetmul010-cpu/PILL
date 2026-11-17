package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class SeniorSemanticColors(
  val headerBackground: Color = WarmLinenSurface,
  val cardBorder: Color = WarmBorder,
  val checkedGreen: Color = CheckedGreen,
  val checkedGreenTint: Color = CheckedGreenTint,
  val checkedGreenDarkText: Color = CheckedGreenDarkText,
  val waterFill: Color = WaterBlueLight,
  val waterCupBorder: Color = WaterCupBorder,
  val waterCupEmpty: Color = WaterCupEmpty,
  val morningIconTint: Color = WarmAmber,
  val morningIconBg: Color = WarmAmberTint,
  val lunchIconTint: Color = CheckedGreen,
  val lunchIconBg: Color = CheckedGreenTint,
  val dinnerIconTint: Color = EveningIndigo,
  val dinnerIconBg: Color = EveningIndigoTint,
  val goalBadgeBg: Color = WarmAmberTint,
  val goalBadgeText: Color = WarmAmberDarkText,
)

val LocalSeniorColors = staticCompositionLocalOf { SeniorSemanticColors() }

private val SeniorIvoryColorScheme =
  lightColorScheme(
    primary = CheckedGreen,
    onPrimary = Color.White,
    primaryContainer = CheckedGreenTint,
    onPrimaryContainer = CheckedGreenDarkText,
    secondary = WaterBlue,
    onSecondary = Color.White,
    secondaryContainer = WaterCardTint,
    onSecondaryContainer = WaterBlueDarkText,
    tertiary = WarmAmber,
    onTertiary = Color.White,
    tertiaryContainer = WarmAmberTint,
    onTertiaryContainer = WarmAmberDarkText,
    background = IvoryBackground,
    onBackground = WarmInk,
    surface = CreamSurface,
    onSurface = WarmInk,
    surfaceVariant = WarmLinenSurface,
    onSurfaceVariant = WarmEspresso,
    outline = WarmBorder,
    error = ErrorSoftRed,
    onError = Color.White,
    errorContainer = ErrorSoftTint,
    onErrorContainer = ErrorSoftRed,
  )

val SeniorShapes =
  Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp),
  )

@Composable
fun SeniorWellnessTheme(content: @Composable () -> Unit) {
  CompositionLocalProvider(LocalSeniorColors provides SeniorSemanticColors()) {
    MaterialTheme(
      colorScheme = SeniorIvoryColorScheme,
      typography = SeniorTypography,
      shapes = SeniorShapes,
      content = content,
    )
  }
}

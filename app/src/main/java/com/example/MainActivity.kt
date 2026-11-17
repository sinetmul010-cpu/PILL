package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppDatabase
import com.example.data.DailyCheckRepository
import com.example.ui.DailyCheckScreen
import com.example.ui.DailyCheckViewModel
import com.example.ui.theme.IvoryBackground
import com.example.ui.theme.SeniorWellnessTheme

class MainActivity : ComponentActivity() {

  private val viewModel: DailyCheckViewModel by viewModels {
    val dao = AppDatabase.getInstance(applicationContext).dailyCheckDao()
    DailyCheckViewModel.Factory(DailyCheckRepository(dao))
  }

  private val dateChangeReceiver =
    object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        viewModel.refreshDateIfNeeded()
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(IvoryBackground.toArgb(), IvoryBackground.toArgb()),
      navigationBarStyle = SystemBarStyle.light(IvoryBackground.toArgb(), IvoryBackground.toArgb()),
    )
    setContent {
      SeniorWellnessTheme {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        DailyCheckScreen(
          uiState = uiState,
          onToggleMorning = viewModel::toggleMorning,
          onToggleLunch = viewModel::toggleLunch,
          onToggleDinner = viewModel::toggleDinner,
          onAddWaterCup = viewModel::incrementWater,
          onRemoveWaterCup = viewModel::decrementWater,
        )
      }
    }
  }

  override fun onStart() {
    super.onStart()
    val filter =
      IntentFilter().apply {
        addAction(Intent.ACTION_DATE_CHANGED)
        addAction(Intent.ACTION_TIME_CHANGED)
        addAction(Intent.ACTION_TIMEZONE_CHANGED)
      }
    ContextCompat.registerReceiver(
      this,
      dateChangeReceiver,
      filter,
      ContextCompat.RECEIVER_NOT_EXPORTED,
    )
  }

  override fun onResume() {
    super.onResume()
    viewModel.refreshDateIfNeeded()
  }

  override fun onStop() {
    super.onStop()
    unregisterReceiver(dateChangeReceiver)
  }
}

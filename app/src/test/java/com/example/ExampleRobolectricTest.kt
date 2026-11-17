package com.example

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.DailyCheckRepository
import com.example.ui.DailyCheckScreen
import com.example.ui.DailyCheckUiState
import com.example.ui.DailyCheckViewModel
import com.example.ui.theme.SeniorWellnessTheme
import java.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var database: AppDatabase
  private lateinit var repository: DailyCheckRepository

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database =
      Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        .allowMainThreadQueries()
        .setQueryExecutor { it.run() }
        .setTransactionExecutor { it.run() }
        .build()
    repository = DailyCheckRepository(database.dailyCheckDao())
  }

  @After
  fun teardown() {
    database.close()
  }

  @Test
  fun `app name matches Korean title`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("오늘 약과 물", appName)
  }

  @Test
  fun `room repository persists daily medication and water counts`() = runTest {
    val dateKey = "2026-11-16"

    repository.toggleMorning(dateKey)
    repository.toggleDinner(dateKey)
    repository.incrementWater(dateKey)
    repository.incrementWater(dateKey)

    val saved = repository.observeDailyCheck(dateKey).first()
    assertTrue(saved.morningTaken)
    assertFalse(saved.lunchTaken)
    assertTrue(saved.dinnerTaken)
    assertEquals(2, saved.waterCups)

    repository.decrementWater(dateKey)
    val afterUndo = repository.observeDailyCheck(dateKey).first()
    assertEquals(1, afterUndo.waterCups)
  }

  @Test
  fun `clearPreviousDays deletes yesterday record after midnight`() = runTest {
    val yesterday = "2026-11-16"
    val today = "2026-11-17"

    repository.toggleMorning(yesterday)
    repository.incrementWater(yesterday)
    repository.incrementWater(yesterday)

    repository.clearPreviousDays(today)

    assertNull(database.dailyCheckDao().getByDate(yesterday))
    val todayState = repository.observeDailyCheck(today).first()
    assertFalse(todayState.morningTaken)
    assertFalse(todayState.lunchTaken)
    assertFalse(todayState.dinnerTaken)
    assertEquals(0, todayState.waterCups)
  }

  @Test
  fun `viewModel automatically resets medication and water checks when midnight passes`() = runTest {
    val testDispatcher = UnconfinedTestDispatcher(testScheduler)
    Dispatchers.setMain(testDispatcher)
    val store = ViewModelStore()
    try {
      val cal =
        Calendar.getInstance().apply {
          set(2026, Calendar.NOVEMBER, 16, 23, 59, 55)
          set(Calendar.MILLISECOND, 0)
        }
      var simulatedNowMillis = cal.timeInMillis

      val owner =
        object : ViewModelStoreOwner {
          override val viewModelStore: ViewModelStore = store
        }
      val viewModel =
        androidx.lifecycle.ViewModelProvider(
          owner,
          object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
              return DailyCheckViewModel(
                repository = repository,
                nowMillisProvider = { simulatedNowMillis },
              ) as T
            }
          },
        )[DailyCheckViewModel::class.java]

      val collectJob = backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }

      viewModel.toggleMorning()
      viewModel.toggleLunch()
      viewModel.incrementWater()
      viewModel.incrementWater()
      viewModel.incrementWater()

      val beforeMidnight = viewModel.uiState.value as DailyCheckUiState.Success
      assertEquals("2026-11-16", beforeMidnight.dateKey)
      assertTrue(beforeMidnight.morningTaken)
      assertTrue(beforeMidnight.lunchTaken)
      assertEquals(3, beforeMidnight.waterCups)

      // Advance clock past midnight (23:59:55 -> 00:00:01 on 2026-11-17)
      simulatedNowMillis += 6_000L
      advanceTimeBy(6_000L)

      val afterMidnight = viewModel.uiState.value as DailyCheckUiState.Success
      assertEquals("2026-11-17", afterMidnight.dateKey)
      assertFalse(afterMidnight.morningTaken)
      assertFalse(afterMidnight.lunchTaken)
      assertFalse(afterMidnight.dinnerTaken)
      assertEquals(0, afterMidnight.waterCups)
      assertNull(database.dailyCheckDao().getByDate("2026-11-16"))

      collectJob.cancel()
    } finally {
      store.clear()
      Dispatchers.resetMain()
    }
  }

  @Test
  fun `daily check screen toggles medication status and water cups`() {
    var state by
      mutableStateOf(
        DailyCheckUiState.Success(
          dateKey = "2026-11-16",
          formattedDate = "2026년 11월 16일",
          dayOfWeek = "월요일",
          morningTaken = false,
          lunchTaken = false,
          dinnerTaken = false,
          waterCups = 0,
        )
      )

    composeTestRule.setContent {
      SeniorWellnessTheme {
        DailyCheckScreen(
          uiState = state,
          onToggleMorning = { state = state.copy(morningTaken = !state.morningTaken) },
          onToggleLunch = { state = state.copy(lunchTaken = !state.lunchTaken) },
          onToggleDinner = { state = state.copy(dinnerTaken = !state.dinnerTaken) },
          onAddWaterCup = { state = state.copy(waterCups = state.waterCups + 1) },
          onRemoveWaterCup = { state = state.copy(waterCups = (state.waterCups - 1).coerceAtLeast(0)) },
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("2026년 11월 16일 (월요일)").assertIsDisplayed()
    composeTestRule.onNodeWithTag("med_morning_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("복용 완료").assertIsDisplayed()

    composeTestRule.onNodeWithTag("water_add_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("water_count_display").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("1").assertExists()
  }
}

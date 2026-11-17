package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DailyCheckRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DailyCheckUiState {
  data object Loading : DailyCheckUiState

  data class Success(
    val dateKey: String,
    val formattedDate: String,
    val dayOfWeek: String,
    val morningTaken: Boolean,
    val lunchTaken: Boolean,
    val dinnerTaken: Boolean,
    val waterCups: Int,
  ) : DailyCheckUiState {
    val takenCount: Int
      get() = listOf(morningTaken, lunchTaken, dinnerTaken).count { it }
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
class DailyCheckViewModel(
  private val repository: DailyCheckRepository,
  private val nowMillisProvider: () -> Long = { System.currentTimeMillis() },
) : ViewModel() {

  private val manualRefreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

  private val midnightTickerFlow: Flow<String> = flow {
    while (true) {
      val nowMillis = nowMillisProvider()
      emit(formatDateKey(nowMillis))
      delay(millisUntilNextMidnight(nowMillis))
    }
  }

  private val manualDateFlow: Flow<String> =
    manualRefreshTrigger.map { formatDateKey(nowMillisProvider()) }

  private val activeDateKeyFlow: Flow<String> =
    merge(midnightTickerFlow, manualDateFlow)
      .distinctUntilChanged()
      .onEach { todayKey -> repository.clearPreviousDays(todayKey) }

  val uiState: StateFlow<DailyCheckUiState> =
    activeDateKeyFlow
      .flatMapLatest { dateKey ->
        repository.observeDailyCheck(dateKey).map { record ->
          val displayDate = Date(nowMillisProvider())
          DailyCheckUiState.Success(
            dateKey = record.dateKey,
            formattedDate = formatKoreanDate(displayDate),
            dayOfWeek = formatKoreanDay(displayDate),
            morningTaken = record.morningTaken,
            lunchTaken = record.lunchTaken,
            dinnerTaken = record.dinnerTaken,
            waterCups = record.waterCups,
          )
        }
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailyCheckUiState.Loading,
      )

  fun refreshDateIfNeeded() {
    manualRefreshTrigger.tryEmit(Unit)
  }

  fun toggleMorning() {
    val key = currentActiveDateKey()
    viewModelScope.launch {
      repository.clearPreviousDays(key)
      repository.toggleMorning(key)
    }
  }

  fun toggleLunch() {
    val key = currentActiveDateKey()
    viewModelScope.launch {
      repository.clearPreviousDays(key)
      repository.toggleLunch(key)
    }
  }

  fun toggleDinner() {
    val key = currentActiveDateKey()
    viewModelScope.launch {
      repository.clearPreviousDays(key)
      repository.toggleDinner(key)
    }
  }

  fun incrementWater() {
    val key = currentActiveDateKey()
    viewModelScope.launch {
      repository.clearPreviousDays(key)
      repository.incrementWater(key)
    }
  }

  fun decrementWater() {
    val key = currentActiveDateKey()
    viewModelScope.launch {
      repository.clearPreviousDays(key)
      repository.decrementWater(key)
    }
  }

  private fun currentActiveDateKey(): String = formatDateKey(nowMillisProvider())

  private fun formatDateKey(millis: Long): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(millis))
  }

  private fun formatKoreanDate(date: Date): String {
    return SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA).format(date)
  }

  private fun formatKoreanDay(date: Date): String {
    return SimpleDateFormat("EEEE", Locale.KOREA).format(date)
  }

  companion object {
    internal fun millisUntilNextMidnight(nowMillis: Long): Long {
      val nextMidnight =
        Calendar.getInstance().apply {
          timeInMillis = nowMillis
          add(Calendar.DAY_OF_YEAR, 1)
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 100)
        }
      return (nextMidnight.timeInMillis - nowMillis).coerceAtLeast(1_000L)
    }
  }

  class Factory(private val repository: DailyCheckRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(DailyCheckViewModel::class.java)) {
        return DailyCheckViewModel(repository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
  }
}

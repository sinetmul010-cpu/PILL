package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DailyCheckRepository(private val dao: DailyCheckDao) {
  fun observeDailyCheck(dateKey: String): Flow<DailyCheckEntity> {
    return dao.observeByDate(dateKey).map { it ?: DailyCheckEntity(dateKey = dateKey) }
  }

  suspend fun toggleMorning(dateKey: String) {
    dao.toggleMorning(dateKey)
  }

  suspend fun toggleLunch(dateKey: String) {
    dao.toggleLunch(dateKey)
  }

  suspend fun toggleDinner(dateKey: String) {
    dao.toggleDinner(dateKey)
  }

  suspend fun incrementWater(dateKey: String) {
    dao.incrementWater(dateKey)
  }

  suspend fun decrementWater(dateKey: String) {
    dao.decrementWater(dateKey)
  }

  suspend fun clearPreviousDays(todayKey: String) {
    dao.deleteRecordsBefore(todayKey)
  }
}

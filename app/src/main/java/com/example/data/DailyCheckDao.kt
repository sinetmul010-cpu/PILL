package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCheckDao {
  @Query("SELECT * FROM daily_checks WHERE dateKey = :dateKey LIMIT 1")
  fun observeByDate(dateKey: String): Flow<DailyCheckEntity?>

  @Query("SELECT * FROM daily_checks WHERE dateKey = :dateKey LIMIT 1")
  suspend fun getByDate(dateKey: String): DailyCheckEntity?

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertIfAbsent(entity: DailyCheckEntity)

  @Query(
    "UPDATE daily_checks SET morningTaken = NOT morningTaken, updatedAtMillis = :now WHERE dateKey = :dateKey"
  )
  suspend fun toggleMorningInternal(dateKey: String, now: Long)

  @Query(
    "UPDATE daily_checks SET lunchTaken = NOT lunchTaken, updatedAtMillis = :now WHERE dateKey = :dateKey"
  )
  suspend fun toggleLunchInternal(dateKey: String, now: Long)

  @Query(
    "UPDATE daily_checks SET dinnerTaken = NOT dinnerTaken, updatedAtMillis = :now WHERE dateKey = :dateKey"
  )
  suspend fun toggleDinnerInternal(dateKey: String, now: Long)

  @Query(
    "UPDATE daily_checks SET waterCups = waterCups + 1, updatedAtMillis = :now WHERE dateKey = :dateKey"
  )
  suspend fun incrementWaterInternal(dateKey: String, now: Long)

  @Query(
    "UPDATE daily_checks SET waterCups = CASE WHEN waterCups > 0 THEN waterCups - 1 ELSE 0 END, updatedAtMillis = :now WHERE dateKey = :dateKey"
  )
  suspend fun decrementWaterInternal(dateKey: String, now: Long)

  @Query("DELETE FROM daily_checks WHERE dateKey < :todayKey")
  suspend fun deleteRecordsBefore(todayKey: String)

  @Transaction
  suspend fun toggleMorning(dateKey: String, now: Long = System.currentTimeMillis()) {
    insertIfAbsent(DailyCheckEntity(dateKey = dateKey, updatedAtMillis = now))
    toggleMorningInternal(dateKey, now)
  }

  @Transaction
  suspend fun toggleLunch(dateKey: String, now: Long = System.currentTimeMillis()) {
    insertIfAbsent(DailyCheckEntity(dateKey = dateKey, updatedAtMillis = now))
    toggleLunchInternal(dateKey, now)
  }

  @Transaction
  suspend fun toggleDinner(dateKey: String, now: Long = System.currentTimeMillis()) {
    insertIfAbsent(DailyCheckEntity(dateKey = dateKey, updatedAtMillis = now))
    toggleDinnerInternal(dateKey, now)
  }

  @Transaction
  suspend fun incrementWater(dateKey: String, now: Long = System.currentTimeMillis()) {
    insertIfAbsent(DailyCheckEntity(dateKey = dateKey, updatedAtMillis = now))
    incrementWaterInternal(dateKey, now)
  }

  @Transaction
  suspend fun decrementWater(dateKey: String, now: Long = System.currentTimeMillis()) {
    insertIfAbsent(DailyCheckEntity(dateKey = dateKey, updatedAtMillis = now))
    decrementWaterInternal(dateKey, now)
  }
}

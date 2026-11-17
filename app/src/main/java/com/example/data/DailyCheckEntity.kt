package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_checks")
data class DailyCheckEntity(
  @PrimaryKey val dateKey: String,
  val morningTaken: Boolean = false,
  val lunchTaken: Boolean = false,
  val dinnerTaken: Boolean = false,
  val waterCups: Int = 0,
  val updatedAtMillis: Long = System.currentTimeMillis(),
)

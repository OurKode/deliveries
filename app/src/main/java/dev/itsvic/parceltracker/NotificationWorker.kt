// SPDX-License-Identifier: GPL-3.0-or-later
package dev.itsvic.parceltracker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dev.itsvic.parceltracker.api.ParcelHistoryItem
import dev.itsvic.parceltracker.api.getParcel
import dev.itsvic.parceltracker.db.ParcelStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NotificationWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
  override suspend fun doWork(): Result {
    val db = ParcelApplication.db
    val parcelDao = db.parcelDao()
    val statusDao = db.parcelStatusDao()
    val zone = ZoneId.systemDefault()
    Log.d("NotificationWorker", "I ran!")

    withContext(Dispatchers.IO) {
      val parcels = parcelDao.getAllNonArchivedWithStatusAsync()
      Log.d("NotificationWorker", "Got parcels count: ${parcels.size}")

      for (parcelWithStatus in parcels) {
        val parcel = parcelWithStatus.parcel
        val oldStatus = parcelWithStatus.status

        try {
          Log.d(
              "NotificationWorker",
              "Fetching parcel status for DB ID: ${parcel.id}, service: ${parcel.service}")
          val apiParcel =
              try {
                applicationContext.getParcel(parcel.parcelId, parcel.postalCode, parcel.service)
              } catch (e: Exception) {
                Log.d("NotificationWorker", "Failed to fetch, skipping", e)
                continue
              }

          val latestHistoryItem = apiParcel.history.firstOrNull()
          val lastChange =
              latestHistoryItem?.time?.atZone(zone)?.toInstant()
                  ?: oldStatus?.lastChange
                  ?: Instant.now()

          when {
            oldStatus == null -> {
              Log.d("NotificationWorker", "Parcel did not have a status before, will only add one.")
              statusDao.insert(ParcelStatus(parcel.id, apiParcel.currentStatus, lastChange))
            }

            oldStatus.status != apiParcel.currentStatus ||
                (latestHistoryItem != null && oldStatus.lastChange != lastChange) -> {
              Log.d("NotificationWorker", "Parcel has had updates since then, push a notification!")
              val event =
                  latestHistoryItem
                      ?: ParcelHistoryItem(
                          description =
                              apiParcel.description
                                  ?: applicationContext.getString(apiParcel.currentStatus.nameResource),
                          time = LocalDateTime.now(),
                          location = "")
              applicationContext.sendNotification(parcel, apiParcel.currentStatus, event)
              statusDao.update(ParcelStatus(parcel.id, apiParcel.currentStatus, lastChange))
            }

            else -> Log.d("NotificationWorker", "Parcel has not had any updates yet.")
          }
        } catch (e: Exception) {
          Log.e("NotificationWorker", "Error processing parcel ${parcel.id}", e)
        }
      }
    }

    return Result.success()
  }
}

private const val WORK_NAME = "ParcelTrackerNotificationWorker"

suspend fun Context.enqueueNotificationWorker(
    policy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE
) {
  val unmeteredOnly = this.dataStore.data.map { it[UNMETERED_ONLY] ?: false }.first()
  val intervalMinutes =
      this.dataStore.data.map { it[SYNC_INTERVAL_MINUTES] ?: 60L }.first().coerceAtLeast(15L)

  val constraints =
      Constraints.Builder()
          .setRequiredNetworkType(
              if (unmeteredOnly) NetworkType.UNMETERED else NetworkType.CONNECTED)
          .build()

  val request =
      PeriodicWorkRequestBuilder<NotificationWorker>(intervalMinutes, TimeUnit.MINUTES)
          .setConstraints(constraints)
          .build()

  WorkManager.getInstance(this)
      .enqueueUniquePeriodicWork(
          WORK_NAME, policy, request)
}

suspend fun Context.enqueueWorkerIfNotQueued() {
  this.enqueueNotificationWorker(ExistingPeriodicWorkPolicy.KEEP)
}

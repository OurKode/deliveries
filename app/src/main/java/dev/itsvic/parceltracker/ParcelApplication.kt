// SPDX-License-Identifier: GPL-3.0-or-later
package dev.itsvic.parceltracker

import android.app.Application
import android.content.Context
import androidx.room.Room
import dev.itsvic.parceltracker.db.AppDatabase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ParcelApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    appContext = applicationContext
    db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "parcel-tracker").build()

    applicationContext.createNotificationChannel()
    MainScope().launch { applicationContext.enqueueWorkerIfNotQueued() }
  }

  companion object {
    lateinit var db: AppDatabase
    lateinit var appContext: Context
  }
}

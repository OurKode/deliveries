// SPDX-License-Identifier: GPL-3.0-or-later
package dev.itsvic.parceltracker.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import dev.itsvic.parceltracker.R

object OEMHelper {
  fun isXiaomiDevice(): Boolean {
    val manufacturer = Build.MANUFACTURER.orEmpty().lowercase()
    val brand = Build.BRAND.orEmpty().lowercase()
    return manufacturer.contains("xiaomi") ||
        manufacturer.contains("redmi") ||
        manufacturer.contains("poco") ||
        brand.contains("xiaomi") ||
        brand.contains("redmi") ||
        brand.contains("poco")
  }

  fun openAutostartSettings(context: Context) {
    val miuiIntents = listOf(
        Intent().apply {
          component = ComponentName(
              "com.miui.securitycenter",
              "com.miui.permcenter.autostart.AutoStartManagementActivity"
          )
        },
        Intent("miui.intent.action.OP_AUTO_START").apply {
          addCategory(Intent.CATEGORY_DEFAULT)
        }
    )

    for (intent in miuiIntents) {
      try {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return
      } catch (_: Exception) {
        // Try next candidate
      }
    }

    // Fallback to Application Details Settings
    try {
      val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      Toast.makeText(
          context,
          context.getString(R.string.oem_autostart_not_found),
          Toast.LENGTH_LONG
      ).show()
      context.startActivity(fallbackIntent)
    } catch (_: Exception) {
      // Ignored if unable to launch settings
    }
  }

  fun openBatteryOptimizationSettings(context: Context) {
    try {
      val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (_: Exception) {
      try {
        val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
          data = Uri.fromParts("package", context.packageName, null)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(fallbackIntent)
      } catch (_: Exception) {
        // Ignored
      }
    }
  }
}

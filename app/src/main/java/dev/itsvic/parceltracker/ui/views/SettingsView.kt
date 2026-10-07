// SPDX-License-Identifier: GPL-3.0-or-later
package dev.itsvic.parceltracker.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.itsvic.parceltracker.BuildConfig
import dev.itsvic.parceltracker.BINDERBYTE_API_KEY
import dev.itsvic.parceltracker.DEMO_MODE
import dev.itsvic.parceltracker.R
import dev.itsvic.parceltracker.SYNC_INTERVAL_MINUTES
import dev.itsvic.parceltracker.UNMETERED_ONLY
import dev.itsvic.parceltracker.utils.OEMHelper
import dev.itsvic.parceltracker.api.ParcelHistoryItem
import dev.itsvic.parceltracker.api.Service
import dev.itsvic.parceltracker.api.Status
import dev.itsvic.parceltracker.dataStore
import dev.itsvic.parceltracker.db.Parcel
import dev.itsvic.parceltracker.enqueueNotificationWorker
import dev.itsvic.parceltracker.sendNotification
import dev.itsvic.parceltracker.ui.components.LogcatButton
import dev.itsvic.parceltracker.ui.theme.ParcelTrackerTheme
import java.time.LocalDateTime
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    onBackPressed: () -> Unit,
) {
  val context = LocalContext.current
  val demoModeFlow = remember(context) { context.dataStore.data.map { it[DEMO_MODE] == true } }
  val demoMode by demoModeFlow.collectAsState(false)

  val unmeteredOnlyFlow =
      remember(context) { context.dataStore.data.map { it[UNMETERED_ONLY] == true } }
  val unmeteredOnly by unmeteredOnlyFlow.collectAsState(false)

  val syncIntervalMinutesFlow =
      remember(context) { context.dataStore.data.map { it[SYNC_INTERVAL_MINUTES] ?: 60L } }
  val syncIntervalMinutes by syncIntervalMinutesFlow.collectAsState(60L)

  val isXiaomi = remember { OEMHelper.isXiaomiDevice() }
  val coroutineScope = rememberCoroutineScope()
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

  val binderbyteApiKeyFlow =
      remember(context) { context.dataStore.data.map { it[BINDERBYTE_API_KEY] ?: "" } }
  val binderbyteApiKey by binderbyteApiKeyFlow.collectAsState("")

  fun <T> setValue(key: Preferences.Key<T>, value: T) {
    coroutineScope.launch { context.dataStore.edit { it[key] = value } }
  }

  val setUnmeteredOnly: (Boolean) -> Unit = { value ->
    coroutineScope.launch {
      context.dataStore.edit { it[UNMETERED_ONLY] = value }
      // reschedule notification worker to update constraints
      context.enqueueNotificationWorker()
    }
  }

  val setSyncIntervalMinutes: (Long) -> Unit = { value ->
    coroutineScope.launch {
      context.dataStore.edit { it[SYNC_INTERVAL_MINUTES] = value }
      // reschedule notification worker to update interval
      context.enqueueNotificationWorker()
    }
  }

  Scaffold(
      topBar = {
        LargeTopAppBar(
            title = { Text(stringResource(R.string.settings)) },
            navigationIcon = {
              IconButton(onClick = onBackPressed) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.go_back))
              }
            },
            scrollBehavior = scrollBehavior,
        )
      },
      modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
  ) { innerPadding ->
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
      
      // Preferences Section
      Text(
          text = "Preferences",
          modifier = Modifier.padding(top = 16.dp, start = 8.dp),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary)

      Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          ),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
          Row(
              modifier =
                  Modifier.clickable { setUnmeteredOnly(unmeteredOnly.not()) }
                      .fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
              Text(
                  stringResource(R.string.unmetered_only_setting),
                  fontWeight = FontWeight.SemiBold)
              Text(
                  stringResource(R.string.unmetered_only_setting_detail),
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = unmeteredOnly, onCheckedChange = { setUnmeteredOnly(it) })
          }

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.sync_interval_title),
                fontWeight = FontWeight.SemiBold)
            Text(
                stringResource(R.string.sync_interval_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            var intervalExpanded by remember { mutableStateOf(false) }
            val intervalOptions = listOf(
                15L to stringResource(R.string.sync_interval_15m),
                30L to stringResource(R.string.sync_interval_30m),
                60L to stringResource(R.string.sync_interval_1h),
                180L to stringResource(R.string.sync_interval_3h),
            )
            val currentIntervalLabel =
                intervalOptions.firstOrNull { it.first == syncIntervalMinutes }?.second
                    ?: stringResource(R.string.sync_interval_1h)

            ExposedDropdownMenuBox(
                expanded = intervalExpanded,
                onExpandedChange = { intervalExpanded = it },
                modifier = Modifier.fillMaxWidth(),
            ) {
              OutlinedTextField(
                  value = currentIntervalLabel,
                  onValueChange = {},
                  readOnly = true,
                  shape = RoundedCornerShape(12.dp),
                  trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(intervalExpanded) },
                  colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                  modifier =
                      Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                          .fillMaxWidth(),
              )
              ExposedDropdownMenu(
                  expanded = intervalExpanded,
                  onDismissRequest = { intervalExpanded = false },
              ) {
                intervalOptions.forEach { (minutes, label) ->
                  DropdownMenuItem(
                      text = { Text(label) },
                      onClick = {
                        setSyncIntervalMinutes(minutes)
                        intervalExpanded = false
                      },
                  )
                }
              }
            }
          }
        }
      }

      if (isXiaomi) {
        Text(
            text = stringResource(R.string.oem_guidance_title),
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                stringResource(R.string.oem_guidance_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            FilledTonalButton(
                onClick = { OEMHelper.openAutostartSettings(context) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)) {
              Text(stringResource(R.string.open_autostart_settings))
            }

            OutlinedButton(
                onClick = { OEMHelper.openBatteryOptimizationSettings(context) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)) {
              Text(stringResource(R.string.open_battery_settings))
            }
          }
        }
      }

      // API Keys Section
      Text(
          text = stringResource(R.string.settings_api_keys),
          modifier = Modifier.padding(start = 8.dp),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary)

      Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          ),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
              value = binderbyteApiKey,
              onValueChange = { setValue(BINDERBYTE_API_KEY, it) },
              modifier = Modifier.fillMaxWidth(),
              label = { Text("Binderbyte API Key") },
              shape = RoundedCornerShape(12.dp),
              singleLine = true,
              visualTransformation = PasswordVisualTransformation(),
          )

          Text(
              AnnotatedString.fromHtml(
                  stringResource(R.string.binderbyte_api_key_flavor_text),
                  linkStyles =
                      TextLinkStyles(
                          style =
                              SpanStyle(
                                  textDecoration = TextDecoration.Underline,
                                  color = MaterialTheme.colorScheme.primary))),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }

      // Experimental / Advanced Tools Section
      Text(
          text = "Advanced Tools",
          modifier = Modifier.padding(start = 8.dp),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary)

      Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          ),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
              modifier =
                  Modifier.clickable { setValue(DEMO_MODE, demoMode.not()) }
                      .fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
              Text(
                  stringResource(R.string.demo_mode),
                  fontWeight = FontWeight.SemiBold)
              Text(
                  stringResource(R.string.demo_mode_detail),
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = demoMode, onCheckedChange = { setValue(DEMO_MODE, it) })
          }

          if (BuildConfig.DEBUG) {
            FilledTonalButton(
                onClick = {
                  context.sendNotification(
                      Parcel(0xf100f, "Cool stuff", "", null, Service.EXAMPLE),
                      Status.OutForDelivery,
                      ParcelHistoryItem(
                          "The courier has picked up the package", LocalDateTime.now(), ""))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)) {
              Text("Send test notification")
            }
          }

          LogcatButton(modifier = Modifier.fillMaxWidth())
        }
      }

      Text(
          "${stringResource(R.string.app_name)} ${BuildConfig.VERSION_NAME}",
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
@PreviewLightDark
private fun SettingsViewPreview() {
  ParcelTrackerTheme {
    SettingsView(
        onBackPressed = {},
    )
  }
}

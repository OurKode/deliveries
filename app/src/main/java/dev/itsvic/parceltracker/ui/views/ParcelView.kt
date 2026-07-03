// SPDX-License-Identifier: GPL-3.0-or-later
package dev.itsvic.parceltracker.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.itsvic.parceltracker.R
import dev.itsvic.parceltracker.api.Parcel
import dev.itsvic.parceltracker.api.ParcelHistoryItem
import dev.itsvic.parceltracker.api.Service
import dev.itsvic.parceltracker.api.Status
import dev.itsvic.parceltracker.api.getDeliveryServiceName
import dev.itsvic.parceltracker.ui.components.ParcelHistoryItemRow
import dev.itsvic.parceltracker.ui.theme.MenuItemContentPadding
import dev.itsvic.parceltracker.ui.theme.ParcelTrackerTheme
import dev.itsvic.parceltracker.ui.theme.getColorsForStatus
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelView(
    parcel: Parcel,
    humanName: String,
    service: Service,
    isArchived: Boolean,
    archivePromptDismissed: Boolean,
    onBackPressed: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onArchive: () -> Unit,
    onArchivePromptDismissal: () -> Unit,
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  var expanded by remember { mutableStateOf(false) }
  val clipboardManager = LocalClipboardManager.current
  val (statusContainerColor, statusTextColor) = getColorsForStatus(parcel.currentStatus)

  Scaffold(
      topBar = {
        MediumTopAppBar(
            title = { Text(parcel.description?.takeIf { it.isNotBlank() } ?: humanName, fontWeight = FontWeight.Bold) },
            navigationIcon = {
              IconButton(onClick = onBackPressed) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.go_back))
              }
            },
            actions = {
              IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Filled.MoreVert, stringResource(R.string.more_options))
              }
              DropdownMenu(
                  expanded = expanded,
                  onDismissRequest = { expanded = false },
              ) {
                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Filled.Edit, stringResource(R.string.edit)) },
                    text = { Text(stringResource(R.string.edit)) },
                    onClick = {
                      expanded = false
                      onEdit()
                    },
                    contentPadding = MenuItemContentPadding,
                )
                if (!isArchived)
                    DropdownMenuItem(
                        leadingIcon = {
                          Icon(
                              painterResource(R.drawable.archive), stringResource(R.string.archive))
                        },
                        text = { Text(stringResource(R.string.archive)) },
                        onClick = onArchive,
                        contentPadding = MenuItemContentPadding,
                    )
                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Filled.Delete, stringResource(R.string.delete)) },
                    text = { Text(stringResource(R.string.delete)) },
                    onClick = onDelete,
                    contentPadding = MenuItemContentPadding,
                )
              }
            },
            scrollBehavior = scrollBehavior,
        )
      },
      modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          // Parcel Info Card
          item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                )) {
              Column(
                  modifier = Modifier.padding(16.dp),
                  verticalArrangement = Arrangement.spacedBy(12.dp)) {
                
                // Courier & ID
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                  Column {
                    Text(
                        text = "Courier",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    getDeliveryServiceName(service)?.let {
                      Text(
                          stringResource(it),
                          style = MaterialTheme.typography.titleMedium,
                          fontWeight = FontWeight.Bold,
                          color = MaterialTheme.colorScheme.onSurface)
                    }
                  }

                  Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                      Text(
                          text = "Tracking ID",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant)
                      SelectionContainer {
                        Text(
                            parcel.id,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface)
                      }
                    }
                    Text(
                        text = "Copy",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { clipboardManager.setText(AnnotatedString(parcel.id)) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }

                // Dynamic properties
                if (parcel.properties.isNotEmpty()) {
                  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    parcel.properties.forEach { (key, value) ->
                      Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            stringResource(key),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            value,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End)
                      }
                    }
                  }
                }
              }
            }
          }

          // Current Status Banner
          item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = statusContainerColor)) {
              Row(
                  modifier = Modifier.padding(16.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(statusTextColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center) {
                  Icon(
                      painter = painterResource(
                          when (parcel.currentStatus) {
                            Status.Preadvice -> R.drawable.outline_other_admission_24
                            Status.LockerboxAcceptedParcel -> R.drawable.outline_deployed_code_update_24
                            Status.PickedUpByCourier -> R.drawable.outline_deployed_code_account_24
                            Status.InTransit -> R.drawable.outline_local_shipping_24
                            Status.InWarehouse -> R.drawable.outline_warehouse_24
                            Status.Customs -> R.drawable.outline_search_24
                            Status.OutForDelivery -> R.drawable.outline_delivery_truck_speed_24
                            Status.DeliveryFailure -> R.drawable.outline_error_24
                            Status.PickupTimeEndingSoon -> R.drawable.outline_notifications_active_24
                            Status.AwaitingPickup -> R.drawable.outline_pin_drop_24
                            Status.Delivered,
                            Status.PickedUp -> R.drawable.outline_check_24
                            Status.DeliveredToNeighbor -> R.drawable.outline_holiday_village_24
                            Status.DeliveredToASafePlace -> R.drawable.outline_roofing_24
                            Status.DroppedAtCustomerService -> R.drawable.outline_support_agent_24
                            Status.ReturningToSender -> R.drawable.outline_arrow_top_left_24
                            Status.ReturnedToSender -> R.drawable.outline_arrow_top_left_24
                            Status.Delayed -> R.drawable.outline_deployed_code_history_24
                            Status.Damaged -> R.drawable.outline_deployed_code_alert_24
                            Status.Destroyed -> R.drawable.outline_destruction_24
                            else -> R.drawable.outline_question_mark_24
                          }
                      ),
                      contentDescription = null,
                      tint = statusTextColor,
                      modifier = Modifier.size(24.dp))
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                  Text(
                      text = "Current Status",
                      style = MaterialTheme.typography.labelSmall,
                      color = statusTextColor.copy(alpha = 0.8f))
                  Text(
                      text = LocalContext.current.getString(parcel.currentStatus.nameResource),
                      style = MaterialTheme.typography.titleLarge,
                      fontWeight = FontWeight.ExtraBold,
                      color = statusTextColor)
                }
              }
            }
          }

          // Archive Prompt
          if (!isArchived &&
              !archivePromptDismissed &&
              (parcel.currentStatus == Status.Delivered || parcel.currentStatus == Status.PickedUp)) {
            item {
              Card(
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(
                      stringResource(R.string.archive_prompt_question),
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold)
                  Text(
                      stringResource(R.string.archive_prompt_text),
                      style = MaterialTheme.typography.bodyMedium)
                  Row(
                      horizontalArrangement = Arrangement.spacedBy(12.dp),
                      modifier = Modifier.fillMaxWidth()) {
                    FilledTonalButton(
                        onArchivePromptDismissal, modifier = Modifier.weight(1f)) {
                      Text(stringResource(R.string.ignore))
                    }
                    Button(onArchive, modifier = Modifier.weight(1f)) {
                      Text(stringResource(R.string.archive))
                    }
                  }
                }
              }
            }
          }

          // Timeline Section Title
          if (parcel.history.isNotEmpty()) {
            item {
              Text(
                  text = "Tracking Timeline",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(top = 8.dp))
            }

            items(parcel.history.size) { index ->
              ParcelHistoryItemRow(
                  item = parcel.history[index],
                  isFirst = index == 0,
                  isLast = index == parcel.history.size - 1)
            }
          }
        }
      }
}

@Composable
@PreviewLightDark
private fun ParcelViewPreview() {
  val parcel =
      Parcel(
          "EXMPL0001",
          listOf(
              ParcelHistoryItem(
                  "The package got lost. Whoops!",
                  LocalDateTime.of(2025, 1, 1, 12, 0, 0),
                  "Warsaw, Poland"),
              ParcelHistoryItem(
                  "Arrived at local warehouse",
                  LocalDateTime.of(2025, 1, 1, 10, 0, 0),
                  "Warsaw, Poland"),
              ParcelHistoryItem(
                  "En route to local warehouse",
                  LocalDateTime.of(2024, 12, 1, 12, 0, 0),
                  "Netherlands"),
              ParcelHistoryItem(
                  "Label created", LocalDateTime.of(2024, 12, 1, 12, 0, 0), "Netherlands"),
          ),
          Status.DeliveryFailure)
  ParcelTrackerTheme {
    ParcelView(
        parcel,
        "My precious package",
        Service.EXAMPLE,
        isArchived = false,
        archivePromptDismissed = false,
        onBackPressed = {},
        onEdit = {},
        onDelete = {},
        onArchive = {},
        onArchivePromptDismissal = {},
    )
  }
}

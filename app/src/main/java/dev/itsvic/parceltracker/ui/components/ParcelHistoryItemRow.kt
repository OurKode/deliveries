// SPDX-License-Identifier: GPL-3.0-or-later
package dev.itsvic.parceltracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.itsvic.parceltracker.api.ParcelHistoryItem
import dev.itsvic.parceltracker.ui.theme.ParcelTrackerTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ParcelHistoryItemRow(item: ParcelHistoryItem, isFirst: Boolean, isLast: Boolean) {
  Row(
      modifier = Modifier
          .fillMaxWidth()
          .height(IntrinsicSize.Min),
      horizontalArrangement = Arrangement.spacedBy(16.dp)) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(24.dp).fillMaxHeight()) {
      // Top line
      Box(
          modifier = Modifier
              .width(2.dp)
              .height(12.dp)
              .background(if (isFirst) Color.Transparent else MaterialTheme.colorScheme.outlineVariant))

      // Center dot
      Box(
          modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(if (isFirst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant))

      // Bottom line
      Box(
          modifier = Modifier
              .width(2.dp)
              .weight(1f)
              .background(if (isLast) Color.Transparent else MaterialTheme.colorScheme.outlineVariant))
    }

    Column(
        modifier = Modifier.weight(1f).padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)) {
      SelectionContainer {
        Text(
            item.description,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isFirst) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            item.time.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (item.location.isNotBlank()) {
          Text(
              item.location,
              style = MaterialTheme.typography.bodyMedium,
              textAlign = TextAlign.End,
              color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }
  }
}

@Composable
@PreviewLightDark
private fun ParcelHistoryItemRowPreview() {
  val exampleItem =
      ParcelHistoryItem(
          "Customs service",
          LocalDateTime.of(2024, 12, 22, 9, 38, 48),
          "Warsaw, Poland")

  ParcelTrackerTheme {
    Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
      ParcelHistoryItemRow(exampleItem, isFirst = true, isLast = false)
    }
  }
}


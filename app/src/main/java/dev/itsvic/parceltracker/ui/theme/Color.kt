package dev.itsvic.parceltracker.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import dev.itsvic.parceltracker.api.Status

val primaryLight = Color(0xFF8D4D2D)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFFFDBCC)
val onPrimaryContainerLight = Color(0xFF351000)
val secondaryLight = Color(0xFF765749)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFFFDBCC)
val onSecondaryContainerLight = Color(0xFF2C160B)
val tertiaryLight = Color(0xFF655F31)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFEDE4A9)
val onTertiaryContainerLight = Color(0xFF1F1C00)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)
val backgroundLight = Color(0xFFFFF8F6)
val onBackgroundLight = Color(0xFF221A16)
val surfaceLight = Color(0xFFFFF8F6)
val onSurfaceLight = Color(0xFF221A16)
val surfaceVariantLight = Color(0xFFF4DED5)
val onSurfaceVariantLight = Color(0xFF52443D)
val outlineLight = Color(0xFF85736C)
val outlineVariantLight = Color(0xFFD7C2B9)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF382E2A)
val inverseOnSurfaceLight = Color(0xFFFFEDE6)
val inversePrimaryLight = Color(0xFFFFB693)
val surfaceDimLight = Color(0xFFE8D7D0)
val surfaceBrightLight = Color(0xFFFFF8F6)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFFFF1EB)
val surfaceContainerLight = Color(0xFFFCEAE3)
val surfaceContainerHighLight = Color(0xFFF6E5DE)
val surfaceContainerHighestLight = Color(0xFFF0DFD8)

val primaryDark = Color(0xFFFFB693)
val onPrimaryDark = Color(0xFF542104)
val primaryContainerDark = Color(0xFF703718)
val onPrimaryContainerDark = Color(0xFFFFDBCC)
val secondaryDark = Color(0xFFE6BEAC)
val onSecondaryDark = Color(0xFF432A1E)
val secondaryContainerDark = Color(0xFF5C4033)
val onSecondaryContainerDark = Color(0xFFFFDBCC)
val tertiaryDark = Color(0xFFD0C890)
val onTertiaryDark = Color(0xFF363107)
val tertiaryContainerDark = Color(0xFF4D481C)
val onTertiaryContainerDark = Color(0xFFEDE4A9)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF1A120E)
val onBackgroundDark = Color(0xFFF0DFD8)
val surfaceDark = Color(0xFF1A120E)
val onSurfaceDark = Color(0xFFF0DFD8)
val surfaceVariantDark = Color(0xFF52443D)
val onSurfaceVariantDark = Color(0xFFD7C2B9)
val outlineDark = Color(0xFFA08D85)
val outlineVariantDark = Color(0xFF52443D)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFF0DFD8)
val inverseOnSurfaceDark = Color(0xFF382E2A)
val inversePrimaryDark = Color(0xFF8D4D2D)
val surfaceDimDark = Color(0xFF1A120E)
val surfaceBrightDark = Color(0xFF423732)
val surfaceContainerLowestDark = Color(0xFF140C09)
val surfaceContainerLowDark = Color(0xFF221A16)
val surfaceContainerDark = Color(0xFF271E1A)
val surfaceContainerHighDark = Color(0xFF322824)
val surfaceContainerHighestDark = Color(0xFF3D332E)
val statusContainerDeliveredLight = Color(0xFFD1E7DD)
val statusTextDeliveredLight = Color(0xFF0F5132)
val statusContainerInTransitLight = Color(0xFFCFF4FC)
val statusTextInTransitLight = Color(0xFF055160)
val statusContainerPendingLight = Color(0xFFFFF3CD)
val statusTextPendingLight = Color(0xFF664D03)
val statusContainerFailureLight = Color(0xFFF8D7DA)
val statusTextFailureLight = Color(0xFF842029)

val statusContainerDeliveredDark = Color(0xFF0F5132)
val statusTextDeliveredDark = Color(0xFFD1E7DD)
val statusContainerInTransitDark = Color(0xFF055160)
val statusTextInTransitDark = Color(0xFFCFF4FC)
val statusContainerPendingDark = Color(0xFF664D03)
val statusTextPendingDark = Color(0xFFFFF3CD)
val statusContainerFailureDark = Color(0xFF842029)
val statusTextFailureDark = Color(0xFFF8D7DA)

@Composable
fun getColorsForStatus(status: Status?): Pair<Color, Color> {
  val isDark = isSystemInDarkTheme()
  if (status == null) {
    return if (isDark) Pair(Color(0xFF333333), Color(0xFFCCCCCC)) else Pair(Color(0xFFF0F0F0), Color(0xFF555555))
  }
  return when (status) {
    Status.Delivered,
    Status.PickedUp,
    Status.CustomsSuccess -> {
      if (isDark) Pair(statusContainerDeliveredDark, statusTextDeliveredDark)
      else Pair(statusContainerDeliveredLight, statusTextDeliveredLight)
    }
    Status.InTransit,
    Status.Preadvice,
    Status.LockerboxAcceptedParcel,
    Status.PickedUpByCourier,
    Status.InWarehouse,
    Status.Customs,
    Status.Readdressed -> {
      if (isDark) Pair(statusContainerInTransitDark, statusTextInTransitDark)
      else Pair(statusContainerInTransitLight, statusTextInTransitLight)
    }
    Status.OutForDelivery,
    Status.AwaitingPickup,
    Status.PickupTimeEndingSoon,
    Status.DeliveredToNeighbor,
    Status.DeliveredToASafePlace,
    Status.DroppedAtCustomerService -> {
      if (isDark) Pair(statusContainerPendingDark, statusTextPendingDark)
      else Pair(statusContainerPendingLight, statusTextPendingLight)
    }
    Status.DeliveryFailure,
    Status.ReturningToSender,
    Status.ReturnedToSender,
    Status.Delayed,
    Status.Damaged,
    Status.Destroyed,
    Status.CustomsHeld,
    Status.NetworkFailure,
    Status.NoData,
    Status.Unknown -> {
      if (isDark) Pair(statusContainerFailureDark, statusTextFailureDark)
      else Pair(statusContainerFailureLight, statusTextFailureLight)
    }
  }
}


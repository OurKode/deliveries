package dev.itsvic.parceltracker.api

import dev.itsvic.parceltracker.R
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FormatValidationTest {
  @Test
  fun binderbyteDeliveryService_NotBlankFormatReturnsTrue() {
    val service = BinderbyteDeliveryService("jne", R.string.service_jne)
    assertTrue(service.acceptsFormat("SOCAG00123456789"))
  }

  @Test
  fun binderbyteDeliveryService_BlankFormatReturnsFalse() {
    val service = BinderbyteDeliveryService("jne", R.string.service_jne)
    assertFalse(service.acceptsFormat(""))
    assertFalse(service.acceptsFormat("   "))
  }
}

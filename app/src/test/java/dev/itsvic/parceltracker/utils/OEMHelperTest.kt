package dev.itsvic.parceltracker.utils

import org.junit.Assert.assertNotNull
import org.junit.Test

class OEMHelperTest {
  @Test
  fun oemHelper_IsXiaomiDeviceMethodExistsAndReturnsBoolean() {
    val result = OEMHelper.isXiaomiDevice()
    assertNotNull(result)
  }
}

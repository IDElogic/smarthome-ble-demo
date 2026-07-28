package com.iwoioapps.smarthomebledemo.ui.scan

import com.iwoioapps.smarthomebledemo.ble.ScannedDevice
import com.iwoioapps.smarthomebledemo.ble.shouldEmitUpdatedDevice
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BleScannerTest {

    // --- Arrange: közös alap-eszköz ---
    private val device = ScannedDevice(
        name = "Living Room Light",
        address = "AA:BB:CC:DD:EE:01",
        rssi = -50
    )

    @Test
    fun `new address emits device`() {
        // Arrange
        val previous = null
        // Act
        val result = shouldEmitUpdatedDevice(previous, device)
        // Assert
        assertTrue(result)
    }

    @Test
    fun `same device does not emit`() {
        // Arrange
        val previous = device.copy()
        // Act
        val result = shouldEmitUpdatedDevice(previous, device)
        // Assert
        assertFalse(result)
    }

    @Test
    fun `changed rssi emits device`() {
        // Arrange
        val previous = device.copy(rssi = -70)
        // Act
        val result = shouldEmitUpdatedDevice(previous, device)
        // Assert
        assertTrue(result)
    }

    @Test
    fun `changed name emits device`() {
        // Arrange
        val previous = device.copy(name = "Old Name")
        // Act
        val result = shouldEmitUpdatedDevice(previous, device)
        // Assert
        assertTrue(result)
    }

    @Test
    fun `changed temperature emits device`() {
        // Arrange
        val previous = device.copy(temperature = 22.5f)
        val updated = device.copy(temperature = 23.1f)
        // Act
        val result = shouldEmitUpdatedDevice(previous, updated)
        // Assert
        assertTrue(result)
    }

    @Test
    fun `null temperature to value emits device`() {
        // Arrange
        val previous = device.copy(temperature = null)
        val updated = device.copy(temperature = 21.0f)
        // Act
        val result = shouldEmitUpdatedDevice(previous, updated)
        // Assert
        assertTrue(result)
    }
}

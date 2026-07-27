package com.iwoioapps.smarthomebledemo.ui.scan


import com.iwoioapps.smarthomebledemo.ble.ScannedDevice
import com.iwoioapps.smarthomebledemo.ble.shouldEmitUpdatedDevice
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class BleScannerLogicTest {

    @Test
    fun `shouldEmitUpdatedDevice returns true when same address has different name`() {
        val previous = ScannedDevice(
            name = "Living room",
            address = "AA:BB:CC:11",
            rssi = -60
        )

        val newDevice = ScannedDevice(
            name = "Lampa",
            address = "AA:BB:CC:11",
            rssi = -60
        )

        val result = shouldEmitUpdatedDevice(previous, newDevice)

        assertTrue(result)
    }

    @Test
    fun `shouldEmitUpdatedDevice returns false when device data is identical`() {
        val previous = ScannedDevice(
            name = "Lampa",
            address = "AA:BB:CC:11",
            rssi = -60
        )

        val newDevice = ScannedDevice(
            name = "Lampa",
            address = "AA:BB:CC:11",
            rssi = -60
        )

        val result = shouldEmitUpdatedDevice(previous, newDevice)

        assertFalse(result)
    }

    @Test
    fun `shouldEmitUpdatedDevice returns true when same address has different rssi`() {
        val previous = ScannedDevice(
            name = "Air quality sensor",
            address = "AA:BB:CC:11",
            rssi = -70
        )

        val newDevice = ScannedDevice(
            name = "Air quality sensor",
            address = "AA:BB:CC:11",
            rssi = -55
        )

        val result = shouldEmitUpdatedDevice(previous, newDevice)

        assertTrue(result)
    }

}

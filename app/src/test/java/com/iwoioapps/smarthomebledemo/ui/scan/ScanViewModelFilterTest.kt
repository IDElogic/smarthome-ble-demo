package com.iwoioapps.smarthomebledemo.ui.scan

import com.iwoioapps.smarthomebledemo.ble.ScannedDevice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScanViewModelFilterTest {

    private val devices = listOf(
        ScannedDevice(name = "Living Room Light", address = "AA:BB:CC:DD:EE:01", rssi = -50),
        ScannedDevice(name = "Kitchen Sensor", address = "AA:BB:CC:DD:EE:02", rssi = -60),
        ScannedDevice(name = null, address = "11:22:33:44:55:66", rssi = -70)
    )

    @Test
    fun `blank query returns all devices`() {
        val result = ScanViewModel.filterDevices(devices, "")
        assertEquals(devices, result)
    }

    @Test
    fun `query matches device name case-insensitively`() {
        val result = ScanViewModel.filterDevices(devices, "living")
        assertEquals(1, result.size)
        assertEquals("Living Room Light", result[0].name)
    }

    @Test
    fun `query matches device address`() {
        val result = ScanViewModel.filterDevices(devices, "11:22")
        assertEquals(1, result.size)
        assertEquals("11:22:33:44:55:66", result[0].address)
    }

    @Test
    fun `query with no matches returns empty list`() {
        val result = ScanViewModel.filterDevices(devices, "zzz")
        assertTrue(result.isEmpty())
    }




}
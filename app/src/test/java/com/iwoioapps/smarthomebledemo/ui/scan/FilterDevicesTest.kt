package com.iwoioapps.smarthomebledemo.ui.scan


import com.iwoioapps.smarthomebledemo.ble.ScannedDevice
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterDevicesTest {

    private val devices = listOf(
        ScannedDevice(name = "Lamp", address = "AA:BB:CC:11", rssi = -50),
        ScannedDevice(name = "Speaker", address = "DD:EE:FF:22", rssi = -60),
        ScannedDevice(name = "Air condition", address = "GG:HH:II:33", rssi = -65),
        ScannedDevice(name = null, address = "11:22:33:44:55:66", rssi = -70)
    )

    //üres query → teljes lista,
    @Test
    fun `filterDevices returns original list when query is blank`() {
        //fun `given query is blank when filtering then returns original list() {
        val devices = listOf(
            ScannedDevice(name = "Lamp", address = "AA:BB:CC:11", rssi = -50),
            ScannedDevice(name = "Speaker", address = "DD:EE:FF:22", rssi = -60)
        )
        val query = ""
        // Act
        val result = ScanViewModel.filterDevices(devices, query)
        // Assert
        assertEquals(devices, result)
    }

    //üres lista → üres eredmény,
    @Test
    fun `filterDevices returns empty list when devices list is empty`() {
       //fun `given empty devices list when filtering then returns empty list`() {
        val devices = emptyList<ScannedDevice>()
        val query = "abc"

        val result = ScanViewModel.filterDevices(devices, query)

        assertEquals(emptyList<ScannedDevice>(), result)
    }


    //név alapján pozitív találat,
    @Test
    fun `filterDevices returns matching devices when query matches name`() {
        //fun `given devices  with matching name when filtering then returns matching device`() {

        val devices = listOf(
            ScannedDevice(name = "Lamp", address = "AA:BB:CC:11", rssi = -50),
            ScannedDevice(name = "Speaker", address = "DD:EE:FF:22", rssi = -60)
        )
        val query = "lamp"

        val result = ScanViewModel.filterDevices(devices, query)

        assertEquals(listOf(devices[0]),result)
    }


    //név + address alapján negatív eset (nincs találat)
    @Test
    fun `filterDevices returns empty list when query does not match any name or address`() {
        // fun `given devices with does not match any name or address when filtering then returns empty list`() {

        val devices = listOf(
        ScannedDevice(name = "Lamp", address = "AA:BB:CC:11", rssi = -50),
        ScannedDevice(name = "Speaker", address = "DD:EE:FF:22", rssi = -60),
        ScannedDevice(name = "Air condition", address = "GG:HH:II:33", rssi = -65)
        )
    val query = "blinds"

    val result = ScanViewModel.filterDevices(devices, query)

    assertEquals(emptyList<ScannedDevice>(), result)
}

    //address alapján pozitív találat.
    @Test
    fun `filterDevices returns matching devices when query matches address`() {
      //fun `given devices with matching address when filtering then returns only matching devices`()`{

        val devices = listOf(
            ScannedDevice(name = "Lamp", address = "AA:BB:CC:11", rssi = -50),
            ScannedDevice(name = "Speaker", address = "DD:EE:FF:22", rssi = -60),
        )
        val query = "AA:BB"

        val result = ScanViewModel.filterDevices(devices, query)

        assertEquals(listOf(devices[0]), result)
    }

}
package com.iwoioapps.smarthomebledemo.ble

import java.util.UUID

/**
 * Custom 128-bit UUIDs for this demo's "Smart Home" GATT service.
 * When testing with nRF Connect's GATT Server simulator on a second phone,
 * create a service and characteristics using EXACTLY these UUIDs.
 */


object BleConstants {

    val SMART_HOME_SERVICE_UUID: UUID =
        UUID.fromString("5b1d2f00-1a2b-4c3d-9e8f-7a6b5c4d3e2f")

    // Write + Notify. 1 byte: 0x00 = off, 0x01 = on
    val SWITCH_CHARACTERISTIC_UUID: UUID =
        UUID.fromString("5b1d2f01-1a2b-4c3d-9e8f-7a6b5c4d3e2f")

    // Read + Write. 1 byte: brightness percentage, 0-100
    val BRIGHTNESS_CHARACTERISTIC_UUID: UUID =
        UUID.fromString("5b1d2f02-1a2b-4c3d-9e8f-7a6b5c4d3e2f")

    // Standard Client Characteristic Configuration Descriptor - required to enable notifications
    val CCCD_UUID: UUID =
        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    val TEMPERATURE_CHARACTERISTIC_UUID: UUID =
        UUID.fromString("00002a6e-0000-1000-8000-00805f9b34fb") // BLE standard

}

package com.iwoioapps.smarthomebledemo.ble

sealed class ConnectionState {
    data object Disconnected : ConnectionState()
    data object Connecting : ConnectionState()
    data object Connected : ConnectionState()
    data object DiscoveringServices : ConnectionState()
    data object Ready : ConnectionState()
    data class Failed(val message: String) : ConnectionState()
}

data class ScannedDevice(
    val name: String?,
    val address: String,
    val rssi: Int
)

data class SwitchState(
    val isOn: Boolean = false,
    val brightness: Int = 0
)

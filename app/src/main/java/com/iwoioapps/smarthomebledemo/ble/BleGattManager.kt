package com.iwoioapps.smarthomebledemo.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps the callback-based Android BLE GATT client APIs behind StateFlow so
 * ViewModels/Compose UI never touch BluetoothGattCallback directly.
 *
 * Talking points:
 * - Connection state machine: Disconnected -> Connecting -> Connected -> DiscoveringServices -> Ready
 * - Service/characteristic discovery is asynchronous and must complete before read/write/notify
 * - Notifications require writing the CCCD descriptor, not just calling setCharacteristicNotification
 */
@Singleton
class BleGattManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter
) {
    private var gatt: BluetoothGatt? = null
    private var switchCharacteristic: BluetoothGattCharacteristic? = null
    private var brightnessCharacteristic: BluetoothGattCharacteristic? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _switchState = MutableStateFlow(SwitchState())
    val switchState: StateFlow<SwitchState> = _switchState.asStateFlow()

    private var temperatureCharacteristic: BluetoothGattCharacteristic? = null

    private val _temperature = MutableStateFlow<Float?>(null)
    val temperature: StateFlow<Float?> = _temperature.asStateFlow()


    @SuppressLint("MissingPermission")
    fun connect(address: String) {
        val device = bluetoothAdapter.getRemoteDevice(address)
        _connectionState.value = ConnectionState.Connecting
        gatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        _connectionState.value = ConnectionState.Disconnected
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    fun setSwitch(isOn: Boolean) {
        val characteristic = switchCharacteristic ?: return
        characteristic.value = byteArrayOf(if (isOn) 1 else 0)
        gatt?.writeCharacteristic(characteristic)
        // Optimistic local update: this characteristic has WRITE, and we already
        // know the value we just sent, so the UI shouldn't wait for a round-trip
        // (some peripherals echo writes back as a notification, but that's not
        // guaranteed by the GATT spec, so we don't rely on it).
        _switchState.value = _switchState.value.copy(isOn = isOn)
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    fun setBrightness(percent: Int) {
        val characteristic = brightnessCharacteristic ?: return
        val clamped = percent.coerceIn(0, 100)
        characteristic.value = byteArrayOf(clamped.toByte())
        gatt?.writeCharacteristic(characteristic)
        // Brightness has no NOTIFY property at all, so without this optimistic
        // update the slider would always snap back to its last read value.
        _switchState.value = _switchState.value.copy(brightness = clamped)
    }

    private val gattCallback = object : BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _connectionState.value = ConnectionState.DiscoveringServices
                    g.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectionState.value = ConnectionState.Disconnected
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                _connectionState.value = ConnectionState.Failed("Service discovery failed ($status)")
                return
            }
            val service = g.getService(BleConstants.SMART_HOME_SERVICE_UUID)
            if (service == null) {
                _connectionState.value = ConnectionState.Failed("Smart home service not found on device")
                return
            }
            switchCharacteristic = service.getCharacteristic(BleConstants.SWITCH_CHARACTERISTIC_UUID)
            brightnessCharacteristic = service.getCharacteristic(BleConstants.BRIGHTNESS_CHARACTERISTIC_UUID)
            temperatureCharacteristic = service.getCharacteristic(BleConstants.TEMPERATURE_CHARACTERISTIC_UUID)

            switchCharacteristic?.let { enableNotifications(g, it) }
            brightnessCharacteristic?.let { g.readCharacteristic(it) }
            temperatureCharacteristic?.let {
                enableNotifications(g, it)   // notify, ha az eszköz támogatja
                g.readCharacteristic(it)     // + egyszer rögtön olvassuk is
            }

            _connectionState.value = ConnectionState.Ready
        }

        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(
            g: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            handleCharacteristicUpdate(characteristic)
        }

        @Suppress("DEPRECATION")
        override fun onCharacteristicRead(
            g: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                handleCharacteristicUpdate(characteristic)
            }
        }

        @Suppress("DEPRECATION")
        private fun handleCharacteristicUpdate(characteristic: BluetoothGattCharacteristic) {
            val value = characteristic.value?.firstOrNull()?.toInt() ?: return
            when (characteristic.uuid) {
                BleConstants.SWITCH_CHARACTERISTIC_UUID -> {
                    _switchState.value = _switchState.value.copy(isOn = value == 1)
                }
                BleConstants.BRIGHTNESS_CHARACTERISTIC_UUID -> {
                    _switchState.value = _switchState.value.copy(brightness = value)
                }
                BleConstants.TEMPERATURE_CHARACTERISTIC_UUID -> {
                    val raw = characteristic.value
                        ?.takeIf { it.size >= 2 }
                        ?.let { bytes ->
                            val sint16 = (bytes[1].toInt() shl 8) or (bytes[0].toInt() and 0xFF)
                            sint16 / 100.0f
                        }
                    _temperature.value = raw
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableNotifications(g: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        g.setCharacteristicNotification(characteristic, true)
        val descriptor = characteristic.getDescriptor(BleConstants.CCCD_UUID)
        if (descriptor != null) {
            @Suppress("DEPRECATION")
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            @Suppress("DEPRECATION")
            g.writeDescriptor(descriptor)
        }
    }
}

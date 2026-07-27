package com.iwoioapps.smarthomebledemo.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps the callback-based BluetoothLeScanner API in a cold Flow so the rest
 * of the app can just `collect` scan results with coroutines.
 */

@Singleton
class BleScanner @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter
) {
    @SuppressLint("MissingPermission")

    fun scan(filterByServiceUuid: Boolean = true): Flow<ScannedDevice> = callbackFlow {
        val scanner = bluetoothAdapter.bluetoothLeScanner
            ?: throw IllegalStateException("Bluetooth is off or unsupported on this device.")

        // Eddig csak címeket tároltunk egy set-ben.
        // Most teljes ScannedDevice-et tárolunk cím szerint,
        // hogy meg tudjuk nézni, változott-e valami.
        val lastSeenByAddress = mutableMapOf<String, ScannedDevice>()
        val producerScope = this

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val address = result.device.address
                val newDevice = ScannedDevice(
                    name = result.device.name ?: result.scanRecord?.deviceName,
                    address = address,
                    rssi = result.rssi
                )

                val previous = lastSeenByAddress[address]

                if (shouldEmitUpdatedDevice(previous, newDevice)) {
                    lastSeenByAddress[address] = newDevice
                    producerScope.trySend(newDevice)
                }

            }

            override fun onScanFailed(errorCode: Int) {
                producerScope.close(
                    IllegalStateException("BLE scan failed with error code $errorCode")
                )
            }
        }

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val filters = if (filterByServiceUuid) {
            listOf(
                ScanFilter.Builder()
                    .setServiceUuid(ParcelUuid(BleConstants.SMART_HOME_SERVICE_UUID))
                    .build()
            )
        } else {
            emptyList()
        }

        scanner.startScan(filters, settings, callback)

        awaitClose {
            scanner.stopScan(callback)
        }
    }

}

internal fun shouldEmitUpdatedDevice(
    previous: ScannedDevice?,
    newDevice: ScannedDevice
): Boolean {
    return previous == null || previous != newDevice
}
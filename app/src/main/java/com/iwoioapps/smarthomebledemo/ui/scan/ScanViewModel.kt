package com.iwoioapps.smarthomebledemo.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iwoioapps.smarthomebledemo.ble.BleScanner
import com.iwoioapps.smarthomebledemo.ble.ScannedDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val bleScanner: BleScanner
) : ViewModel() {

    private val _devices = MutableStateFlow<List<ScannedDevice>>(emptyList())
    val devices: StateFlow<List<ScannedDevice>> = _devices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var scanJob: Job? = null

    fun startScan(filterByServiceUuid: Boolean = true) {
        scanJob?.cancel()
        _devices.value = emptyList()
        _error.value = null
        _isScanning.value = true
        scanJob = viewModelScope.launch {
            bleScanner.scan(filterByServiceUuid)
                .catch { e ->
                    _error.value = e.message ?: "Scan failed"
                    _isScanning.value = false
                }
                .collect { device ->
                    _devices.value = _devices.value + device
                }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        _isScanning.value = false
    }

    override fun onCleared() {
        super.onCleared()
        scanJob?.cancel()
    }

    companion object {
        fun filterDevices(devices: List<ScannedDevice>, query: String): List<ScannedDevice> {
            if (query.isBlank()) return devices
            return devices.filter { device ->
                device.name?.contains(query, ignoreCase = true) == true ||
                        device.address.contains(query, ignoreCase = true)
            }
        }
    }
}

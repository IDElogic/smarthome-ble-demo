package com.iwoioapps.smarthomebledemo.ui.smarthome

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.iwoioapps.smarthomebledemo.ble.BleGattManager
import com.iwoioapps.smarthomebledemo.ble.ConnectionState
import com.iwoioapps.smarthomebledemo.ble.SwitchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SmartHomeViewModel @Inject constructor(
    private val gattManager: BleGattManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val address: String = checkNotNull(savedStateHandle["address"])

    /** Exposed read-only so the UI can show which peripheral it's talking to. */
    val deviceAddress: String get() = address

    val temperature: StateFlow<Float?> = gattManager.temperature
    //fake test
    //val temperature: StateFlow<Float?> = MutableStateFlow(23.4f)

    val connectionState: StateFlow<ConnectionState> = gattManager.connectionState
    val switchState: StateFlow<SwitchState> = gattManager.switchState

    init {
        gattManager.connect(address)
    }

    fun toggleSwitch(isOn: Boolean) = gattManager.setSwitch(isOn)

    fun setBrightness(percent: Int) = gattManager.setBrightness(percent)

    override fun onCleared() {
        super.onCleared()
        gattManager.disconnect()
    }
}

package com.iwoioapps.smarthomebledemo.ui.scan

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.iwoioapps.smarthomebledemo.ble.BlePermissions
import com.iwoioapps.smarthomebledemo.ble.ScannedDevice
import com.iwoioapps.smarthomebledemo.ui.theme.AppBackground
import com.iwoioapps.smarthomebledemo.ui.theme.AppBackgroundEnd
import com.iwoioapps.smarthomebledemo.ui.theme.Ros
import com.iwoioapps.smarthomebledemo.ui.theme.StatusError
import com.iwoioapps.smarthomebledemo.ui.theme.SurfaceMuted
import com.iwoioapps.smarthomebledemo.ui.theme.Wheat
import com.iwoioapps.smarthomebledemo.ui.theme.Zuzmo


/**
 * First screen: request BLE permissions, scan for nearby devices advertising
 * the demo's custom service UUID, and let the user pick one to connect to.
 */


@Composable
fun ScanScreen(
    onDeviceSelected: (String) -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var permissionsGranted by remember {
        mutableStateOf(
            BlePermissions.required.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result -> permissionsGranted = result.values.all { it } }

    LaunchedEffect(Unit) {
        if (!permissionsGranted) permissionLauncher.launch(BlePermissions.required)
    }

    val devices by viewModel.devices.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AppBackground, AppBackgroundEnd)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())

            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = "BLE SMART HOME",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ros
                )
                Text(
                    text = "Nearby devices",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Wheat
                )
            }

            Button(
                onClick = {
                    when {
                        !permissionsGranted -> permissionLauncher.launch(BlePermissions.required)
                        isScanning -> viewModel.stopScan()
                        else -> viewModel.startScan()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ros,
                    contentColor = AppBackground
                )
            ) {
                Icon(
                    Icons.Filled.BluetoothSearching, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = when {
                        !permissionsGranted -> "Grant Bluetooth permissions"
                        isScanning -> "Stop scanning"
                        else -> "Scan for devices"
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }

            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = StatusError, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(20.dp))

            if (devices.isEmpty() && !isScanning) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No devices found yet. Tap scan and make sure your simulator " +
                            "(e.g. nRF Connect GATT Server) is advertising with the demo's service UUID.",
                        color = Zuzmo,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                LazyColumn {
                    items(devices) { device ->
                        DeviceRow(device = device, onClick = { onDeviceSelected(device.address) })
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceRow(device: ScannedDevice, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceMuted)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Ros.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Bluetooth,
                    contentDescription = null,
                    tint = Ros,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = device.name ?: "Unknown device",
                    color = Wheat,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = device.address,
                    color = Zuzmo,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${device.rssi} dBm",
                color = Zuzmo,
                style = MaterialTheme.typography.labelSmall
            )
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = Zuzmo,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

package com.iwoioapps.smarthomebledemo.ui.smarthome

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iwoioapps.smarthomebledemo.ble.ConnectionState
import com.iwoioapps.smarthomebledemo.ui.theme.AppBackground
import com.iwoioapps.smarthomebledemo.ui.theme.AppBackgroundEnd
import com.iwoioapps.smarthomebledemo.ui.theme.CardDark
import com.iwoioapps.smarthomebledemo.ui.theme.Ros
import com.iwoioapps.smarthomebledemo.ui.theme.StatCardTintA
import com.iwoioapps.smarthomebledemo.ui.theme.StatCardTintB
import com.iwoioapps.smarthomebledemo.ui.theme.StatusError
import com.iwoioapps.smarthomebledemo.ui.theme.StatusPending
import com.iwoioapps.smarthomebledemo.ui.theme.StatusReady
import com.iwoioapps.smarthomebledemo.ui.theme.SurfaceMuted
import com.iwoioapps.smarthomebledemo.ui.theme.Wheat
import com.iwoioapps.smarthomebledemo.ui.theme.Zuzmo

/**
 * Second screen: once connected, this shows a "smart light" panel that reacts
 * live to BLE notifications, plus a switch + slider that write back to the
 * peripheral. Visual language (dark hero card, dusty-rose accent, small stat
 * cards) is a custom "smart home panel" design built for this demo.
 */


@Composable
fun SmartHomeScreen(
    onBack: () -> Unit,
    viewModel: SmartHomeViewModel = hiltViewModel()
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val switchState by viewModel.switchState.collectAsState()
    val isReady = connectionState is ConnectionState.Ready

    val bulbColor by animateColorAsState(
        targetValue = if (switchState.isOn) {
            Ros.copy(alpha = 0.55f + switchState.brightness / 100f * 0.45f)
        } else {
            SurfaceMuted
        },
        animationSpec = tween(300),
        label = "bulbColor"
    )
    val glowColor by animateColorAsState(
        targetValue = if (switchState.isOn) Ros.copy(alpha = 0.35f) else Color.Transparent,
        animationSpec = tween(300),
        label = "glowColor"
    )

    val temperature by viewModel.temperature.collectAsState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(AppBackground, AppBackgroundEnd))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())

            // --- Top bar -------------------------------------------------
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Wheat
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "BLE SMART HOME",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ros
                    )
                    Text(
                        text = connectionStateLabel(connectionState),
                        style = MaterialTheme.typography.titleMedium,
                        color = Wheat,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Utoljára csatlakoztatva: ${viewModel.deviceAddress}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Wheat.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Bluetooth,
                    contentDescription = "Connection status",
                    tint = statusColor(connectionState),
                    modifier = Modifier
                        .size(22.dp)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Hero card: living room light control ---------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(CardDark)
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVING ROOM",
                            style = MaterialTheme.typography.labelSmall,
                            color = Zuzmo
                        )
                        StatusPill(connectionState)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Glowing bulb: a soft radial halo behind a solid circle,
                    // built with layered Boxes (no blur API -> works on minSdk 26).
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(glowColor, Color.Transparent),
                                        center = Offset.Unspecified
                                    ),
                                    shape = CircleShape
                                )
                        )
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(bulbColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = "Light state",
                                modifier = Modifier.size(56.dp),
                                tint = Wheat
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(thickness = 1.dp, color = SurfaceMuted)

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Power switch row --------------------------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Power",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Wheat,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = switchState.isOn,
                            onCheckedChange = { viewModel.toggleSwitch(it) },
                            enabled = isReady,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Wheat,
                                checkedTrackColor = Ros,
                                uncheckedThumbColor = Zuzmo,
                                uncheckedTrackColor = SurfaceMuted
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Brightness slider row ----------------------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Brightness",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Wheat,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${switchState.brightness}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ros,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Slider(
                        value = switchState.brightness.toFloat(),
                        onValueChange = { viewModel.setBrightness(it.toInt()) },
                        valueRange = 0f..100f,
                        enabled = isReady,
                        colors = SliderDefaults.colors(
                            activeTrackColor = Ros,
                            inactiveTrackColor = Zuzmo.copy(alpha = 0.35f),
                            thumbColor = Wheat
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Small stat cards -------------------------------------------
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    tint = StatCardTintA,
                    icon = Icons.Filled.Router,
                    label = "STATUS",
                    value = connectionStateLabel(connectionState),
                    valueColor = statusColor(connectionState)
                )
                Spacer(modifier = Modifier.width(12.dp))
                StatCard(
                    modifier = Modifier.weight(1f),
                    tint = StatCardTintB,
                    icon = Icons.Filled.Bluetooth,
                    label = "DEVICE",
                    value = viewModel.deviceAddress,
                    valueColor = Wheat
                )
            }

            temperature?.let { temp ->
                Spacer(modifier = Modifier.height(12.dp))
                StatCard(
                    modifier = Modifier.fillMaxWidth(),
                    tint = StatCardTintA,
                    icon = Icons.Filled.Thermostat,
                    label = "TEMPERATURE",
                    value = "%.1f°C".format(temp),
                    valueColor = Ros
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatusPill(state: ConnectionState) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(statusColor(state).copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(statusColor(state))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = connectionStateLabel(state),
                style = MaterialTheme.typography.labelSmall,
                color = statusColor(state),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 120.dp)
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    tint: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(tint)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Wheat,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Zuzmo
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun connectionStateLabel(state: ConnectionState): String = when (state) {
    ConnectionState.Disconnected -> "Disconnected"
    ConnectionState.Connecting -> "Connecting…"
    ConnectionState.Connected -> "Connected"
    ConnectionState.DiscoveringServices -> "Discovering services…"
    ConnectionState.Ready -> "Ready"
    is ConnectionState.Failed -> "Error: ${state.message}"
}

private fun statusColor(state: ConnectionState): Color = when (state) {
    ConnectionState.Ready -> StatusReady
    is ConnectionState.Failed -> StatusError
    ConnectionState.Disconnected -> StatusError
    else -> StatusPending
}

package com.nofeed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nofeed.service.FeedBlockerAccessibilityService
import com.nofeed.util.PermissionHelper

class MainActivity : ComponentActivity() {

    override fun onResume() {
        super.onResume()
        setContent { NoFeedApp() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NoFeedApp() }
    }
}

@Composable
fun NoFeedApp() {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences(
            FeedBlockerAccessibilityService.PREFS_NAME,
            android.content.Context.MODE_PRIVATE
        )
    }

    var isEnabled by remember {
        mutableStateOf(prefs.getBoolean(FeedBlockerAccessibilityService.KEY_BLOCKING_ENABLED, false))
    }
    val hasOverlay = PermissionHelper.hasOverlayPermission(context)
    val hasAccessibility = PermissionHelper.isAccessibilityServiceEnabled(context)
    val allGranted = hasOverlay && hasAccessibility

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF0A0A0A),
            surface = Color(0xFF1A1A1A),
            primary = Color.White,
            onPrimary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White,
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(64.dp))

                Text(
                    text = "NoFeed",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Instagram sem feed",
                    fontSize = 14.sp,
                    color = Color(0xFF888888),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(64.dp))

                // Main toggle card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bloquear feed",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            Text(
                                text = if (isEnabled && allGranted) "Ativo" else if (isEnabled) "Faltam permissões" else "Inativo",
                                fontSize = 13.sp,
                                color = when {
                                    isEnabled && allGranted -> Color(0xFF4CAF50)
                                    isEnabled -> Color(0xFFFF9800)
                                    else -> Color(0xFF666666)
                                }
                            )
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { checked ->
                                isEnabled = checked
                                prefs.edit()
                                    .putBoolean(FeedBlockerAccessibilityService.KEY_BLOCKING_ENABLED, checked)
                                    .apply()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = Color.White,
                                uncheckedThumbColor = Color(0xFF888888),
                                uncheckedTrackColor = Color(0xFF333333),
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Permission warnings
                if (!hasOverlay) {
                    PermissionCard(
                        title = "Permissão de sobreposição",
                        description = "Necessária para mostrar o bloqueio sobre o Instagram.",
                        buttonText = "Conceder",
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (!hasAccessibility) {
                    PermissionCard(
                        title = "Serviço de acessibilidade",
                        description = "Necessário para detectar quando o feed está visível.",
                        buttonText = "Ativar",
                        onClick = {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            context.startActivity(intent)
                        }
                    )
                }

                if (allGranted) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1F0D))
                    ) {
                        Text(
                            text = "✓  Tudo configurado",
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF4CAF50),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1400))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⚠  $title",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFFF9800)
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color(0xFF888888),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
                    contentColor = Color.Black
                )
            ) {
                Text(text = buttonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

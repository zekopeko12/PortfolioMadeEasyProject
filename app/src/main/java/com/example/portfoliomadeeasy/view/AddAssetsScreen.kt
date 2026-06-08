package com.example.portfoliomadeeasy.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.portfoliomadeeasy.remote.model.Asset
import com.example.portfoliomadeeasy.viewmodel.AssetsViewModel
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import android.Manifest.permission
import android.widget.Toast

@Composable
fun AssetsScreen(
    navController: NavController,
    assetsViewModel: AssetsViewModel,
) {
    val context = LocalContext.current
    val state by assetsViewModel.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if (!isGranted) {
            Toast.makeText(
                context,
                "Notifikacije su onemogućene. Možete ih uključiti u postavkama.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(permission.POST_NOTIFICATIONS)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {

                if(state.isLoading) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Učitavanje...")
                    }
                }

                else if(state.error != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Greška: ${state.error}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { assetsViewModel.loadAllData() }) {
                            Text("Ponovite")
                        }
                    }
                }
                else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {

                        Button(
                            onClick = {
                                navController.navigate(Routes.SCREEN_PORTFOLIO)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Moj Portfolio")
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Dostupna imovina", fontSize = 20.sp)

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(state.assets.size) { index ->
                                val asset = state.assets[index]
                                AssetItem(asset = asset) { quantity ->
                                    assetsViewModel.buyAsset(asset, quantity)

                                    sendSystemNotification(
                                        context = context,
                                        title = "Uspješna kupnja",
                                        message = "Kupili ste $quantity komada ${asset.name}"
                                    )

                                }
                            }
                        }
                    }
                }
        }
    }

}

@Composable
fun AssetItem(
    asset: Asset,
    onBuy: (quantity: Double) -> Unit
) {
    var quantityInput by remember { mutableStateOf("1") }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column{

                Text(
                    "${asset.name} (${asset.type})",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = asset.price?.let { "$$it" } ?: "N/A",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quantityInput,
                    onValueChange = { quantityInput = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .width(60.dp)
                )

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        val quantity = quantityInput.toDoubleOrNull() ?: 1.0
                        onBuy(quantity)
                    }
                ) {
                    Text("Kupi")
                }
            }

        }
    }
}

fun sendSystemNotification(context: Context, title: String, message: String) {
    val channelId = "portfolio_notifications"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channel = NotificationChannel(
        channelId,
        "Portfolio Updates",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Obavijesti o transakcijama"
        lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
    }
    notificationManager.createNotificationChannel(channel)

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setAutoCancel(true)

    notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
}
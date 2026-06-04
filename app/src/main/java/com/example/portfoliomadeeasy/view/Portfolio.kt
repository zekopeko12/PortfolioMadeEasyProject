package com.example.portfoliomadeeasy.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.portfoliomadeeasy.viewmodel.AssetsViewModel

@Composable
fun Portfolio(
    viewModel: AssetsViewModel,
) {
    val state by viewModel.state.collectAsState()

    var selectedType by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val filteredAssets = state.userAssets.filter { asset ->
        selectedType == null || selectedType == asset.type
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Moj portfolio",
            fontSize = 20.sp
        )

        Box(modifier = Modifier.padding(vertical = 8.dp)) {
            Button(onClick = { expanded = true }) {
                Text(selectedType ?: "Sve vrste imovine")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Sve vrste imovine") },
                    onClick = {
                        selectedType = null
                        expanded = false
                    }
                )

                state.userAssets.map { it.type }.distinct().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedType = type
                            expanded = false
                        }
                    )
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredAssets) { userAsset ->
                val context = androidx.compose.ui.platform.LocalContext.current // Dohvati context za notifikaciju

                UserAssetItem(
                    userAsset = userAsset,
                    onSell = { sellQty ->
                        viewModel.sellAsset(userAsset, sellQty)

                        // Dodajemo sistemsku notifikaciju za prodaju
                        sendSystemNotification(
                            context = context,
                            title = "Imovina prodana",
                            message = "Uspješno ste prodali $sellQty komada ${userAsset.name}"
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val totalSpending = state.userAssets.sumOf { it.buyPrice * it.quantity }
        val totalValue = state.userAssets.sumOf { (it.currentPrice ?: it.buyPrice) * it.quantity }

        Text("Ukupno potrošeno: $${"%.2f".format(totalSpending)}", fontSize = 18.sp)
        Text("Trenutna vrijednost portfolia: $${"%.2f".format(totalValue)}", fontSize = 18.sp)
        Text(
            "Profit/Gubitak: $${"%.2f".format(totalValue - totalSpending)}",
            fontSize = 18.sp,
            color = if (totalValue - totalSpending >= 0) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error
        )
    }
}
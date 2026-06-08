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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.portfoliomadeeasy.model.UserAsset
import com.example.portfoliomadeeasy.viewmodel.AssetsViewModel

@Composable
fun Portfolio(
    viewModel: AssetsViewModel,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    var selectedType by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val filteredAssets = state.userAssets.filter { asset ->
        selectedType == null || selectedType == asset.type
    }

    val totalSpending = state.userAssets.sumOf { it.buyPrice * it.quantity }
    val totalValue = state.userAssets.sumOf { it.totalValue }
    val totalProfitLoss = totalValue - totalSpending
    val totalProfitLossPercent = if (totalSpending > 0) totalProfitLoss / totalSpending * 100 else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {
        item {
            Text(
                "Moj portfolio",
                fontSize = 20.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    label = "Uloženo",
                    value = "$${"%.2f".format(totalSpending)}",
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label = "Trenutna vrijednost",
                    value = "$${"%.2f".format(totalValue)}",
                    modifier = Modifier.weight(1f)
                )

            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (totalProfitLoss >= 0)
                        Color(0xFF1B5E20).copy(alpha = 0.1f)
                    else
                        Color(0xFFB71C1C).copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Profit / Gubitak",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "${"%.2f".format(totalProfitLoss)}$",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (totalProfitLoss >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )

                        Text(
                            text = "${"%.2f".format(totalProfitLossPercent)}%",
                            fontSize = 14.sp,
                            color  = if (totalProfitLoss >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }
        }

        item {
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
        }


        items(filteredAssets) { userAsset ->
            PortfolioAssetItem(
                userAsset = userAsset,
                onSell = { sellQty ->
                    viewModel.sellAsset(userAsset, sellQty)
                    sendSystemNotification(
                        context = context,
                        title = "Imovina prodana",
                        message = "Uspješno ste prodali $sellQty komada ${userAsset.name}"
                    )
                }
            )
        }
    }
}

@Composable
fun SummaryCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun PortfolioAssetItem(
    userAsset: UserAsset,
    onSell: (Double) -> Unit
) {
    var sellAmount by remember { mutableStateOf("") }
    val profitColor = if (userAsset.profitLoss >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(userAsset.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        userAsset.type,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "${"%.2f".format(userAsset.profitLossPercent)}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = profitColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AssetDetailColumn("Količina", "%.4f".format(userAsset.quantity))
                AssetDetailColumn("Kupovna", "$${"%.2f".format(userAsset.buyPrice)}")
                AssetDetailColumn("Trenutna", "$${"%.2f".format(userAsset.currentPrice)}")
                AssetDetailColumn(
                    "P/G",
                    "${"%.2f".format(userAsset.profitLoss)}$",
                    valueColor = profitColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                androidx.compose.material3.OutlinedTextField(
                    value = sellAmount,
                    onValueChange = { sellAmount = it },
                    label = { Text("Količina za prodaju") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
                Button(
                    onClick = {
                        val qty = sellAmount.toDoubleOrNull() ?: 0.0
                        if (qty > 0 && qty <= userAsset.quantity) {
                            onSell(qty)
                            sellAmount = ""
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Prodaj")
                }
            }
        }
    }
}

@Composable
fun AssetDetailColumn(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = valueColor)
    }
}
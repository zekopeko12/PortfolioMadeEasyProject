package com.example.portfoliomadeeasy.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portfoliomadeeasy.model.AssetState
import com.example.portfoliomadeeasy.model.Expense
import com.example.portfoliomadeeasy.model.Income
import com.example.portfoliomadeeasy.model.UserAsset
import com.example.portfoliomadeeasy.remote.model.Asset
import com.example.portfoliomadeeasy.repository.AssetsRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.example.portfoliomadeeasy.view.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date

class AssetsViewModel(
    private val repository: AssetsRepository,
    private val expenseViewModel: ExpenseGoalsViewModel
) : ViewModel() {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val uid: String get() = auth.currentUser?.uid ?: ""

    val assetData = mutableStateListOf<UserAsset>()

    private val _state = MutableStateFlow(AssetState(isLoading = true))
    val state: StateFlow<AssetState> = _state.asStateFlow()

    init {
        loadAllData()
    }

    fun loadAllData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val snapshot = db.collection("asset")
                    .whereEqualTo("userId", uid)
                    .get()
                    .await()

                val userList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(UserAsset::class.java)?.apply {
                        id = doc.id // Ručno dodijeli Firebase Document ID
                    }
                }
                assetData.clear()
                assetData.addAll(userList)

                val marketAssets = repository.getAllAssets()

                _state.value = _state.value.copy(
                    userAssets = userList,
                    assets = marketAssets,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("ASSETS", "Greška: ${e.message}", e)
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun buyAsset(asset: Asset, quantity: Double) {
        val price = asset.price?.let { BigDecimal.valueOf(it) } ?: BigDecimal.ZERO
        val qty = BigDecimal.valueOf(quantity)
        val totalPrice = price.multiply(qty).setScale(2, RoundingMode.HALF_UP).toDouble()

        val existingAssetIndex =
            assetData.indexOfFirst { it.name == asset.name && it.type == asset.type }

        if (existingAssetIndex != -1) {
            val existingAsset = assetData[existingAssetIndex]
            val existingQty = BigDecimal.valueOf(existingAsset.quantity)
            val existingBuyTotal = BigDecimal.valueOf(existingAsset.buyPrice).multiply(existingQty)

            val updatedQuantity = existingQty.add(qty)
            val updatedBuyPrice = existingBuyTotal.add(price.multiply(qty))
                .divide(updatedQuantity, 2, RoundingMode.HALF_UP)
                .toDouble()

            val updatedAsset = existingAsset.copy(
                quantity = updatedQuantity.toDouble(),
                buyPrice = updatedBuyPrice,
                currentPrice = asset.price ?: existingAsset.currentPrice
            )
            updateAssetInDb(updatedAsset)
        } else {
            val newAsset = UserAsset(
                type = asset.type,
                name = asset.name,
                buyPrice = asset.price ?: 0.0,
                quantity = quantity,
                currentPrice = asset.price ?: 0.0,
                userId = uid
            )
            addNewAssetToDb(newAsset)
        }

        expenseViewModel.addExpense(
            Expense(
                title = "Kupovina ${asset.name}",
                amount = totalPrice,
                date = Date(),
                category = Category.ASSET.name,
                userId = uid
            )
        )
    }

    fun sellAsset(userAsset: UserAsset, sellQty: Double) {
        if (sellQty <= 0 || sellQty > userAsset.quantity) return

        val buyPrice = BigDecimal.valueOf(userAsset.buyPrice)
        val sellPrice = BigDecimal.valueOf(userAsset.currentPrice)
        val qty = BigDecimal.valueOf(sellQty)

        val profit = sellPrice.subtract(buyPrice).multiply(qty).setScale(2, RoundingMode.HALF_UP)

        if (profit > BigDecimal.ZERO) {
            expenseViewModel.addIncome(
                Income(
                    title = "Dobit od prodaje: ${userAsset.name}",
                    amount = profit.toDouble(),
                    date = Date(),
                    userId = uid
                )
            )
        }

        val remainingQty = userAsset.quantity - sellQty

        if (remainingQty <= 0) {
            db.collection("asset").document(userAsset.id).delete()
                .addOnSuccessListener { Log.d("FIREBASE", "Uspješno obrisano!") }
                .addOnFailureListener { e -> Log.e("FIREBASE", "Greška pri brisanju", e) }

            assetData.removeAll { it.id == userAsset.id }
        } else {
            val updatedAsset = userAsset.copy(quantity = remainingQty)
            updateAssetInDb(updatedAsset)
        }

        _state.value = _state.value.copy(userAssets = assetData.toList())
    }

    private fun addNewAssetToDb(asset: UserAsset) {
        db.collection("asset").add(asset).addOnSuccessListener { doc ->
            asset.id = doc.id

            assetData.add(asset)
            _state.value = _state.value.copy(
                userAssets = assetData.toList(),
                isLoading = false
            )
        }
    }

    private fun updateAssetInDb(asset: UserAsset) {
        if (asset.id.isEmpty()) return
        db.collection("asset").document(asset.id).set(asset).addOnSuccessListener {
            val index = assetData.indexOfFirst { it.id == asset.id }
            if (index != -1) {
                assetData[index] = asset
                _state.value = _state.value.copy(
                    userAssets = assetData.toList()
                )
            }
        }
    }
}
package com.smartkup.smartkup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkup.smartkup.model.ShoppingListItem
import com.smartkup.smartkup.model.ShoppingListResponseDTO
import com.smartkup.smartkup.repository.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    private val _listData = MutableStateFlow<ShoppingListResponseDTO?>(null)
    val listData: StateFlow<ShoppingListResponseDTO?> = _listData.asStateFlow()

    fun loadList(listId: Long) {
        viewModelScope.launch {
            _listData.value = repository.fetchShoppingList(listId)
        }
    }

    fun toggleItemPurchased(itemId: Long, isPurchased: Boolean) {
        // 1. Instantly update the UI so the app feels lightning fast
        val currentData = _listData.value ?: return
        val updatedItems = currentData.items.map { item ->
            if (item.itemId == itemId) item.copy(purchased = isPurchased) else item
        }
        _listData.value = currentData.copy(items = updatedItems)

        // 2. Launch the network request in the background to save it permanently!
        viewModelScope.launch {
            val success = repository.toggleItemStatus(itemId, isPurchased)
            if (!success) {
                // If the internet disconnected and it failed to save, we should probably
                // revert the checkbox in the UI, but for now we'll just let it be.
            }
        }
    }

    // Updated to accept shopId and a custom string if it's a new product
    fun addNewItem(listId: Long, productId: Long?, customName: String, quantity: Double, unit: String, shopId: Long?) {
        viewModelScope.launch {
            val newItem = ShoppingListItem(
                itemId = 0L,
                listId = listId,
                productId = productId ?: 0L, // 0 means "Backend, please create this!"
                productName = customName,    // Send the custom name
                shopId = shopId,             // Include the store!
                quantity = quantity,
                unit = unit,
                purchased = false
            )

            val success = repository.addItem(newItem)
            if (success) {
                loadList(listId) // Reload to see the new item grouped correctly
            }
        }
    }
}
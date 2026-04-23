package com.smartkup.smartkup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkup.smartkup.model.*
import com.smartkup.smartkup.repository.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    private val _listData = MutableStateFlow<ShoppingListResponseDTO?>(null)
    val listData: StateFlow<ShoppingListResponseDTO?> = _listData.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _shops = MutableStateFlow<List<Shop>>(emptyList())
    val shops: StateFlow<List<Shop>> = _shops.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            _shops.value = repository.fetchShops()
            _products.value = repository.fetchProducts()
            _categories.value = repository.fetchCategories() // <-- ADD THIS
            loadAllLists()
        }
    }

    fun loadList(listId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.fetchShoppingList(listId)

            if (response != null) {
                _listData.value = response
            } else {
                _listData.value = ShoppingListResponseDTO(
                    listDetails = ShoppingListDetails(listId, null, "Nový seznam", "active"),
                    items = emptyList()
                )
            }
            _isLoading.value = false
        }
    }

    fun toggleItemPurchased(itemId: Long, isPurchased: Boolean) {
        val currentData = _listData.value ?: return
        val updatedItems = currentData.items.map { item ->
            if (item.itemId == itemId) item.copy(purchased = isPurchased) else item
        }
        _listData.value = currentData.copy(items = updatedItems)

        viewModelScope.launch {
            repository.toggleItemStatus(itemId, isPurchased)
        }
    }

    fun addNewItem(listId: Long, productId: Long?, customName: String, quantity: Double, unit: String, shopId: Long?, categoryId: Long?) {
        viewModelScope.launch {
            val newItem = ShoppingListItem(
                itemId = 0L,
                listId = listId,
                productId = productId ?: 0L,
                productName = customName,
                categoryId = categoryId, // <-- ADD THIS
                shopId = shopId,
                quantity = quantity,
                unit = unit,
                estimatedPrice = null,
                actualPrice = null,
                purchased = false,
                addedFromPantry = false
            )
            val success = repository.addItem(newItem)
            if (success) loadList(listId)
        }
    }

    private val _overviewLists = MutableStateFlow<List<ShoppingListDetails>>(emptyList())
    val overviewLists: StateFlow<List<ShoppingListDetails>> = _overviewLists.asStateFlow()

    fun loadAllLists() {
        viewModelScope.launch {
            _overviewLists.value = repository.fetchAllLists()
        }
    }
    fun createNewList(name: String) {
        viewModelScope.launch {
            val success = repository.createList(name)
            if (success) {
                // If the DB saved it successfully, re-download the lists to show the new one!
                loadAllLists()
            }
        }
    }
}
package com.smartkup.smartkup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkup.smartkup.model.*
import com.smartkup.smartkup.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PantryItemViewModel(private val repository: CategoryRepository) : ViewModel() {

    private val _items = MutableStateFlow<List<PantryItem>>(emptyList())
    val items: StateFlow<List<PantryItem>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadItemsForCategory(categoryId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _items.value = repository.getPantryItemsByCategory(categoryId)
            } catch (e: Exception) {
                e.printStackTrace()
                _items.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
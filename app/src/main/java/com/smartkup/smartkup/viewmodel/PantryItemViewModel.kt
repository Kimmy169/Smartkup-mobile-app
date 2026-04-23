package com.smartkup.smartkup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkup.smartkup.model.*
import com.smartkup.smartkup.repository.CategoryRepository // Or wherever you put the repo method
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
            _isLoading.value = true // 1. Turn spinner ON
            try {
                // 2. Try to get the data
                _items.value = repository.getPantryItemsByCategory(categoryId)
            } catch (e: Exception) {
                // 3. If a 404 or crash happens, catch it so the app doesn't freeze!
                e.printStackTrace()
                _items.value = emptyList() // Show empty list if error
            } finally {
                // 4. FINALLY block ALWAYS runs, guaranteeing the spinner turns OFF
                _isLoading.value = false
            }
        }
    }
}
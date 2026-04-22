package com.smartkup.smartkup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkup.smartkup.model.Category
import com.smartkup.smartkup.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    // The StateFlow that our Compose UI will observe
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // Automatically fetch data when this ViewModel is created
    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = repository.fetchCategories()
            if (result != null) {
                _categories.value = result
            }
        }
    }
}
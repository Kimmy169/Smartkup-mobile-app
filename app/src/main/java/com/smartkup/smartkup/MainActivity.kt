package com.smartkup.smartkup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartkup.smartkup.network.RetrofitClient
import com.smartkup.smartkup.network.SmartkupApi
import com.smartkup.smartkup.repository.CategoryRepository
import com.smartkup.smartkup.repository.ShoppingListRepository
import com.smartkup.smartkup.ui.AppNavigation
import com.smartkup.smartkup.ui.theme.SmartkupTheme
import com.smartkup.smartkup.viewmodel.CategoryViewModel
import com.smartkup.smartkup.viewmodel.PantryItemViewModel
import com.smartkup.smartkup.viewmodel.ShoppingListViewModel

class ShoppingListViewModelFactory(private val repository: ShoppingListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T { return ShoppingListViewModel(repository) as T }
}

class CategoryViewModelFactory(private val repository: CategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T { return CategoryViewModel(repository) as T }
}

class PantryItemViewModelFactory(private val repository: CategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T { return PantryItemViewModel(repository) as T }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = RetrofitClient.instance.create(SmartkupApi::class.java)

        val shoppingRepository = ShoppingListRepository(api)
        val categoryRepository = CategoryRepository(api)

        setContent {
            val shoppingViewModel: ShoppingListViewModel =
                viewModel(factory = ShoppingListViewModelFactory(shoppingRepository))
            val categoryViewModel: CategoryViewModel =
                viewModel(factory = CategoryViewModelFactory(categoryRepository))
            val pantryItemViewModel: PantryItemViewModel =
                viewModel(factory = PantryItemViewModelFactory(categoryRepository))

            val systemTheme = isSystemInDarkTheme()

            var isDarkTheme by remember { mutableStateOf(systemTheme) }

            SmartkupTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        shoppingListViewModel = shoppingViewModel,
                        categoryViewModel = categoryViewModel,
                        pantryItemViewModel = pantryItemViewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDarkTheme = it }
                    )
                }
            }
        }
    }
}
package com.smartkup.smartkup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smartkup.smartkup.network.RetrofitClient
import com.smartkup.smartkup.network.SmartkupApi
import com.smartkup.smartkup.repository.ShoppingListRepository
import com.smartkup.smartkup.ui.AppNavigation
import com.smartkup.smartkup.viewmodel.ShoppingListViewModel

// 1. We create a "Factory" that tells Android how to build your ViewModel
class ShoppingListViewModelFactory(private val repository: ShoppingListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = RetrofitClient.instance.create(SmartkupApi::class.java)
        val repository = ShoppingListRepository(api)

        // 2. We use the Factory to ask Android for the ViewModel.
        // Now, if you rotate the screen, Android hands you back the EXACT SAME ViewModel!
        val factory = ShoppingListViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[ShoppingListViewModel::class.java]

        setContent {
            AppNavigation(shoppingListViewModel = viewModel)
        }
    }
}
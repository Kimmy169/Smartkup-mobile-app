package com.smartkup.smartkup.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// This creates a strict blueprint for our 4 tabs
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Shopping : BottomNavItem("shopping", "Nákupy", Icons.Filled.ShoppingCart)
    object Recipes : BottomNavItem("recipes", "Recepty", Icons.AutoMirrored.Filled.MenuBook)
    object Pantry : BottomNavItem("pantry", "Spíž", Icons.Filled.Kitchen)
    object Profile : BottomNavItem("profile", "Profil", Icons.Filled.Person)
}
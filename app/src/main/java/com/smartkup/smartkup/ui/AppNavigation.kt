package com.smartkup.smartkup.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.smartkup.smartkup.viewmodel.CategoryViewModel
import com.smartkup.smartkup.viewmodel.PantryItemViewModel
import com.smartkup.smartkup.viewmodel.ShoppingListViewModel

@Composable
fun AppNavigation(
    shoppingListViewModel: ShoppingListViewModel,
    categoryViewModel: CategoryViewModel,
    pantryItemViewModel: PantryItemViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    val tabs = listOf(
        BottomNavItem.Shopping,
        BottomNavItem.Recipes,
        BottomNavItem.Pantry,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val showBottomBar = tabs.any { it.route == currentRoute }

            if (showBottomBar) {
                NavigationBar {
                    val currentDestination = navBackStackEntry?.destination
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.title) },
                            label = { Text(tab.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Shopping.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Shopping.route) {
                ShoppingListsOverviewScreen(
                    viewModel = shoppingListViewModel,
                    onNavigateToList = { listId -> navController.navigate("shopping_list_detail/$listId") }
                )
            }

            composable(
                route = "shopping_list_detail/{listId}",
                arguments = listOf(navArgument("listId") { type = NavType.LongType })
            ) { backStackEntry ->
                val listId = backStackEntry.arguments?.getLong("listId") ?: 1L
                LaunchedEffect(listId) { shoppingListViewModel.loadList(listId) }
                ShoppingListScreen(viewModel = shoppingListViewModel, onNavigateBack = { navController.popBackStack() })
            }

            composable(BottomNavItem.Recipes.route) { RecipesScreen() }

            composable(BottomNavItem.Pantry.route) {
                PantryScreen(
                    viewModel = categoryViewModel,
                    onCategoryClick = { categoryId, categoryName ->
                        navController.navigate("category_detail/$categoryId/$categoryName")
                    }
                )
            }

            composable(
                route = "category_detail/{categoryId}/{categoryName}",
                arguments = listOf(
                    navArgument("categoryId") { type = NavType.LongType },
                    navArgument("categoryName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: 0L
                val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Kategorie"

                CategoryDetailScreen(
                    categoryId = categoryId,
                    categoryName = categoryName,
                    viewModel = pantryItemViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ==========================================
            // UPDATED: Profile Screen now routes to UserScreen
            // ==========================================
            composable(BottomNavItem.Profile.route) {
                UserScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
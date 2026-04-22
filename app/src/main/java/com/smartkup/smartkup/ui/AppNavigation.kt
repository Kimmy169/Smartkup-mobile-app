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
import com.smartkup.smartkup.viewmodel.ShoppingListViewModel

@Composable
fun AppNavigation(shoppingListViewModel: ShoppingListViewModel) {
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

            // Logic: Only show bottom bar on the 4 main root tabs
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
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
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
            // TAB 1: SHOPPING
            composable(BottomNavItem.Shopping.route) {
                ShoppingListsOverviewScreen(
                    onNavigateToList = { listId ->
                        navController.navigate("shopping_list_detail/$listId")
                    }
                )
            }

            // NESTED DETAIL: This is where we go when a list is clicked
            composable(
                route = "shopping_list_detail/{listId}",
                arguments = listOf(navArgument("listId") { type = NavType.LongType })
            ) { backStackEntry ->
                val listId = backStackEntry.arguments?.getLong("listId") ?: 1L

                // Load data as soon as the screen opens
                LaunchedEffect(listId) {
                    shoppingListViewModel.loadList(listId)
                }

                ShoppingListScreen(
                    viewModel = shoppingListViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // TAB 2: RECIPES
            composable(BottomNavItem.Recipes.route) {
                RecipesScreen()
            }

            // TAB 3: PANTRY
            composable(BottomNavItem.Pantry.route) {
                PantryScreen()
            }

            // TAB 4: PROFILE
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
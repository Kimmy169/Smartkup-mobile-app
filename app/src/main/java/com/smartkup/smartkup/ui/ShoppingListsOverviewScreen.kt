package com.smartkup.smartkup.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListsOverviewScreen(onNavigateToList: (Long) -> Unit) {
    // A temporary list of shopping lists (Later, we will fetch this from the ViewModel/Database!)
    // We start with your default list.
    var shoppingLists by remember {
        mutableStateOf(listOf(Pair(1L, "Týdenní nákup")))
    }

    // State for the popup dialog
    var showAddListDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moje Nákupy", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        // NEW: The button to add a new shopping list
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddListDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nový nákup", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Loop through our shopping lists and create a card for each one
            shoppingLists.forEach { (listId, listName) ->
                ElevatedCard(
                    onClick = { onNavigateToList(listId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Cart",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(listName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Kliknutím zobrazíte položky", fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // NEW: The Dialog to create a list
        if (showAddListDialog) {
            var newListName by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddListDialog = false },
                title = { Text("Nový nákupní seznam") },
                text = {
                    OutlinedTextField(
                        value = newListName,
                        onValueChange = { newListName = it },
                        label = { Text("Název seznamu (např. Grilovačka)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newListName.isNotBlank()) {
                                // Generate a fake ID for now (e.g., 2, 3, 4)
                                val newListId = (shoppingLists.maxOfOrNull { it.first } ?: 0L) + 1L
                                shoppingLists = shoppingLists + Pair(newListId, newListName)
                                showAddListDialog = false
                            }
                        },
                        enabled = newListName.isNotBlank()
                    ) {
                        Text("Vytvořit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddListDialog = false }) {
                        Text("Zrušit")
                    }
                }
            )
        }
    }
}
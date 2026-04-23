package com.smartkup.smartkup.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.smartkup.smartkup.viewmodel.ShoppingListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListsOverviewScreen(
    viewModel: ShoppingListViewModel,
    onNavigateToList: (Long) -> Unit
) {
    // Fetch the real lists from the database via ViewModel!
    val shoppingLists by viewModel.overviewLists.collectAsState()
    var showAddListDialog by remember { mutableStateOf(false) }

    // Optional: Refresh lists when screen appears
    LaunchedEffect(Unit) { viewModel.loadAllLists() }

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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddListDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nový nákup", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->

        // If empty, the screen is clean. Otherwise, show the grid!
        if (shoppingLists.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(shoppingLists) { list ->
                    ElevatedCard(
                        onClick = { onNavigateToList(list.listId) }, // Use the REAL Database ID
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = list.name, fontSize = 18.sp, fontWeight = FontWeight.Bold) // Use REAL Database Name
                        }
                    }
                }
            }
        }

        if (showAddListDialog) {
            var newListName by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddListDialog = false },
                title = { Text("Nový nákupní seznam") },
                text = {
                    OutlinedTextField(
                        value = newListName,
                        onValueChange = { newListName = it },
                        label = { Text("Název seznamu (např. Oslava)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newListName.isNotBlank()) {
                                viewModel.createNewList(newListName)
                                showAddListDialog = false
                            }
                        },
                        enabled = newListName.isNotBlank()
                    ) { Text("Vytvořit") }
                },
                dismissButton = { TextButton(onClick = { showAddListDialog = false }) { Text("Zrušit") } }
            )
        }
    }
}
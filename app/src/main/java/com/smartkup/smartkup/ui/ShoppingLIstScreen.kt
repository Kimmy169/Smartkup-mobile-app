package com.smartkup.smartkup.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartkup.smartkup.model.ShoppingListItem
import com.smartkup.smartkup.viewmodel.ShoppingListViewModel

// Hardcoded for now - we will fetch these from DB in the next phase!
val availableShops = mapOf(
    1L to "Albert",
    2L to "Lidl",
    3L to "Tesco",
    0L to "Anywhere"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ShoppingListScreen(viewModel: ShoppingListViewModel, onNavigateBack: () -> Unit) {
    val listData by viewModel.listData.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(listData?.listDetails?.name ?: "Načítání...", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            // The FAB appears once the list is loaded
            if (listData != null) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Přidat položku")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // This is the fix for the content being cut off
        ) {
            if (listData == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                val groupedItems = listData!!.items.groupBy { it.shopId ?: 0L }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    groupedItems.forEach { (shopId, itemsInShop) ->
                        stickyHeader {
                            val shopName = availableShops[shopId] ?: "Neznámý obchod"
                            Text(
                                text = shopName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(vertical = 8.dp)
                            )
                        }

                        items(itemsInShop) { item ->
                            ShoppingItemRow(item) { isChecked ->
                                viewModel.toggleItemPurchased(item.itemId, isChecked)
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog && listData != null) {
            AddItemDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { pId, customName, qty, unit, shopId ->
                    viewModel.addNewItem(listData!!.listDetails.listId, pId, customName, qty, unit, shopId)
                    showAddDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(onDismiss: () -> Unit, onConfirm: (Long?, String, Double, String, Long) -> Unit) {
    data class ProductOption(val id: Long, val name: String, val defaultUnit: String)
    val dbProducts = listOf(
        ProductOption(1L, "Mléko plnotučné", "l"),
        ProductOption(2L, "Rohlík bílý", "ks"),
        ProductOption(3L, "Brambory", "kg")
    )

    var productText by remember { mutableStateOf("") }
    var selectedProductId by remember { mutableStateOf<Long?>(null) }
    var productExpanded by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("ks") }
    var selectedShopId by remember { mutableStateOf(0L) }
    var shopExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Přidat na seznam") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = productExpanded,
                    onExpandedChange = { productExpanded = !productExpanded }
                ) {
                    OutlinedTextField(
                        value = productText,
                        onValueChange = {
                            productText = it
                            selectedProductId = null
                            productExpanded = true
                        },
                        label = { Text("Název produktu") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        singleLine = true
                    )

                    val filtered = dbProducts.filter { it.name.contains(productText, ignoreCase = true) }
                    if (filtered.isNotEmpty() && productText.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = productExpanded,
                            onDismissRequest = { productExpanded = false }
                        ) {
                            filtered.forEach { product ->
                                DropdownMenuItem(
                                    text = { Text(product.name) },
                                    onClick = {
                                        productText = product.name
                                        selectedProductId = product.id
                                        unit = product.defaultUnit
                                        productExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) quantity = it },
                        label = { Text("Množství") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Jednotka") },
                        modifier = Modifier.weight(1f)
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = shopExpanded,
                    onExpandedChange = { shopExpanded = !shopExpanded }
                ) {
                    OutlinedTextField(
                        value = availableShops[selectedShopId] ?: "Kdekoliv",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Obchod") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shopExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = shopExpanded, onDismissRequest = { shopExpanded = false }) {
                        availableShops.forEach { (id, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    selectedShopId = id
                                    shopExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedProductId, productText, quantity.toDoubleOrNull() ?: 1.0, unit, selectedShopId) },
                enabled = productText.isNotBlank() && quantity.isNotBlank()
            ) { Text("Uložit") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Zrušit") } }
    )
}

@Composable
fun ShoppingItemRow(item: ShoppingListItem, onCheckedChange: (Boolean) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = item.purchased, onCheckedChange = onCheckedChange)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = item.productName ?: "Neznámý produkt",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (item.purchased) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (item.purchased) Color.Gray else Color.Unspecified
                )
                Text(text = "${item.quantity} ${item.unit ?: "ks"}", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
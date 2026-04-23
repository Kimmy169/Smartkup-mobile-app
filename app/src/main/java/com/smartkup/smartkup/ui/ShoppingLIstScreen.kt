package com.smartkup.smartkup.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartkup.smartkup.model.*
import com.smartkup.smartkup.viewmodel.ShoppingListViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ShoppingListScreen(viewModel: ShoppingListViewModel, onNavigateBack: () -> Unit) {
    val listData by viewModel.listData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val realShops by viewModel.shops.collectAsState()
    val realProducts by viewModel.products.collectAsState()
    val realCategories by viewModel.categories.collectAsState()

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
            if (!isLoading) {
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (listData == null || listData?.items.isNullOrEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tento seznam je prázdný.", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                    Text("Klikněte na + pro přidání položek.", fontSize = 14.sp, color = Color.Gray)
                }
            } else {
                val groupedItems = listData?.items?.groupBy { it.shopId ?: 0L } ?: emptyMap()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    groupedItems.forEach { (shopId, itemsInShop) ->
                        stickyHeader {
                            val shopName = realShops.find { it.shopId == shopId }?.name ?: "Kdekoliv"
                            Text(
                                text = shopName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(vertical = 8.dp)
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
                dbProducts = realProducts,
                dbShops = realShops,
                dbCategories = realCategories,
                onDismiss = { showAddDialog = false },
                onConfirm = { pId, name, qty, unit, sId, cId, prc ->
                    listData?.listDetails?.listId?.let { id ->
                        viewModel.addNewItem(id, pId, name, qty, unit, sId, cId, prc)
                    }
                    showAddDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(
    dbProducts: List<Product>,
    dbShops: List<Shop>,
    dbCategories: List<Category>,
    onDismiss: () -> Unit,
    onConfirm: (Long?, String, Double, String, Long?, Long?, Double?) -> Unit
) {
    var productText by remember { mutableStateOf("") }
    var selectedProductId by remember { mutableStateOf<Long?>(null) }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }

    var selectedShopId by remember { mutableStateOf<Long?>(null) }
    var shopExpanded by remember { mutableStateOf(false) }

    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Přidat na seznam") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 1. PRODUCT NAME
                OutlinedTextField(
                    value = productText,
                    onValueChange = {
                        productText = it
                        selectedProductId = null
                    },
                    label = { Text("Název produktu") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Suggestions
                val filtered = dbProducts.filter { it.name.contains(productText, ignoreCase = true) }
                if (filtered.isNotEmpty() && productText.isNotEmpty() && selectedProductId == null) {
                    ElevatedCard(modifier = Modifier.fillMaxWidth().heightIn(max = 140.dp)) {
                        LazyColumn {
                            items(filtered) { product ->
                                DropdownMenuItem(
                                    text = { Text(product.name) },
                                    onClick = {
                                        productText = product.name
                                        selectedProductId = product.productId
                                        unit = product.defaultUnit ?: ""
                                        selectedCategoryId = product.categoryId
                                        priceText = product.price?.toString() ?: ""
                                    }
                                )
                            }
                        }
                    }
                }

                // 2. CATEGORY
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    val currentCatName = dbCategories.find { it.categoryId == selectedCategoryId }?.name ?: "Vyberte kategorii"
                    OutlinedTextField(
                        value = currentCatName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategorie") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        dbCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategoryId = category.categoryId
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // 3. QUANTITY & UNIT
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) quantity = it },
                        label = { Text("Množství") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Jednotka") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // 4. PRICE (New!)
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) priceText = it },
                    label = { Text("Cena za jednotku (Kč)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                // 5. SHOP SELECTOR
                ExposedDropdownMenuBox(
                    expanded = shopExpanded,
                    onExpandedChange = { shopExpanded = !shopExpanded }
                ) {
                    val currentShopName = dbShops.find { it.shopId == selectedShopId }?.name ?: "Vyberte obchod (volitelné)"
                    OutlinedTextField(
                        value = currentShopName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Obchod") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shopExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = shopExpanded, onDismissRequest = { shopExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Žádný obchod") },
                            onClick = {
                                selectedShopId = null
                                shopExpanded = false
                            }
                        )
                        dbShops.forEach { shop ->
                            DropdownMenuItem(
                                text = { Text(shop.name) },
                                onClick = {
                                    selectedShopId = shop.shopId
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
                onClick = {
                    onConfirm(
                        selectedProductId,
                        productText,
                        quantity.toDoubleOrNull() ?: 1.0,
                        unit,
                        selectedShopId,
                        selectedCategoryId,
                        priceText.toDoubleOrNull()
                    )
                },
                enabled = productText.isNotBlank() && quantity.isNotBlank() && selectedCategoryId != null
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName ?: "Neznámý produkt",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (item.purchased) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(text = "${item.quantity} ${item.unit ?: "ks"}", fontSize = 14.sp, color = Color.Gray)
            }
            // Display price on the right
            if (item.productPrice != null && item.productPrice > 0) {
                Text(
                    text = "${"%.2f".format(item.productPrice * item.quantity)} Kč",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
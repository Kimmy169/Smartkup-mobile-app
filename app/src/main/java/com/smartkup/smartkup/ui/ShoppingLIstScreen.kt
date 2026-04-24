package com.smartkup.smartkup.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Přidat")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(listData?.items ?: emptyList()) { item ->
                        ShoppingItemRow(item) { isChecked ->
                            viewModel.toggleItemPurchased(item.itemId, isChecked)
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
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
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d*$"""))) quantity = it },
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

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d*$"""))) priceText = it },
                    label = { Text("Cena za jednotku (Kč)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

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
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
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
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = item.purchased, onCheckedChange = onCheckedChange)
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.productName ?: "", fontWeight = FontWeight.Bold)
            Text(text = "${item.quantity} ${item.unit ?: ""}", color = Color.Gray)
        }
        if (item.productPrice != null) {
            Text(text = "${"%.2f".format(item.productPrice * item.quantity)} Kč", fontWeight = FontWeight.Bold)
        }
    }
}
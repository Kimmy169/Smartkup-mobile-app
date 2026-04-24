package com.smartkup.smartkup.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Person

@Composable
fun RecipesScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Recepty", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ElevatedCard(modifier = Modifier.weight(1f).height(120.dp)) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Rychlé večeře") } }
            ElevatedCard(modifier = Modifier.weight(1f).height(120.dp)) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Zdravé obědy") } }
        }
    }
}
@Composable
fun ProfileScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(imageVector = androidx.compose.material.icons.Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Uživatelský Profil", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Nastavení a správa účtu", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
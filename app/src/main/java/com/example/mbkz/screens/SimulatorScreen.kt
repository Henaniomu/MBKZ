package com.example.mbkz.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SimulatorScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "⚙️ Симулятор команд", fontSize = 24.sp)
        Text(text = "Тут можна буде вводити команди та тестувати їх.", fontSize = 16.sp)
    }
}

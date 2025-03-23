package com.example.mbkz.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NetworkTrainerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🖥 Мережевий тренажер", fontSize = 24.sp)
        Text(text = "Тут буде інтерактивний тренажер для мереж.", fontSize = 16.sp)
    }
}

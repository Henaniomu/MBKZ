package com.example.mbkz

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mbkz.screens.GuideScreen
import com.example.mbkz.screens.NetworkTrainerScreen
import com.example.mbkz.screens.SimulatorScreen
import com.example.mbkz.screens.TestScreen
import com.example.mbkz.ui.theme.MBKZTheme

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideSystemUI()

        setContent {
            val currentLanguage = remember { mutableStateOf(LanguageManager.getSavedLanguage(this)) }
            val translations = remember { mutableStateOf(LanguageManager.loadLanguage(this, currentLanguage.value)) }

            MBKZTheme {
                AppNavigation(translations.value, this) { newLang ->
                    LanguageManager.saveLanguagePreference(this, newLang)
                    translations.value = LanguageManager.loadLanguage(this, newLang)
                    currentLanguage.value = newLang
                }
            }
        }
    }

    private fun hideSystemUI() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            view.systemUiVisibility =
                (android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                        or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            insets
        }
    }
}

@Composable
fun AppNavigation(translations: Map<String, String>, context: Context, onLanguageChange: (String) -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainMenuScreen(translations, navController, onLanguageChange) }
        composable("guide") { GuideScreen(context) }
        composable("tests") { TestScreen(navController, context) }
        composable("simulator") { SimulatorScreen() }
        composable("network") { NetworkTrainerScreen() }
    }
}

@Composable
fun MainMenuScreen(translations: Map<String, String>, navController: NavController, onLanguageChange: (String) -> Unit) {
    Box {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                Text(translations["title"] ?: "App", fontSize = 24.sp, color = Color.Magenta)

                Spacer(modifier = Modifier.height(24.dp))

                MenuButton(translations["guide"] ?: "Guide") { navController.navigate("guide") }
                MenuButton(translations["tests"] ?: "Tests") { navController.navigate("tests") }
//                MenuButton(translations["simulator"] ?: "Simulator") { navController.navigate("simulator") }
//                MenuButton(translations["network_trainer"] ?: "Network Trainer") { navController.navigate("network") }

                Spacer(modifier = Modifier.height(32.dp))

                LanguageButton(translations["select_language"] ?: "Change Language", onLanguageChange)
            }
        }
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text, fontSize = 18.sp)
    }
}

@Composable
fun LanguageButton(label: String, onLanguageChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf(
        "uk" to "Українська",
        "en" to "English",
        "cs" to "Čeština"
    )

    Box(modifier = Modifier.wrapContentSize(Alignment.Center)) {
        Button(onClick = { expanded = true }) {
            Text(label)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            languages.forEach { (code, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        expanded = false
                        onLanguageChange(code)
                    }
                )
            }
        }
    }
}

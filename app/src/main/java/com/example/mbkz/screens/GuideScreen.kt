package com.example.mbkz.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mbkz.LanguageManager
import org.json.JSONArray
import kotlin.math.abs

@Composable
fun GuideScreen(context: Context) {
    val currentLanguage = LanguageManager.getSavedLanguage(context)
    val translations = LanguageManager.loadLanguage(context, currentLanguage)

    val flashcards = remember { mutableStateOf(loadFlashcards(translations)) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (flashcards.value.isNotEmpty()) {
            Flashcard(
                term = flashcards.value[currentIndex].first,
                definition = flashcards.value[currentIndex].second,
                isFlipped = isFlipped,
                onFlip = { isFlipped = !isFlipped },
                onSwipe = { direction ->
                    if (direction != 0) {
                        currentIndex = (currentIndex + 1) % flashcards.value.size
                        isFlipped = false
                    }
                }
            )
        } else {
            Text("Немає доступних карток", color = Color.White, fontSize = 20.sp)
        }
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun Flashcard(
    term: String,
    definition: String,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onSwipe: (Int) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(targetValue = offsetX, animationSpec = tween(200), label = "")
    val rotation by animateFloatAsState(targetValue = if (isFlipped) 180f else 0f, animationSpec = tween(300), label = "")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp))
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .scale(1f)
            .offset(x = animatedOffsetX.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (abs(offsetX) > 100) {
                            onSwipe(if (offsetX > 0) 1 else -1)
                        }
                        offsetX = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX += (dragAmount * 0.5).toFloat()
                    }
                )
            }

            .pointerInput(Unit) {
                detectTapGestures(onTap = { onFlip() })
            },
        contentAlignment = Alignment.Center
    ) {
        if (rotation < 90f) {
            Text(
                text = term,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Text(
                text = definition,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

fun loadFlashcards(translations: Map<String, String>): List<Pair<String, String>> {
    return try {
        val jsonArray = JSONArray(translations["flashcards"])
        (0 until jsonArray.length()).map { i ->
            val obj = jsonArray.getJSONObject(i)
            obj.getString("term") to obj.getString("definition")
        }.shuffled()
    } catch (e: Exception) {
        emptyList()
    }
}

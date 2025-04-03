package com.example.mbkz.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mbkz.LanguageManager
import kotlinx.coroutines.delay
import org.json.JSONArray

@Composable
fun TestScreen(navController: NavController, context: Context) {
    val currentLanguage = LanguageManager.getSavedLanguage(context)
    val translations = LanguageManager.loadLanguage(context, currentLanguage)

    var questionCount by remember { mutableStateOf(10) }
    var optionCount by remember { mutableStateOf(4) }
    var testStarted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (!testStarted) {
            TestSettingsDialog(
                onDismiss = { navController.popBackStack() },
                onStartTest = { qCount, oCount ->
                    questionCount = qCount
                    optionCount = oCount
                    testStarted = true
                },
                translations = translations
            )
        } else {
            StartTestScreen(questionCount, optionCount, translations, navController)
        }
    }
}

@Composable
fun StartTestScreen(
    questionCount: Int,
    optionCount: Int,
    translations: Map<String, String>,
    navController: NavController
) {
    val allQuestions = remember { mutableStateOf(loadTestQuestions(translations)) }
    val selectedQuestions = remember { mutableStateOf(allQuestions.value.shuffled().take(questionCount)) }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var showResultDialog by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var answerState by remember { mutableStateOf<AnswerState?>(null) }

    var backgroundColor by remember { mutableStateOf(Color.Black) }

    val isInteractionBlocked = answerState != null

    LaunchedEffect(answerState) {
        if (answerState != null) {
            backgroundColor = when (answerState) {
                AnswerState.CORRECT -> Color(0xFF2E7D32)
                AnswerState.INCORRECT -> Color(0xFFC62828)
                null -> TODO()
            }
            delay(500)
            backgroundColor = Color.Black
            if (currentQuestionIndex + 1 < questionCount) {
                currentQuestionIndex++
                answerState = null
            } else {
                showResultDialog = true
            }
        }
    }

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text(text = translations["test_result"] ?: "Test Result") },
            text = { Text(text = "${translations["your_score"] ?: "Your Score"}: $score / $questionCount") },
            confirmButton = {
                Button(onClick = { navController.popBackStack() }) {
                    Text(text = translations["back_to_menu"] ?: "Back to Menu")
                }
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val question = selectedQuestions.value[currentQuestionIndex]
                val rawOptions = question["options"] as List<*>
                val correctAnswer = question["correct_answer"] as String

                val incorrectOptions = rawOptions.shuffled().take(optionCount - 1)

                val options = (incorrectOptions + correctAnswer).shuffled()

                Text(text = question["question"] as String, fontSize = 20.sp, color = Color.White)

                options.forEach { option ->
                    Button(
                        onClick = {
                            answerState = if (option == question["correct_answer"]) {
                                score++
                                AnswerState.CORRECT
                            } else {
                                AnswerState.INCORRECT
                            }
                        },
                        enabled = !isInteractionBlocked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = option as String)
                    }
                }
            }
        }
    }
}

@Composable
fun TestSettingsDialog(
    onDismiss: () -> Unit,
    onStartTest: (Int, Int) -> Unit,
    translations: Map<String, String>
) {

    val allQuestions = remember { loadTestQuestions(translations) }
    val totalAvailableQuestions = allQuestions.size.coerceAtLeast(10)

    var questionCount by remember { mutableStateOf(10.coerceAtMost(totalAvailableQuestions)) }
    var optionCount by remember { mutableStateOf(4) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = translations["test_settings"] ?: "Test Settings") },
        text = {
            Column {
                Text(text = translations["select_question_count"] ?: "Select number of questions")
                Text(
                    text = "$questionCount / $totalAvailableQuestions",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Slider(
                    value = questionCount.toFloat(),
                    onValueChange = { questionCount = it.toInt() },
                    valueRange = 10f..totalAvailableQuestions.toFloat(),
                    steps = totalAvailableQuestions - 10
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = translations["select_option_count"] ?: "Select number of options")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (2..4).forEach { count ->
                        Button(
                            onClick = { optionCount = count },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (optionCount == count) Color.Blue else Color.Gray
                            )
                        ) {
                            Text(text = "$count")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onStartTest(questionCount, optionCount) }
            ) {
                Text(text = translations["start_test"] ?: "Start Test")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = translations["cancel"] ?: "Cancel")
            }
        }
    )
}

fun loadTestQuestions(translations: Map<String, String>): List<Map<String, Any>> {
    return try {
        val jsonArray = JSONArray(translations["questions"])
        (0 until jsonArray.length()).map { i ->
            val obj = jsonArray.getJSONObject(i)
            mapOf(
                "question" to obj.getString("question"),
                "correct_answer" to obj.getString("correct_answer"),
                "options" to List(obj.getJSONArray("options").length()) { index ->
                    obj.getJSONArray("options").getString(index)
                }
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

enum class AnswerState { CORRECT, INCORRECT }

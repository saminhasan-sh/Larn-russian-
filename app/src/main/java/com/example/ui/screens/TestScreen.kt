package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.QuizMode

@Composable
fun TestScreen(
    viewModel: MainViewModel,
    onNavigateToWrongWords: () -> Unit
) {
    val context = LocalContext.current
    val quizSession by viewModel.quizSession.collectAsState()
    val allVocab by viewModel.allVocab.collectAsState()
    val selectedQuizMode by viewModel.selectedQuizMode.collectAsState()
    val questionCountInput by viewModel.quizQuestionCountInput.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!quizSession.isActive) {
            // Setup Mode Screen
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Russian Vocabulary Test",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        )

                        Text(
                            text = "Test your vocabulary knowledge with random questions & immediate scoring.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                        )

                        // Mode Selector Chips
                        Text(
                            text = "Select Quiz Mode",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedQuizMode == QuizMode.RU_TO_BN,
                                onClick = { viewModel.selectedQuizMode.value = QuizMode.RU_TO_BN },
                                label = { Text("Mode 1: Russian → Bengali") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quiz_mode_ru_bn")
                            )

                            FilterChip(
                                selected = selectedQuizMode == QuizMode.BN_TO_RU,
                                onClick = { viewModel.selectedQuizMode.value = QuizMode.BN_TO_RU },
                                label = { Text("Mode 2: Bengali → Russian") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quiz_mode_bn_ru")
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Number of Questions Input
                        OutlinedTextField(
                            value = questionCountInput,
                            onValueChange = { viewModel.quizQuestionCountInput.value = it },
                            label = { Text("Number of Questions") },
                            placeholder = { Text("e.g. 10 or 30") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("question_count_input")
                        )

                        Text(
                            text = "Available words in storage: ${allVocab.size}. If requested > available, all words will be used.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 4.dp, bottom = 20.dp)
                        )

                        Button(
                            onClick = {
                                if (allVocab.isEmpty()) {
                                    Toast.makeText(context, "Please add some vocabulary words first!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.startQuiz()
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("start_test_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Start Test", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else if (quizSession.isFinished) {
            // Quiz Results Summary Screen
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val accuracy = if (quizSession.questions.isNotEmpty()) {
                            (quizSession.correctAnswersCount.toFloat() / quizSession.questions.size.toFloat()) * 100f
                        } else 0f

                        Text(
                            text = "Quiz Completed! 🎉",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Score Grid Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ScoreSummaryBox(
                                label = "Total",
                                value = "${quizSession.questions.size}",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            ScoreSummaryBox(
                                label = "Correct",
                                value = "${quizSession.correctAnswersCount}",
                                color = Color(0xFF10B981),
                                modifier = Modifier.weight(1f)
                            )
                            ScoreSummaryBox(
                                label = "Wrong",
                                value = "${quizSession.wrongAnswersCount}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Accuracy Percentage",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${String.format("%.1f", accuracy)}%",
                                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.startQuiz() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Retake Test")
                            }

                            if (quizSession.wrongWordsInThisSession.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = onNavigateToWrongWords,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Review Wrong Words")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { viewModel.resetQuiz() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Change Test Settings")
                        }
                    }
                }
            }
        } else {
            // Active Question Interface
            val currentQuestion = quizSession.questions[quizSession.currentIndex]
            val total = quizSession.questions.size
            val progress = (quizSession.currentIndex + 1).toFloat() / total.toFloat()

            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_active_card")
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Header Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question ${quizSession.currentIndex + 1} of $total",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = if (quizSession.mode == QuizMode.RU_TO_BN) "RU → BN" else "BN → RU",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Question Prompt Card
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val questionText = when (quizSession.mode) {
                                    QuizMode.RU_TO_BN -> currentQuestion.russianWord
                                    QuizMode.BN_TO_RU -> currentQuestion.bengaliMeaning
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = questionText,
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 26.sp
                                        ),
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )

                                    if (quizSession.mode == QuizMode.RU_TO_BN) {
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary)
                                                .clickable { viewModel.speakRussian(currentQuestion.russianWord) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Pronounce Russian Word",
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = when (quizSession.mode) {
                                        QuizMode.RU_TO_BN -> "Translate Russian word to Bengali"
                                        QuizMode.BN_TO_RU -> "Translate Bengali meaning to Russian"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Answer Input Field
                        OutlinedTextField(
                            value = quizSession.userAnswer,
                            onValueChange = { viewModel.updateQuizUserAnswer(it) },
                            label = {
                                Text(
                                    when (quizSession.mode) {
                                        QuizMode.RU_TO_BN -> "Enter Bengali Meaning"
                                        QuizMode.BN_TO_RU -> "Enter Russian Word"
                                    }
                                )
                            },
                            enabled = !quizSession.isAnswerChecked,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (!quizSession.isAnswerChecked) {
                                    viewModel.checkQuizAnswer()
                                }
                            }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quiz_answer_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Answer Feedback Section (When Done is clicked)
                        AnimatedVisibility(visible = quizSession.isAnswerChecked) {
                            val isCorrect = quizSession.isCorrect == true
                            val expected = when (quizSession.mode) {
                                QuizMode.RU_TO_BN -> currentQuestion.bengaliMeaning
                                QuizMode.BN_TO_RU -> currentQuestion.russianWord
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isCorrect) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.HighlightOff,
                                            contentDescription = null,
                                            tint = if (isCorrect) Color(0xFF059669) else Color(0xFFDC2626),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isCorrect) "Correct Answer!" else "Incorrect Answer!",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (isCorrect) Color(0xFF059669) else Color(0xFFDC2626)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Your answer: ${quizSession.userAnswer.ifEmpty { "(None)" }}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black.copy(alpha = 0.8f)
                                    )

                                    Text(
                                        text = "Correct answer: $expected",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        // Buttons: Done & Next
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.checkQuizAnswer() },
                                enabled = !quizSession.isAnswerChecked && quizSession.userAnswer.isNotBlank(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("quiz_done_button")
                            ) {
                                Icon(Icons.Default.Done, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Done")
                            }

                            Button(
                                onClick = { viewModel.nextQuizQuestion() },
                                enabled = quizSession.isAnswerChecked,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("quiz_next_button")
                            ) {
                                Text("Next")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreSummaryBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

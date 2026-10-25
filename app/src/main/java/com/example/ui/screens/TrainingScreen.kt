package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.DarkNightBlue
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalNavy
import com.example.ui.theme.MedicalNavyElevated
import com.example.ui.theme.MedicalNavySurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecondaryGrey
import com.example.ui.theme.TurquoiseGreen

@Composable
fun TrainingScreen(viewModel: MainViewModel) {
    val questions = viewModel.quizQuestions
    val currentIndex by viewModel.currentQuizIndex.collectAsState()
    val selectedOption by viewModel.selectedQuizOption.collectAsState()
    val isSubmitted by viewModel.isQuizSubmitted.collectAsState()
    val score by viewModel.quizScore.collectAsState()

    val currentQ = questions.getOrNull(currentIndex)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNightBlue)
            .testTag("training_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "ESPACE FORMATION IAR & MAR",
                        color = MedicalCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Cas cliniques interactifs et QCM de perfectionnement.",
                        color = SecondaryGrey,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MedicalNavyElevated,
                    border = BorderStroke(1.dp, TurquoiseGreen)
                ) {
                    Text(
                        "Score : $score / ${questions.size}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = TurquoiseGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (currentQ != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                    border = BorderStroke(1.dp, MedicalCyan.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MedicalNavySurface
                            ) {
                                Text(
                                    currentQ.domain,
                                    color = MedicalCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                "Question ${currentIndex + 1} / ${questions.size}",
                                color = SecondaryGrey,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            currentQ.question,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        currentQ.options.forEachIndexed { idx, opt ->
                            val isSelected = selectedOption == idx
                            val isCorrect = idx == currentQ.correctIndex

                            val borderCol = when {
                                isSubmitted && isCorrect -> TurquoiseGreen
                                isSubmitted && isSelected && !isCorrect -> AlertRed
                                isSelected -> MedicalCyan
                                else -> BorderSubtle
                            }

                            val bgCol = when {
                                isSubmitted && isCorrect -> TurquoiseGreen.copy(alpha = 0.12f)
                                isSubmitted && isSelected && !isCorrect -> AlertRed.copy(alpha = 0.12f)
                                isSelected -> MedicalCyan.copy(alpha = 0.08f)
                                else -> MedicalNavySurface
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable(enabled = !isSubmitted) {
                                        viewModel.selectQuizAnswer(idx)
                                    },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = bgCol),
                                border = BorderStroke(1.dp, borderCol)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(borderCol.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "${('A'.code + idx).toChar()}",
                                            color = borderCol,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(opt, color = PureWhite, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        if (!isSubmitted) {
                            Button(
                                onClick = { viewModel.submitQuizAnswer() },
                                enabled = selectedOption != null,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalCyan, contentColor = DarkNightBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Valider ma réponse", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Clinical explanation
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = MedicalNavySurface,
                                border = BorderStroke(1.dp, TurquoiseGreen.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("EXPLICATION CLINIQUE :", color = TurquoiseGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(currentQ.clinicalExplanation, color = PureWhite, fontSize = 12.sp, lineHeight = 16.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            if (currentIndex < questions.size - 1) {
                                Button(
                                    onClick = { viewModel.nextQuizQuestion() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = TurquoiseGreen, contentColor = DarkNightBlue),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Question suivante", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(16.dp))
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.resetQuiz() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalCyan, contentColor = DarkNightBlue),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Recommencer l'évaluation", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

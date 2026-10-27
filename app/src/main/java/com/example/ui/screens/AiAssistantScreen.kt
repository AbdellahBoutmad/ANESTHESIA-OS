package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChatMessage
import com.example.ui.MainViewModel

@Composable
fun AiAssistantScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val messages by viewModel.chatMessages.collectAsState()
    val isGenerating by viewModel.isAiGenerating.collectAsState()
    val selectedModel by viewModel.selectedAiModel.collectAsState()
    val customApiKey by viewModel.customApiKey.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showConfigDialog by remember { mutableStateOf(false) }
    var tempApiKey by remember { mutableStateOf(customApiKey) }

    val listState = rememberLazyListState()

    val quickPrompts = listOf(
        "واش صافا، شرح ليا البروبوفول بدارجة",
        "شنو هما مضاعفات قاعة الإفاقة (SSPI)؟",
        "شرح ليا les curares وفوقاش كنستعملو Célocurine",
        "كفاش كنديرو l'intubation difficile؟",
        "Calculer la dose de Propofol pour 70 kg",
        "شنو هو تدبير تشنج الحنجرة Laryngospasme؟"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val currentPalette = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(currentPalette.background)
            .imePadding()
            .testTag("ai_assistant_screen")
    ) {
        // BNaja AI Header with Status Indicator
        Surface(
            color = currentPalette.surface,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, currentPalette.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(currentPalette.primary.copy(alpha = 0.18f))
                                .border(
                                    BorderStroke(1.dp, currentPalette.primary.copy(alpha = 0.7f)),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "BNaja AI",
                                tint = currentPalette.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "BNaja AI",
                                    color = currentPalette.primary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = currentPalette.tertiary.copy(alpha = 0.2f),
                                    border = BorderStroke(0.5.dp, currentPalette.tertiary.copy(alpha = 0.7f))
                                ) {
                                    Text(
                                        "الدارجة المغربية",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        color = currentPalette.tertiary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                "المساعد الذكي للتخدير والإنعاش والقراية • Gemini Live",
                                color = currentPalette.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Top Action buttons: Clear Chat & Model/API Configuration
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                viewModel.clearChatMessages()
                                Toast.makeText(context, "تمت إعادة تعيين المحادثة", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.CleaningServices,
                                contentDescription = "مسح المحادثة",
                                tint = currentPalette.onSurfaceVariant,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                tempApiKey = customApiKey
                                showConfigDialog = true
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = "إعدادات الذكاء الاصطناعي",
                                tint = currentPalette.primary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Moroccan Darija Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                FilterChip(
                    selected = false,
                    onClick = {
                        viewModel.sendAiPrompt(prompt)
                    },
                    label = { Text(prompt, fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = currentPalette.surfaceVariant,
                        labelColor = currentPalette.onSurface
                    ),
                    border = BorderStroke(1.dp, currentPalette.outline.copy(alpha = 0.35f))
                )
            }
        }

        // Chat Messages List with RTL Support
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { msg ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(250)) + slideInVertically(
                        animationSpec = tween(250),
                        initialOffsetY = { 30 }
                    )
                ) {
                    BNajaChatBubble(
                        message = msg,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("BNaja AI", msg.text))
                            Toast.makeText(context, "تم النسخ بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Typing animation when AI is generating
            if (isGenerating) {
                item {
                    BNajaTypingIndicator()
                }
            }
        }

        // Message Input Field
        Surface(
            color = currentPalette.surface,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, currentPalette.outline.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            "سول BNaja AI على أي حاجة بغيتي بالدارجة...",
                            color = currentPalette.onSurfaceVariant,
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Start
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_input_field"),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = currentPalette.onSurface,
                        unfocusedTextColor = currentPalette.onSurface,
                        focusedContainerColor = currentPalette.surfaceVariant.copy(alpha = 0.6f),
                        unfocusedContainerColor = currentPalette.surfaceVariant.copy(alpha = 0.4f),
                        focusedBorderColor = currentPalette.primary,
                        unfocusedBorderColor = currentPalette.outline.copy(alpha = 0.4f)
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isGenerating) {
                            val textToSend = inputText
                            inputText = ""
                            viewModel.sendAiPrompt(textToSend)
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !isGenerating) currentPalette.primary
                            else currentPalette.surfaceVariant
                        )
                        .testTag("ai_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "إرسال إلى BNaja AI",
                        tint = if (inputText.isNotBlank() && !isGenerating) currentPalette.onPrimary
                               else currentPalette.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Model & API Key Configuration Dialog
    if (showConfigDialog) {
        val models = listOf(
            Triple("gemini-3.5-flash-lite", "Flash Lite 3.5 (موصى به)", "استجابة فائقة السرعة بالدارجة المغربية"),
            Triple("gemini-3.8-flash", "Flash 3.8", "تفكير سريري متقدم ومفصل")
        )

        AlertDialog(
            onDismissRequest = { showConfigDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, null, tint = currentPalette.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إعدادات BNaja AI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = currentPalette.primary.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, currentPalette.primary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "مفتاح Google Gemini API مدمج ومفعل افتراضياً للإجابة المباشرة بالدارجة المغربية.",
                            color = currentPalette.onSurface,
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "اختر نموذج الذكاء الاصطناعي :",
                        fontSize = 12.sp,
                        color = currentPalette.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    models.forEach { (modelId, label, desc) ->
                        val isSelected = selectedModel == modelId
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) currentPalette.primary.copy(alpha = 0.15f) else currentPalette.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) currentPalette.primary else currentPalette.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.setAiModel(modelId) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) currentPalette.primary else currentPalette.onSurface
                                )
                                Text(
                                    desc,
                                    fontSize = 11.sp,
                                    color = currentPalette.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "مفتاح API مخصص (اختياري) :",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = currentPalette.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = tempApiKey,
                        onValueChange = { tempApiKey = it },
                        placeholder = { Text("المفتاح المدمج مفعل تلقائياً (أو أدخل مفتاحك الخاص)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = currentPalette.onSurface,
                            unfocusedTextColor = currentPalette.onSurface
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setCustomApiKey(tempApiKey)
                        showConfigDialog = false
                        Toast.makeText(context, "تم حفظ الإعدادات", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = currentPalette.primary)
                ) {
                    Text("حفظ", color = currentPalette.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfigDialog = false }) {
                    Text("إغلاق", color = currentPalette.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
fun BNajaChatBubble(message: ChatMessage, onCopy: () -> Unit = {}) {
    val isUser = message.isUser
    val currentPalette = MaterialTheme.colorScheme

    val isArabicText = remember(message.text) {
        message.text.any { it in '\u0600'..'\u06FF' }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(currentPalette.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        null,
                        tint = currentPalette.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "BNaja AI",
                    color = currentPalette.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) currentPalette.primary else currentPalette.surfaceVariant,
            border = BorderStroke(
                1.dp,
                if (isUser) currentPalette.primary else currentPalette.outline.copy(alpha = 0.35f)
            ),
            modifier = Modifier.widthIn(min = 70.dp, max = 560.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides (if (isArabicText) LayoutDirection.Rtl else LayoutDirection.Ltr)
                ) {
                    SelectionContainer {
                        Text(
                            text = message.text,
                            color = if (isUser) currentPalette.onPrimary else currentPalette.onSurface,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            textAlign = if (isArabicText) TextAlign.Start else TextAlign.Start
                        )
                    }
                }

                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "نسخ",
                                tint = currentPalette.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BNajaTypingIndicator() {
    val currentPalette = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "typing")
    val dot1Scale by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Scale by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Scale by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = currentPalette.surfaceVariant,
        border = BorderStroke(1.dp, currentPalette.outline.copy(alpha = 0.3f)),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp * dot1Scale)
                    .clip(CircleShape)
                    .background(currentPalette.primary)
            )
            Box(
                modifier = Modifier
                    .size(8.dp * dot2Scale)
                    .clip(CircleShape)
                    .background(currentPalette.primary)
            )
            Box(
                modifier = Modifier
                    .size(8.dp * dot3Scale)
                    .clip(CircleShape)
                    .background(currentPalette.primary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                "BNaja AI كيوجد الجواب...",
                color = currentPalette.primary,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

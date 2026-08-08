package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MaleVoiceProfile
import com.example.data.model.VideoEngine
import com.example.ui.theme.*

@Composable
fun GenerationProgressOverlay(
    engine: VideoEngine,
    maleVoice: MaleVoiceProfile,
    progressPercentage: Int, // 0 to 100
    currentStageText: String,
    targetDurationSec: Int = 45,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SleekBgDark)
                .border(1.dp, SleekBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
                .testTag("generation_progress_dialog"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Spinning AI Aperture Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                colors = listOf(
                                    SleekBluePrimary,
                                    engine.badgeColor,
                                    SleekCyanGlow,
                                    SleekIndigoAccent,
                                    SleekBluePrimary
                                )
                            )
                        )
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(SleekSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Rendering AI video",
                            tint = SleekBluePrimary,
                            modifier = Modifier
                                .size(36.dp)
                                .rotate(rotation)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Synthesizing Video Master",
                        color = SleekTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${engine.displayName} • $targetDurationSec Seconds • Male VO",
                        color = SleekCyanGlow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Progress Bar with sleek blue gradient
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = currentStageText,
                            color = SleekTextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "$progressPercentage%",
                            color = SleekBluePrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progressPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = SleekBluePrimary,
                        trackColor = SleekCardSecondary
                    )
                }

                // Stage Checklist
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekCardSecondary)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StageRow("1. Prompt Decomposition & Camera Paths", isDone = progressPercentage >= 25, isCurrent = progressPercentage < 25)
                    StageRow("2. Deep Male Voice Narration & Audio Track", isDone = progressPercentage >= 50, isCurrent = progressPercentage in 25..49)
                    StageRow("3. 4-Scene Volumetric Raytracing & Physics", isDone = progressPercentage >= 75, isCurrent = progressPercentage in 50..74)
                    StageRow("4. Temporal Coherence & 1080p Master Output", isDone = progressPercentage >= 100, isCurrent = progressPercentage in 75..99)
                }

                TextButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.textButtonColors(contentColor = SleekTextMuted)
                ) {
                    Text("Run in Background", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun StageRow(title: String, isDone: Boolean, isCurrent: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
            contentDescription = null,
            tint = if (isDone) SleekEmerald else if (isCurrent) SleekBluePrimary else SleekTextMuted,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = title,
            color = if (isDone) SleekTextPrimary else if (isCurrent) SleekBluePrimary else SleekTextMuted,
            fontSize = 11.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
        )
    }
}

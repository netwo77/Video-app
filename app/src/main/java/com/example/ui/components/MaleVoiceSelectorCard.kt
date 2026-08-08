package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MaleVoiceProfile
import com.example.ui.theme.*

@Composable
fun MaleVoiceSelectorCard(
    selectedVoice: MaleVoiceProfile,
    onSelectVoice: (MaleVoiceProfile) -> Unit,
    isSpeaking: Boolean,
    audioLevels: FloatArray,
    onPreviewVoice: (MaleVoiceProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SleekCardSecondary)
            .border(1.dp, SleekBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("male_voice_card")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaleVoiceDeep.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Male Voice",
                        tint = MaleVoiceDeep,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = "NARRATOR (MALE ONLY)",
                        color = SleekTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "${selectedVoice.speakerName} • ${selectedVoice.styleTitle}",
                        color = SleekTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Preview speech button
            Button(
                onClick = { onPreviewVoice(selectedVoice) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSpeaking) MaleVoiceDeep else SleekCard
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSpeaking) MaleVoiceDeep else SleekBorder),
                modifier = Modifier.testTag("preview_voice_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.GraphicEq else Icons.Default.VolumeUp,
                        contentDescription = "Audition Voice",
                        tint = if (isSpeaking) Color.Black else MaleVoiceDeep,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isSpeaking) "Speaking..." else "Audition",
                        color = if (isSpeaking) Color.Black else SleekTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Live Audio Waveform Visualizer
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SleekCard)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            audioLevels.forEach { level ->
                val barHeight = (level * 18).coerceIn(3f, 18f).dp
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(barHeight)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isSpeaking) MaleVoiceDeep else SleekBorderFocus.copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Voice Profile Grid (Marcus, David, Alex, Liam, Julian, Ethan)
        Text(
            text = "Select Male Voice Artist:",
            color = SleekTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MaleVoiceProfile.values().forEach { profile ->
                val isSelected = selectedVoice == profile
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) SleekCard.copy(alpha = 0.9f) else SleekCardSecondary.copy(alpha = 0.4f))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) MaleVoiceDeep else SleekBorder,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectVoice(profile) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = profile.speakerName,
                                color = if (isSelected) Color.White else SleekTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = MaleVoiceDeep.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = profile.accentTag,
                                    color = MaleVoiceDeep,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Text(
                            text = profile.vocalTone,
                            color = SleekTextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectVoice(profile) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaleVoiceDeep,
                            unselectedColor = SleekBorder
                        )
                    )
                }
            }
        }
    }
}

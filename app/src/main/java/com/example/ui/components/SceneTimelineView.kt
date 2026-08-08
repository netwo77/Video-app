package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SceneItem
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun SceneTimelineView(
    scenes: List<SceneItem>,
    currentTimeSec: Float,
    onSelectScene: (SceneItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SleekCardSecondary)
            .border(1.dp, SleekBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("scene_timeline_container")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ViewTimeline,
                    contentDescription = "Timeline",
                    tint = SleekBluePrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "4-Shot Storyboard Breakdown",
                    color = SleekTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                color = SleekCard,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
            ) {
                Text(
                    text = "${scenes.size} Dynamic Cuts",
                    color = SleekTextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        scenes.forEachIndexed { index, scene ->
            val isActive = currentTimeSec >= scene.startTimeSec && currentTimeSec < scene.endTimeSec
            val borderHighlight by animateColorAsState(
                targetValue = if (isActive) SleekBluePrimary else SleekBorder,
                label = "scene_border"
            )
            val bgHighlight by animateColorAsState(
                targetValue = if (isActive) SleekCard.copy(alpha = 0.95f) else SleekCardSecondary.copy(alpha = 0.5f),
                label = "scene_bg"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgHighlight)
                    .border(1.dp, borderHighlight, RoundedCornerShape(12.dp))
                    .clickable { onSelectScene(scene) }
                    .padding(12.dp)
                    .testTag("scene_item_${scene.sceneIndex}"),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Scene Index and Time badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(56.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isActive) SleekBluePrimary else SleekCard)
                            .border(1.dp, if (isActive) Color.White else SleekBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${scene.sceneIndex}",
                            color = if (isActive) Color.White else SleekTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = String.format(Locale.US, "%02d-%02ds", scene.startTimeSec.toInt(), scene.endTimeSec.toInt()),
                        color = if (isActive) SleekCyanGlow else SleekTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Scene Details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = scene.title,
                            color = if (isActive) SleekTextPrimary else SleekTextPrimary.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isActive) {
                            Surface(
                                color = SleekBluePrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "PLAYING",
                                    color = SleekBluePrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = scene.visualPrompt,
                        color = SleekTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Camera & Voiceover badges
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = SleekCard,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = SleekCyanGlow,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = scene.cameraMovement,
                                    color = SleekTextSecondary,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (scene.voiceoverText.isNotBlank()) {
                            Surface(
                                color = SleekCard,
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = MaleVoiceDeep,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "Male VO",
                                        color = MaleVoiceDeep,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (index < scenes.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

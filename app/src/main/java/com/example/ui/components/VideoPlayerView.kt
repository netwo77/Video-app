package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale

@Composable
fun VideoPlayerView(
    project: VideoProject,
    isPlaying: Boolean,
    currentTimeSec: Float,
    onSeekTo: (Float) -> Unit,
    onTogglePlay: () -> Unit,
    onReplay: () -> Unit,
    isVoiceEnabled: Boolean,
    onToggleVoice: () -> Unit,
    modifier: Modifier = Modifier,
    isFullscreen: Boolean = false,
    onToggleFullscreen: () -> Unit = {}
) {
    val durationSec = project.targetDurationSeconds.toFloat().coerceAtLeast(40f)
    val progress = (currentTimeSec / durationSec).coerceIn(0f, 1f)

    // Identify active scene cut
    val activeScene = project.scenes.find { scene ->
        currentTimeSec >= scene.startTimeSec && currentTimeSec < scene.endTimeSec
    } ?: project.scenes.lastOrNull()

    // Smooth cinematic zoom and pan camera animation
    val infiniteTransition = rememberInfiniteTransition(label = "camera_motion")
    val cameraDrift by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "camera_zoom"
    )

    val panOffset by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "camera_pan"
    )

    // Resolve visual asset for this scene
    val drawableRes = remember(activeScene, project.engine) {
        when {
            activeScene?.visualAssetType?.contains("ZERO", ignoreCase = true) == true || project.engine == VideoEngine.ZERO_V3 -> R.drawable.img_zero_demo
            activeScene?.visualAssetType?.contains("NANO", ignoreCase = true) == true || project.engine == VideoEngine.NANO_V2 -> R.drawable.img_nano_demo
            else -> R.drawable.img_sora_demo
        }
    }

    var showControls by remember { mutableStateOf(true) }

    // Auto-hide controls after 3 seconds of playing
    LaunchedEffect(isPlaying, showControls) {
        if (isPlaying && showControls) {
            delay(3500)
            showControls = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 18.dp))
            .background(SleekBgDark)
            .border(
                width = if (isFullscreen) 0.dp else 1.dp,
                color = SleekBorder,
                shape = RoundedCornerShape(if (isFullscreen) 0.dp else 18.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
            .testTag("video_player_surface")
    ) {
        // 1. Cinematic Video Frame Render Canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = cameraDrift
                    scaleY = cameraDrift
                    translationX = panOffset
                }
        ) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = activeScene?.visualPrompt ?: "Video scene frame",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Film grain & cinematic gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.65f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
        }

        // 2. Engine & Resolution Badges (Top Left & Right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Engine badge
            Surface(
                color = project.engine.badgeColor.copy(alpha = 0.25f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, project.engine.badgeColor.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(project.engine.badgeColor)
                    )
                    Text(
                        text = project.engine.displayName.uppercase(Locale.ROOT),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Voice & Resolution Badges
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    color = SleekCard.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Male Voice",
                            tint = MaleVoiceDeep,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = project.maleVoice.speakerName,
                            color = SleekTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    color = SleekCard.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
                ) {
                    Text(
                        text = "${project.targetDurationSeconds}s • ${project.fps}fps",
                        color = SleekTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // 3. Subtitles / Male Voiceover Closed Captions (Bottom overlay above timeline)
        if (activeScene != null && activeScene.voiceoverText.isNotBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (showControls) 86.dp else 24.dp, start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = "Narration voice",
                        tint = MaleVoiceDeep,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "\"${activeScene.voiceoverText}\"",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // 4. Center Play/Pause Indicator (when paused or clicking)
        AnimatedVisibility(
            visible = showControls || !isPlaying,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(SleekBluePrimary.copy(alpha = 0.9f))
                    .shadow(12.dp, CircleShape)
                    .testTag("play_pause_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause video" else "Play video",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // 5. Bottom Overlay Player Controls (Timeline, Scrubber, Timecode, Fullscreen)
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Scene Index and Camera Motion Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activeScene?.title ?: "Full Sequence",
                        color = SleekTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = activeScene?.cameraMovement ?: "Cinematic Motion",
                        color = SleekCyanGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Custom Timeline Scrubber with Scene Break Points
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Slider(
                        value = currentTimeSec,
                        onValueChange = onSeekTo,
                        valueRange = 0f..durationSec,
                        colors = SliderDefaults.colors(
                            thumbColor = SleekBluePrimary,
                            activeTrackColor = SleekBluePrimary,
                            inactiveTrackColor = SleekCardSecondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("timeline_slider")
                    )
                }

                // Controls Bar (Play/Pause, Voice Toggle, Replay, Timecode, Fullscreen)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = onTogglePlay,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = onReplay,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Replay from start",
                                tint = SleekTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onToggleVoice,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isVoiceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = "Toggle male narration audio",
                                tint = if (isVoiceEnabled) MaleVoiceDeep else SleekTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Timecode Display: 00:23 / 00:45
                        val currentMin = (currentTimeSec / 60).toInt()
                        val currentSec = (currentTimeSec % 60).toInt()
                        val totalMin = (durationSec / 60).toInt()
                        val totalSec = (durationSec % 60).toInt()
                        Text(
                            text = String.format(Locale.US, "%02d:%02d / %02d:%02d", currentMin, currentSec, totalMin, totalSec),
                            color = SleekTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onToggleFullscreen,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = "Toggle Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

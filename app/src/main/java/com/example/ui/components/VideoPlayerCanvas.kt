package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SceneItem
import com.example.data.model.VideoAspectRatio
import com.example.data.model.VideoEngine
import com.example.data.model.VideoProject
import com.example.ui.theme.*
import kotlin.math.sin

@Composable
fun VideoPlayerCanvas(
    project: VideoProject,
    isPlaying: Boolean,
    currentTimeSec: Float,
    activeSceneIndex: Int,
    activeSubtitle: String,
    onPlayPause: () -> Unit,
    onSeekTo: (Float) -> Unit,
    onJumpToScene: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalDuration = project.targetDurationSeconds.toFloat()

    // Animated camera movement and lighting shaders
    val infiniteTransition = rememberInfiniteTransition(label = "camera_motion")
    val panOffset by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pan"
    )
    val zoomScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoom"
    )
    val lightSweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "light"
    )

    val activeScene = project.scenes.firstOrNull { it.sceneIndex == activeSceneIndex }
        ?: project.scenes.firstOrNull()

    val ratio = when (project.aspectRatio) {
        VideoAspectRatio.VERTICAL_9_16 -> 9f / 16f
        VideoAspectRatio.SQUARE_1_1 -> 1f
        VideoAspectRatio.ANAMORPHIC_239_1 -> 2.39f
        else -> 16f / 9f
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Video Viewport Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio.coerceAtLeast(1.4f))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
                .border(BorderStroke(1.dp, SleekBorder), RoundedCornerShape(16.dp))
                .testTag("video_viewport")
        ) {
            val drawableRes = when (project.engine) {
                VideoEngine.ZERO_V3 -> R.drawable.img_zero_demo
                VideoEngine.NANO_V2 -> R.drawable.img_nano_demo
                else -> R.drawable.img_sora_demo
            }

            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = "Cinematic Video Frame",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(if (isPlaying) zoomScale else 1.0f)
                    .offset(x = if (isPlaying) panOffset.dp else 0.dp)
            )

            // Canvas for Shaders & Atmospheric Effects
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Radial Vignette
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color(0x99000000), Color(0xDD0F1113)),
                        center = Offset(width / 2f, height / 2f),
                        radius = width * 0.8f
                    )
                )

                // Top and Bottom Letterbox Gradients
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xCC0F1113), Color.Transparent, Color(0xEE0F1113)),
                        startY = 0f,
                        endY = height
                    )
                )

                if (isPlaying) {
                    when (project.engine) {
                        VideoEngine.ZERO_V3 -> {
                            val streakY1 = (height * 0.35f + sin(currentTimeSec.toDouble() * 5.0).toFloat() * 40f)
                            val streakY2 = (height * 0.65f + sin(currentTimeSec.toDouble() * 7.0).toFloat() * 30f)
                            drawLine(
                                color = ZeroV3Cyan.copy(alpha = 0.6f),
                                start = Offset(0f, streakY1),
                                end = Offset(width, streakY1),
                                strokeWidth = 2.5f
                            )
                            drawLine(
                                color = SleekBlueLight.copy(alpha = 0.4f),
                                start = Offset(0f, streakY2),
                                end = Offset(width, streakY2),
                                strokeWidth = 1.5f
                            )
                        }
                        VideoEngine.SORA -> {
                            val sweepX = width * lightSweep
                            drawLine(
                                brush = Brush.linearGradient(
                                    listOf(Color.Transparent, SleekBluePrimary.copy(alpha = 0.35f), Color.Transparent)
                                ),
                                start = Offset(sweepX - 80f, 0f),
                                end = Offset(sweepX + 80f, height),
                                strokeWidth = 120f
                            )
                        }
                        VideoEngine.NANO_V2 -> {
                            for (i in 1..5) {
                                val px = width * ((i * 0.2f + currentTimeSec * 0.05f) % 1f)
                                val py = height * (0.2f + (i * 0.15f))
                                drawCircle(
                                    color = NanoV2Green.copy(alpha = 0.5f),
                                    radius = 3.5f,
                                    center = Offset(px, py)
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }

            // Top HUD: Engine Badge, Resolution & Aspect Ratio
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SleekGlassDark,
                    border = BorderStroke(1.dp, SleekBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(project.engine.badgeColor)
                        )
                        Text(
                            text = project.engine.modelTag,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SleekGlassDark,
                        border = BorderStroke(1.dp, SleekBorder)
                    ) {
                        Text(
                            text = "${project.fps} FPS • 4K",
                            color = SleekCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SleekGlassDark,
                        border = BorderStroke(1.dp, SleekBorder)
                    ) {
                        Text(
                            text = project.aspectRatio.displayName.split(" ").first(),
                            color = SleekTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Center Play/Pause Button
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) SleekGlassDark else SleekBluePrimary)
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)), CircleShape)
                    .clickable { onPlayPause() }
                    .testTag("center_play_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Bottom Overlay: Male Voice Subtitle Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                activeScene?.let { scene ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SleekCardElevated.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, SleekBluePrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Voiceover",
                                tint = SleekBlueLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (activeSubtitle.isNotBlank()) "\"$activeSubtitle\"" else "\"${scene.voiceoverText}\"",
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 16.sp
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Timeline Slider & Timecode
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Slider(
                value = currentTimeSec.coerceIn(0f, totalDuration),
                onValueChange = onSeekTo,
                valueRange = 0f..totalDuration,
                colors = SliderDefaults.colors(
                    thumbColor = SleekBluePrimary,
                    activeTrackColor = SleekBluePrimary,
                    inactiveTrackColor = TimelineTrack
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .testTag("timeline_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimecode(currentTimeSec),
                    style = TextStyle(
                        color = SleekTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Male Voice",
                        tint = SleekBluePrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Voice: ${project.maleVoice.speakerName}",
                        style = TextStyle(
                            color = SleekTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Text(
                    text = formatTimecode(totalDuration),
                    style = TextStyle(
                        color = SleekTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                )
            }
        }

        // 4-Act Scene Jump Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            project.scenes.forEach { scene ->
                val isActive = scene.sceneIndex == activeSceneIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActive) SleekCardElevated else SleekCard)
                        .border(
                            BorderStroke(
                                1.dp,
                                if (isActive) SleekBluePrimary else SleekBorder
                            ),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onJumpToScene(scene.sceneIndex) }
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ACT ${scene.sceneIndex}",
                            style = TextStyle(
                                color = if (isActive) SleekBlueLight else SleekTextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "${scene.startTimeSec.toInt()}s-${scene.endTimeSec.toInt()}s",
                            style = TextStyle(
                                color = if (isActive) Color.White else SleekTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun formatTimecode(seconds: Float): String {
    val sec = seconds.toInt().coerceAtLeast(0)
    val mins = sec / 60
    val remSec = sec % 60
    return String.format("%02d:%02d", mins, remSec)
}

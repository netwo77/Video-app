package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MaleVoiceProfile
import com.example.data.model.PromptPreset
import com.example.data.model.VideoAspectRatio
import com.example.data.model.VideoEngine
import com.example.ui.theme.*

@Composable
fun SleekTopHeader(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SleekBluePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = "Studio Logo",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = "Visionary AI",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                )
                Text(
                    text = "Sora • Zero V3 • Nano V2 Studio",
                    style = TextStyle(
                        color = SleekTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SleekSurface,
                border = BorderStroke(1.dp, SleekBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(SleekEmerald)
                    )
                    Text(
                        text = "Gemini 2.5 Active",
                        style = TextStyle(
                            color = SleekTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SleekSurface)
                    .border(BorderStroke(1.dp, SleekBorder), CircleShape)
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Settings",
                    tint = SleekTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun SleekPromptInput(
    prompt: String,
    onPromptChanged: (String) -> Unit,
    onOptimizePrompt: () -> Unit,
    isOptimizing: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, SleekBorder), RoundedCornerShape(16.dp)),
        color = SleekSurface
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Prompt",
                        tint = SleekBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "PROMPT TO 40-50s CINEMATIC VIDEO",
                        style = TextStyle(
                            color = SleekBlueLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                TextButton(
                    onClick = onOptimizePrompt,
                    enabled = !isOptimizing && prompt.isNotBlank(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    if (isOptimizing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            color = SleekBluePrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Auto-Direct",
                            color = if (prompt.isNotBlank()) SleekBluePrimary else SleekTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            OutlinedTextField(
                value = prompt,
                onValueChange = onPromptChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 90.dp)
                    .testTag("prompt_input"),
                placeholder = {
                    Text(
                        text = "Describe your cinematic concept (e.g., A cybernetic samurai meditating under glowing neon cherry blossoms in Tokyo 2099)...",
                        color = SleekTextMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                },
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = SleekBluePrimary
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${prompt.length} chars • 4-Act Storyboard Script",
                    style = TextStyle(
                        color = SleekTextMuted,
                        fontSize = 10.sp
                    )
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SleekCardElevated
                ) {
                    Text(
                        text = "40-50s Calibrated",
                        color = SleekEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SleekEngineSelector(
    selectedEngine: VideoEngine,
    onEngineSelected: (VideoEngine) -> Unit,
    modifier: Modifier = Modifier
) {
    val engines = listOf(
        VideoEngine.SORA,
        VideoEngine.ZERO_V3,
        VideoEngine.NANO_V2,
        VideoEngine.AUTO_MULTI
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SELECT VIDEO ENGINE",
                style = TextStyle(
                    color = SleekTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
            Text(
                text = selectedEngine.subtitle,
                style = TextStyle(
                    color = SleekBlueLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            engines.forEach { engine ->
                val isSelected = selectedEngine == engine
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            BorderStroke(
                                1.dp,
                                if (isSelected) SleekBluePrimary else SleekBorder
                            ),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onEngineSelected(engine) }
                        .testTag("engine_chip_${engine.id}"),
                    color = if (isSelected) SleekCardElevated else SleekSurface
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = engine.displayName,
                            style = TextStyle(
                                color = if (isSelected) Color.White else SleekTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "${engine.defaultFps}fps",
                            style = TextStyle(
                                color = if (isSelected) SleekCyan else SleekTextMuted,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SleekPresetCarousel(
    onPresetSelected: (PromptPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = listOf(
        PromptPreset(
            title = "Cyberpunk Neo-Tokyo",
            category = "Sci-Fi",
            shortPrompt = "A lone cybernetic samurai walking through rainy Neo-Tokyo, reflection of holographic billboards in puddles, cinematic bokeh and steam rising from grates.",
            recommendedEngine = VideoEngine.ZERO_V3,
            recommendedVoice = MaleVoiceProfile.LIAM_DRAMATIC,
            durationSec = 48,
            previewGradientColors = listOf(SleekBluePrimary, SleekCyan)
        ),
        PromptPreset(
            title = "Deep Cosmos Odyssey",
            category = "Space",
            shortPrompt = "Interstellar starship warping past glowing stellar nebula, volumetric light rays penetrating deep asteroid belt, awe-inspiring celestial scale.",
            recommendedEngine = VideoEngine.SORA,
            recommendedVoice = MaleVoiceProfile.MARCUS_BARITONE,
            durationSec = 50,
            previewGradientColors = listOf(SleekIndigo, SleekPurple)
        ),
        PromptPreset(
            title = "Mystic Forest Dragon",
            category = "Fantasy",
            shortPrompt = "Majestic emerald dragon emerging through ancient bioluminescent redwood forest, floating spores, golden dawn light filtering through canopy.",
            recommendedEngine = VideoEngine.NANO_V2,
            recommendedVoice = MaleVoiceProfile.DAVID_NARRATOR,
            durationSec = 45,
            previewGradientColors = listOf(SleekEmerald, SleekGold)
        )
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "DIRECTOR'S PROMPT PRESETS",
            style = TextStyle(
                color = SleekTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(BorderStroke(1.dp, SleekBorder), RoundedCornerShape(12.dp))
                        .clickable { onPresetSelected(preset) },
                    color = SleekSurface
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = preset.category.uppercase(),
                            color = SleekCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = preset.title,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${preset.durationSec}s • ${preset.recommendedVoice.speakerName.split(" ").first()}",
                            color = SleekTextMuted,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SleekNarratorAndDurationBar(
    voice: MaleVoiceProfile,
    durationSeconds: Int,
    aspectRatio: VideoAspectRatio,
    onVoiceClicked: () -> Unit,
    onDurationClicked: () -> Unit,
    onAspectRatioClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Male Voice Profile Button (MALE ONLY)
        Surface(
            modifier = Modifier
                .weight(1.3f)
                .clip(RoundedCornerShape(12.dp))
                .border(BorderStroke(1.dp, SleekBorder), RoundedCornerShape(12.dp))
                .clickable { onVoiceClicked() }
                .testTag("voice_selector_button"),
            color = SleekSurface
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(SleekBluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Male Voice",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MALE NARRATOR",
                        style = TextStyle(
                            color = SleekBlueLight,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = voice.speakerName,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Duration Picker Button (40-50s)
        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .border(BorderStroke(1.dp, SleekBorder), RoundedCornerShape(12.dp))
                .clickable { onDurationClicked() }
                .testTag("duration_selector_button"),
            color = SleekSurface
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Duration",
                    tint = SleekBlueLight,
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text(
                        text = "DURATION",
                        style = TextStyle(
                            color = SleekTextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${durationSeconds}s Video",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Aspect Ratio Button
        Surface(
            modifier = Modifier
                .weight(0.9f)
                .clip(RoundedCornerShape(12.dp))
                .border(BorderStroke(1.dp, SleekBorder), RoundedCornerShape(12.dp))
                .clickable { onAspectRatioClicked() }
                .testTag("aspect_ratio_button"),
            color = SleekSurface
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "RATIO",
                    style = TextStyle(
                        color = SleekTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = aspectRatio.displayName.split(" ").first(),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun SleekGenerateButton(
    isGenerating: Boolean,
    progressText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isGenerating,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("generate_video_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SleekBluePrimary,
            disabledContainerColor = SleekCardElevated
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Text(
                    text = progressText,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Generate",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Generate 40-50s Video with Male Voice",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun SleekBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SleekSurface,
        border = BorderStroke(1.dp, SleekBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.VideoCall,
                label = "Studio",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )
            BottomNavItem(
                icon = Icons.Default.VideoLibrary,
                label = "Vault",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
            BottomNavItem(
                icon = Icons.Default.Mic,
                label = "Male Voices",
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) }
            )
            BottomNavItem(
                icon = Icons.Default.Dashboard,
                label = "Engines",
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) SleekBluePrimary else SleekTextMuted,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = TextStyle(
                color = if (isSelected) Color.White else SleekTextMuted,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

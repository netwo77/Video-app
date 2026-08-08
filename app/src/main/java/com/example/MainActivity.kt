package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentTimeSec by viewModel.currentTimeSec.collectAsState()
    val isVoiceEnabled by viewModel.isVoiceEnabled.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generationProgress by viewModel.generationProgress.collectAsState()
    val stageText by viewModel.generationStageText.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val isSpeaking by viewModel.voiceService.isSpeaking.collectAsState()
    val audioLevels by viewModel.voiceService.audioLevels.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = SleekBgDark,
        bottomBar = {
            SleekBottomNavBar(
                activeTab = activeTab,
                onTabSelect = { viewModel.setActiveTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SleekBgDark)
        ) {
            when (activeTab) {
                0 -> StudioCreateScreen(
                    viewModel = viewModel,
                    onOpenSettings = { showExportDialog = true }
                )
                1 -> PlayerStudioScreen(
                    project = activeProject,
                    isPlaying = isPlaying,
                    currentTimeSec = currentTimeSec,
                    isVoiceEnabled = isVoiceEnabled,
                    isSpeaking = isSpeaking,
                    audioLevels = audioLevels,
                    onTogglePlay = { viewModel.togglePlayPause() },
                    onSeekTo = { viewModel.seekTo(it) },
                    onReplay = { viewModel.seekTo(0f); viewModel.startPlayback() },
                    onToggleVoice = { viewModel.toggleVoice() },
                    onExport = { showExportDialog = true },
                    onBackToCreate = { viewModel.setActiveTab(0) }
                )
                2 -> VideoGalleryView(
                    projects = allProjects,
                    onSelectProject = { viewModel.selectProject(it) },
                    onDeleteProject = { viewModel.deleteProject(it) },
                    onNewProject = { viewModel.setActiveTab(0) }
                )
                3 -> NarratorStudioScreen(
                    viewModel = viewModel,
                    isSpeaking = isSpeaking,
                    audioLevels = audioLevels
                )
            }

            // Generation Progress Modal
            if (isGenerating) {
                GenerationProgressOverlay(
                    engine = viewModel.selectedEngine.value,
                    maleVoice = viewModel.selectedMaleVoice.value,
                    progressPercentage = generationProgress,
                    currentStageText = stageText,
                    targetDurationSec = viewModel.targetDurationSeconds.value,
                    onCancel = { /* background mode */ }
                )
            }

            // Export Dialog
            if (showExportDialog && activeProject != null) {
                ExportVideoDialog(
                    project = activeProject!!,
                    onDismiss = { showExportDialog = false }
                )
            }
        }
    }
}

@Composable
fun StudioCreateScreen(
    viewModel: MainViewModel,
    onOpenSettings: () -> Unit
) {
    val prompt by viewModel.currentPrompt.collectAsState()
    val selectedEngine by viewModel.selectedEngine.collectAsState()
    val selectedVoice by viewModel.selectedMaleVoice.collectAsState()
    val durationSeconds by viewModel.targetDurationSeconds.collectAsState()
    val aspectRatio by viewModel.selectedAspectRatio.collectAsState()
    val isSpeaking by viewModel.voiceService.isSpeaking.collectAsState()
    val audioLevels by viewModel.voiceService.audioLevels.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Sleek Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SleekBluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MovieFilter,
                        contentDescription = "Studio Icon",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "Visionary AI",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "40-50s Video • Male VO • Multi-Engine",
                        color = SleekTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SleekCard)
                    .border(1.dp, SleekBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = SleekTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Section 1: Creative Prompt Input
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Creative Prompt",
                    color = SleekTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${prompt.length} chars",
                    color = SleekTextMuted,
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SleekCard)
                    .border(1.dp, SleekBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
                    .testTag("prompt_input_container")
            ) {
                BasicTextField(
                    value = prompt,
                    onValueChange = { viewModel.updatePrompt(it) },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(SleekBluePrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 180.dp)
                        .testTag("creative_prompt_input"),
                    decorationBox = { innerTextField ->
                        if (prompt.isEmpty()) {
                            Text(
                                text = "Describe the video you want to generate (e.g., cyber warrior, deep sea discovery, space odyssey)...",
                                color = SleekTextMuted,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }

            // Quick Preset Prompts Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetPrompts.presets.forEach { preset ->
                    Surface(
                        color = SleekCardSecondary,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                        modifier = Modifier.clickable { viewModel.applyPreset(preset) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = preset.recommendedEngine.badgeColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = preset.title,
                                color = SleekTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Model Architecture (SORA, ZERO V3, NANO V2 + All Available Video Creators)
        VideoEngineSelector(
            selectedEngine = selectedEngine,
            onSelectEngine = { viewModel.setEngine(it) }
        )

        // Section 3: Narrator & Duration Grid (Sleek Interface 2-Column Grid)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Narrator Card (Male Only)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SleekCardSecondary)
                    .border(1.dp, SleekBorder, RoundedCornerShape(16.dp))
                    .clickable { viewModel.setActiveTab(3) }
                    .padding(14.dp)
                    .testTag("narrator_grid_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "NARRATOR",
                        color = SleekTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Male Voice",
                            tint = MaleVoiceDeep,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${selectedVoice.speakerName.split(" ").first()} (Male)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = selectedVoice.styleTitle,
                        color = SleekTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            // Duration Card (40-50 Seconds)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SleekCardSecondary)
                    .border(1.dp, SleekBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
                    .testTag("duration_grid_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "DURATION",
                        color = SleekTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Duration",
                            tint = SleekBluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$durationSeconds Seconds",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "40 to 50s Cinematic",
                        color = SleekTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Duration Adjustment Slider (40s - 50s)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SleekCard)
                .border(1.dp, SleekBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cinematic Target Duration",
                    color = SleekTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$durationSeconds seconds",
                    color = SleekBluePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = durationSeconds.toFloat(),
                onValueChange = { viewModel.setTargetDuration(it.toInt()) },
                valueRange = 40f..50f,
                steps = 9,
                colors = SliderDefaults.colors(
                    thumbColor = SleekBluePrimary,
                    activeTrackColor = SleekBluePrimary,
                    inactiveTrackColor = SleekCardSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("duration_slider")
            )
        }

        // Section 4: Primary Generate Video Button (Sleek Blue Gradient)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.generateVideo() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(16.dp, RoundedCornerShape(16.dp), ambientColor = SleekBlueDark, spotColor = SleekBluePrimary)
                    .testTag("generate_video_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(SleekBlueDark, SleekBluePrimary, SleekIndigoAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Generate Video",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Text(
                text = "Converts prompt into a $durationSeconds-second video with ${selectedEngine.displayName} & male voice narration",
                color = SleekTextMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PlayerStudioScreen(
    project: VideoProject?,
    isPlaying: Boolean,
    currentTimeSec: Float,
    isVoiceEnabled: Boolean,
    isSpeaking: Boolean,
    audioLevels: FloatArray,
    onTogglePlay: () -> Unit,
    onSeekTo: (Float) -> Unit,
    onReplay: () -> Unit,
    onToggleVoice: () -> Unit,
    onExport: () -> Unit,
    onBackToCreate: () -> Unit
) {
    if (project == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = SleekTextMuted,
                    modifier = Modifier.size(56.dp)
                )
                Text(
                    text = "No Video Project Loaded",
                    color = SleekTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onBackToCreate,
                    colors = ButtonDefaults.buttonColors(containerColor = SleekBluePrimary)
                ) {
                    Text("Create New Video")
                }
            }
        }
        return
    }

    var isFullscreen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Player Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBackToCreate) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column {
                    Text(
                        text = project.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "${project.targetDurationSeconds}s Master • ${project.engine.displayName}",
                        color = SleekCyanGlow,
                        fontSize = 11.sp
                    )
                }
            }

            Button(
                onClick = onExport,
                colors = ButtonDefaults.buttonColors(containerColor = SleekCard),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = SleekBluePrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 1. High-Performance Video Player View
        VideoPlayerView(
            project = project,
            isPlaying = isPlaying,
            currentTimeSec = currentTimeSec,
            onSeekTo = onSeekTo,
            onTogglePlay = onTogglePlay,
            onReplay = onReplay,
            isVoiceEnabled = isVoiceEnabled,
            onToggleVoice = onToggleVoice,
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
            isFullscreen = isFullscreen,
            onToggleFullscreen = { isFullscreen = !isFullscreen }
        )

        // 2. Male Voice Narration Live Waveform Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SleekCardSecondary)
                .border(1.dp, SleekBorder, RoundedCornerShape(14.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaleVoiceDeep.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = null,
                    tint = MaleVoiceDeep,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Male Narrator: ${project.maleVoice.speakerName}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = project.maleVoice.styleTitle,
                    color = SleekTextSecondary,
                    fontSize = 10.sp
                )
            }

            // Waveform pulse
            Row(
                modifier = Modifier.width(70.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                audioLevels.take(12).forEach { lvl ->
                    val barH = (lvl * 16).coerceIn(3f, 16f).dp
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(barH)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isPlaying && isVoiceEnabled) MaleVoiceDeep else SleekBorder)
                    )
                }
            }
        }

        // 3. 4-Scene Storyboard Breakdown (tap to seek)
        SceneTimelineView(
            scenes = project.scenes,
            currentTimeSec = currentTimeSec,
            onSelectScene = { scene ->
                onSeekTo(scene.startTimeSec)
            }
        )

        // 4. Director's Enhanced Prompt Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SleekCardSecondary)
                .border(1.dp, SleekBorder, RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "DIRECTOR'S PROMPT & KINEMATICS",
                color = SleekTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Text(
                text = project.enhancedPrompt,
                color = SleekTextPrimary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun NarratorStudioScreen(
    viewModel: MainViewModel,
    isSpeaking: Boolean,
    audioLevels: FloatArray
) {
    val selectedVoice by viewModel.selectedMaleVoice.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Male Voice Narration Studio",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Audition and assign deep male voices for your 40-50s videos",
                color = SleekTextSecondary,
                fontSize = 12.sp
            )
        }

        MaleVoiceSelectorCard(
            selectedVoice = selectedVoice,
            onSelectVoice = { viewModel.setMaleVoice(it) },
            isSpeaking = isSpeaking,
            audioLevels = audioLevels,
            onPreviewVoice = { viewModel.previewVoiceSample(it) }
        )

        Surface(
            color = SleekCardSecondary,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Acoustic Pacing & Synchronization",
                    color = SleekTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Each 40-50 second video is divided into 4 key narrative acts with calibrated speech cadence, pauses, and sub-bass resonance designed specifically for deep male voice profiles.",
                    color = SleekTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
fun SleekBottomNavBar(
    activeTab: Int,
    onTabSelect: (Int) -> Unit
) {
    Surface(
        color = SleekBgDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                label = "Create",
                isSelected = activeTab == 0,
                onClick = { onTabSelect(0) }
            )
            NavItem(
                icon = Icons.Default.Movie,
                label = "Player",
                isSelected = activeTab == 1,
                onClick = { onTabSelect(1) }
            )
            NavItem(
                icon = Icons.Default.VideoLibrary,
                label = "Gallery",
                isSelected = activeTab == 2,
                onClick = { onTabSelect(2) }
            )
            NavItem(
                icon = Icons.Default.Mic,
                label = "Narrator",
                isSelected = activeTab == 3,
                onClick = { onTabSelect(3) }
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isSelected) SleekBluePrimary else SleekTextMuted

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("nav_tab_${label.lowercase()}")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = tint,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_projects")
data class VideoProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val userPrompt: String,
    val enhancedPrompt: String,
    val engineName: String, // SORA, ZERO_V3, NANO_V2, AUTO_MULTI
    val maleVoiceId: String, // marcus, david, alex, liam, julian
    val durationSeconds: Int, // 40 to 50
    val aspectRatioName: String, // WIDESCREEN_16_9, VERTICAL_9_16, etc.
    val scenesJson: String, // Serialized scene cuts
    val createdAt: Long = System.currentTimeMillis(),
    val isRendered: Boolean = true,
    val fps: Int = 30,
    val resolution: String = "1080p",
    val bgMusicTrack: String = "Cinematic Ambient Wave"
)

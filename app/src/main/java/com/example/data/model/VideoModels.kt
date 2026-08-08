package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class VideoEngine(
    val id: String,
    val displayName: String,
    val modelTag: String,
    val subtitle: String,
    val description: String,
    val badgeColor: Color,
    val badgeBgColor: Color,
    val defaultFps: Int,
    val motionCapability: String,
    val latencyScore: String,
    val iconName: String
) {
    SORA(
        id = "sora",
        displayName = "Sora Turbo",
        modelTag = "OpenAI Sora v2.4",
        subtitle = "Cinematic Realism & Complex Physics",
        description = "World-class physical simulation, photorealistic textures, dynamic camera paths, and light coherence across 50-second generation.",
        badgeColor = SoraBrandColor,
        badgeBgColor = SoraPurpleBg,
        defaultFps = 30,
        motionCapability = "Complex 3D Camera Sweep",
        latencyScore = "High Fidelity (35s render)",
        iconName = "sora"
    ),
    ZERO_V3(
        id = "zero_v3",
        displayName = "Zero V3",
        modelTag = "Zero V3 Diffusion Pro",
        subtitle = "Hyper-Speed Kinetic Action & Neon VFX",
        description = "Optimized for extreme velocity, neon raytracing, dynamic motion streaks, and high frame-rate temporal coherence.",
        badgeColor = ZeroV3BrandColor,
        badgeBgColor = ZeroV3CyanBg,
        defaultFps = 60,
        motionCapability = "High-Octane Kinetic Action",
        latencyScore = "Ultra Fast (18s render)",
        iconName = "zero"
    ),
    NANO_V2(
        id = "nano_v2",
        displayName = "Nano V2",
        modelTag = "Nano V2 Neural Mobile",
        subtitle = "Stylized & Rapid Neural Synthesis",
        description = "Lightweight high-efficiency engine for vibrant anime, 3D animated worlds, crisp fantasy art, and instantaneous iteration.",
        badgeColor = NanoV2BrandColor,
        badgeBgColor = NanoV2GreenBg,
        defaultFps = 24,
        motionCapability = "Smooth Stylized Keyframes",
        latencyScore = "Instantaneous (12s render)",
        iconName = "nano"
    ),
    VEO_2(
        id = "veo_2",
        displayName = "Veo 2",
        modelTag = "Google DeepMind Veo 2",
        subtitle = "High-Definition Narrative Cinema",
        description = "Master-grade cinematic compositions with volumetric lighting, precise narrative pacing, and seamless visual storytelling.",
        badgeColor = VeoBrandColor,
        badgeBgColor = MaleVoiceDarkBg,
        defaultFps = 30,
        motionCapability = "Narrative Dolly & Pan",
        latencyScore = "Pro Cinema (28s render)",
        iconName = "veo"
    ),
    RUNWAY_GEN3(
        id = "runway_gen3",
        displayName = "Runway Gen-3",
        modelTag = "Gen-3 Alpha Motion",
        subtitle = "Director Controls & Expressive VFX",
        description = "Fine-grained camera trajectory control, fluid transitions, motion brush manipulation, and cinematic lighting depth.",
        badgeColor = RunwayBrandColor,
        badgeBgColor = MaleVoiceDarkBg,
        defaultFps = 30,
        motionCapability = "Motion Brush & Orbit",
        latencyScore = "High Precision (30s render)",
        iconName = "runway"
    ),
    LUMA_DREAM(
        id = "luma_dream",
        displayName = "Luma Dream",
        modelTag = "Luma Dream Machine 1.5",
        subtitle = "Hyper-Realistic 3D Depth & Light",
        description = "Exceptional camera motion, smooth perspective shifts, and realistic light ray bouncing in photorealistic environments.",
        badgeColor = LumaBrandColor,
        badgeBgColor = MaleVoiceDarkBg,
        defaultFps = 30,
        motionCapability = "Realistic 3D Flythrough",
        latencyScore = "Fast 3D (22s render)",
        iconName = "luma"
    ),
    KLING_AI(
        id = "kling_ai",
        displayName = "Kling 1.5 HD",
        modelTag = "Kling AI Cinema Studio",
        subtitle = "Action Physics & Temporal Coherence",
        description = "Advanced simulation of human motion, martial dynamics, and high-fidelity object interactions over long multi-shot sequences.",
        badgeColor = KlingBrandColor,
        badgeBgColor = MaleVoiceDarkBg,
        defaultFps = 30,
        motionCapability = "Dynamic Character Motion",
        latencyScore = "Action Grade (25s render)",
        iconName = "kling"
    ),
    PIKA_2(
        id = "pika_2",
        displayName = "Pika 2.0",
        modelTag = "Pika Dynamic FX Studio",
        subtitle = "Playful FX & Scene Transformations",
        description = "Creative particle effects, scene morphing, stylized explosions, and fast dynamic visual iterations.",
        badgeColor = PikaBrandColor,
        badgeBgColor = MaleVoiceDarkBg,
        defaultFps = 30,
        motionCapability = "Particle & Morph FX",
        latencyScore = "Ultra Fast (15s render)",
        iconName = "pika"
    ),
    STABLE_VIDEO(
        id = "stable_video",
        displayName = "Stable Video XL",
        modelTag = "Stability AI SVD-XT Pro",
        subtitle = "Open Diffusion & High Dynamic Range",
        description = "Diffusion-based video generation with rich texture detail, deep shadows, and customizable generative control.",
        badgeColor = StableBrandColor,
        badgeBgColor = MaleVoiceDarkBg,
        defaultFps = 25,
        motionCapability = "Smooth Diffusion Drift",
        latencyScore = "Balanced (24s render)",
        iconName = "stable"
    ),
    AUTO_MULTI(
        id = "auto_multi",
        displayName = "Auto / Multi-Engine",
        modelTag = "Ensemble Studio Suite",
        subtitle = "Intelligent Multi-Shot Blend",
        description = "Orchestrates across Sora, Zero V3, and Nano V2 for each individual scene cut to achieve the ultimate cinematic synthesis.",
        badgeColor = AutoMultiBrandColor,
        badgeBgColor = MaleVoiceDarkBg,
        defaultFps = 30,
        motionCapability = "Multi-Perspective Fusion",
        latencyScore = "Ensemble Mode (32s render)",
        iconName = "auto"
    )
}

enum class MaleVoiceProfile(
    val id: String,
    val speakerName: String,
    val styleTitle: String,
    val vocalTone: String,
    val pitchMultiplier: Float, // <1.0 = deeper male voice
    val speedMultiplier: Float,
    val sampleQuote: String,
    val accentTag: String
) {
    MARCUS_BARITONE(
        id = "marcus",
        speakerName = "Marcus Vance",
        styleTitle = "Deep Cinematic Baritone",
        vocalTone = "Epic movie trailer, gravitas, profound resonance",
        pitchMultiplier = 0.80f,
        speedMultiplier = 0.92f,
        sampleQuote = "In a universe unbounded by time, every shadow whispers a forgotten truth.",
        accentTag = "Deep US Baritone"
    ),
    DAVID_NARRATOR(
        id = "david",
        speakerName = "David Sterling",
        styleTitle = "Prestige Documentarian",
        vocalTone = "Authoritative British documentary, warm, articulate",
        pitchMultiplier = 0.85f,
        speedMultiplier = 0.98f,
        sampleQuote = "Beneath the surface of this extraordinary world lies a delicate harmony millions of years in the making.",
        accentTag = "British Oxford"
    ),
    ALEX_TECH(
        id = "alex",
        speakerName = "Alex Mercer",
        styleTitle = "Visionary Tech Presenter",
        vocalTone = "Crisp, energetic, modern AI keynote style",
        pitchMultiplier = 0.89f,
        speedMultiplier = 1.05f,
        sampleQuote = "This is not just the next frontier of technology. It is the beginning of everything.",
        accentTag = "Crisp US Modern"
    ),
    LIAM_DRAMATIC(
        id = "liam",
        speakerName = "Liam Thorne",
        styleTitle = "Dramatic Noir Storyteller",
        vocalTone = "Suspenseful noir, gritty, emotional intensity",
        pitchMultiplier = 0.83f,
        speedMultiplier = 0.95f,
        sampleQuote = "When the neon faded into rain, there was only one path left to take.",
        accentTag = "Gritty Cinematic"
    ),
    JULIAN_CALM(
        id = "julian",
        speakerName = "Julian Hayes",
        styleTitle = "Calm Ambient Sage",
        vocalTone = "Warm, meditative, deep philosophical presence",
        pitchMultiplier = 0.86f,
        speedMultiplier = 0.88f,
        sampleQuote = "Breathe in the quiet magnitude of the cosmos. Everything arrives at its appointed hour.",
        accentTag = "Deep Ambient"
    ),
    ETHAN_COMMANDER(
        id = "ethan",
        speakerName = "Ethan Cross",
        styleTitle = "Sci-Fi Fleet Commander",
        vocalTone = "Resonant, authoritative, interstellar leadership",
        pitchMultiplier = 0.82f,
        speedMultiplier = 0.96f,
        sampleQuote = "All stations report green. Engage quantum drive and prepare to break through the veil.",
        accentTag = "Command Voice"
    )
}

enum class VideoAspectRatio(
    val id: String,
    val displayName: String,
    val ratioValue: Float,
    val subtitle: String,
    val iconDesc: String
) {
    WIDESCREEN_16_9("16:9", "16:9 Widescreen", 16f / 9f, "YouTube & Cinema", "Horizontal"),
    VERTICAL_9_16("9:16", "9:16 Vertical", 9f / 16f, "Reels, Shorts & TikTok", "Vertical"),
    SQUARE_1_1("1:1", "1:1 Square", 1f, "Social Media", "Square"),
    ANAMORPHIC_239_1("2.39:1", "2.39:1 Anamorphic", 2.39f, "Hollywood Scope", "Ultra-Wide")
}

data class SceneItem(
    val sceneIndex: Int,
    val startTimeSec: Float,
    val endTimeSec: Float,
    val title: String,
    val visualPrompt: String,
    val cameraMovement: String,
    val lightingAndAtmosphere: String,
    val voiceoverText: String,
    val sfxCue: String,
    val visualAssetType: String,
    val colorGrading: String
) {
    val durationSec: Float get() = (endTimeSec - startTimeSec).coerceAtLeast(1f)
}

data class VideoProject(
    val id: Long = 0,
    val title: String,
    val prompt: String,
    val enhancedPrompt: String,
    val engine: VideoEngine,
    val maleVoice: MaleVoiceProfile,
    val targetDurationSeconds: Int = 45, // 40 to 50 seconds
    val aspectRatio: VideoAspectRatio = VideoAspectRatio.WIDESCREEN_16_9,
    val scenes: List<SceneItem>,
    val createdAt: Long = System.currentTimeMillis(),
    val isRendered: Boolean = true,
    val fps: Int = 30,
    val resolution: String = "1080p",
    val backgroundMusicTrack: String = "Cinematic Ambient Wave"
)

data class PromptPreset(
    val title: String,
    val category: String,
    val shortPrompt: String,
    val recommendedEngine: VideoEngine,
    val recommendedVoice: MaleVoiceProfile,
    val durationSec: Int = 45,
    val previewGradientColors: List<Color>
)

package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

object PresetPrompts {
    val presets = listOf(
        PromptPreset(
            title = "Neo-Tokyo Rain Drift",
            category = "Cyberpunk Action",
            shortPrompt = "A high-speed cyber warrior on an anti-gravity bike weaving through rain-slicked skyscrapers of Neo-Tokyo with electric cyan light trails, dramatic male voiceover, 45 seconds",
            recommendedEngine = VideoEngine.ZERO_V3,
            recommendedVoice = MaleVoiceProfile.MARCUS_BARITONE,
            durationSec = 45,
            previewGradientColors = listOf(ZeroV3BrandColor, SleekIndigoAccent)
        ),
        PromptPreset(
            title = "Abyssal Trench Expedition",
            category = "Oceanic Documentary",
            shortPrompt = "Deep-sea research submarine descending into a glowing hydrothermal trench discovering an ancient bioluminescent civilization, authoritative 48s documentary male voice",
            recommendedEngine = VideoEngine.SORA,
            recommendedVoice = MaleVoiceProfile.DAVID_NARRATOR,
            durationSec = 48,
            previewGradientColors = listOf(SoraBrandColor, SleekCyanGlow)
        ),
        PromptPreset(
            title = "Solaris Twin Moon Crossing",
            category = "Sci-Fi Space Odyssey",
            shortPrompt = "A fleet of exploration vessels orbiting floating crystalline islands above a gas giant with rings, 45 seconds stylized Nano V2 animation with calm male narrator",
            recommendedEngine = VideoEngine.NANO_V2,
            recommendedVoice = MaleVoiceProfile.JULIAN_CALM,
            durationSec = 45,
            previewGradientColors = listOf(NanoV2BrandColor, SleekPurple)
        ),
        PromptPreset(
            title = "Quantum Singularity Breach",
            category = "Hard Sci-Fi Cinema",
            shortPrompt = "A futuristic particle accelerator opening a controlled miniature wormhole in orbit around Jupiter, gravitational lensing warping nearby starfields, 50 seconds",
            recommendedEngine = VideoEngine.VEO_2,
            recommendedVoice = MaleVoiceProfile.ALEX_TECH,
            durationSec = 50,
            previewGradientColors = listOf(VeoBrandColor, SleekBlueDark)
        ),
        PromptPreset(
            title = "The Obsidian Dragon Fortress",
            category = "Dark Fantasy",
            shortPrompt = "Armored knights defending a cliffside gothic fortress as an obsidian black dragon swoops through stormy clouds breathing violet plasma fire, 44 seconds cinematic",
            recommendedEngine = VideoEngine.KLING_AI,
            recommendedVoice = MaleVoiceProfile.LIAM_DRAMATIC,
            durationSec = 44,
            previewGradientColors = listOf(KlingBrandColor, SleekGold)
        ),
        PromptPreset(
            title = "Fleet Commander Protocol",
            category = "Military Sci-Fi",
            shortPrompt = "Star dreadnought fleet exiting hyperspace over a shattered alien ringworld, heavy macro turrets rotating into combat formation, 46 seconds male commander voice",
            recommendedEngine = VideoEngine.RUNWAY_GEN3,
            recommendedVoice = MaleVoiceProfile.ETHAN_COMMANDER,
            durationSec = 46,
            previewGradientColors = listOf(RunwayBrandColor, SleekPurple)
        )
    )
}

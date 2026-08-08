package com.example.data.repository

import com.example.data.db.VideoProjectDao
import com.example.data.db.VideoProjectEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class VideoRepository(private val dao: VideoProjectDao) {

    val allProjects: Flow<List<VideoProject>> = dao.getAllProjects().map { entities ->
        entities.map { it.toDomainModel() }
    }

    suspend fun getProjectById(id: Long): VideoProject? {
        return dao.getProjectById(id)?.toDomainModel()
    }

    suspend fun saveProject(project: VideoProject): Long {
        val entity = project.toEntity()
        return dao.insertProject(entity)
    }

    suspend fun deleteProject(id: Long) {
        dao.deleteProjectById(id)
    }

    suspend fun prepopulateIfEmpty() {
        // Provide starter cinematic 45s sample projects
        val starter1 = createDefaultCyberpunkProject()
        val starter2 = createDefaultDeepSeaProject()
        val starter3 = createDefaultSpaceOdysseyProject()
        dao.insertProject(starter1.toEntity())
        dao.insertProject(starter2.toEntity())
        dao.insertProject(starter3.toEntity())
    }

    companion object {
        fun serializeScenes(scenes: List<SceneItem>): String {
            val array = JSONArray()
            scenes.forEach { scene ->
                val obj = JSONObject().apply {
                    put("sceneIndex", scene.sceneIndex)
                    put("startTimeSec", scene.startTimeSec.toDouble())
                    put("endTimeSec", scene.endTimeSec.toDouble())
                    put("title", scene.title)
                    put("visualPrompt", scene.visualPrompt)
                    put("cameraMovement", scene.cameraMovement)
                    put("lightingAndAtmosphere", scene.lightingAndAtmosphere)
                    put("voiceoverText", scene.voiceoverText)
                    put("sfxCue", scene.sfxCue)
                    put("visualAssetType", scene.visualAssetType)
                    put("colorGrading", scene.colorGrading)
                }
                array.put(obj)
            }
            return array.toString()
        }

        fun deserializeScenes(jsonString: String): List<SceneItem> {
            if (jsonString.isBlank()) return emptyList()
            val list = mutableListOf<SceneItem>()
            try {
                val array = JSONArray(jsonString)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        SceneItem(
                            sceneIndex = obj.optInt("sceneIndex", i + 1),
                            startTimeSec = obj.optDouble("startTimeSec", i * 11.0).toFloat(),
                            endTimeSec = obj.optDouble("endTimeSec", (i + 1) * 11.25).toFloat(),
                            title = obj.optString("title", "Scene ${i + 1}"),
                            visualPrompt = obj.optString("visualPrompt", ""),
                            cameraMovement = obj.optString("cameraMovement", "Smooth Cinematic Pan"),
                            lightingAndAtmosphere = obj.optString("lightingAndAtmosphere", "Atmospheric volumetric lighting"),
                            voiceoverText = obj.optString("voiceoverText", ""),
                            sfxCue = obj.optString("sfxCue", "Cinematic drone"),
                            visualAssetType = obj.optString("visualAssetType", "SORA"),
                            colorGrading = obj.optString("colorGrading", "Cinema Grade")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }

        fun createDefaultCyberpunkProject(): VideoProject {
            return VideoProject(
                id = 1,
                title = "Neon Genesis: 2099",
                prompt = "A high-speed cyber warrior rushing through rainy neo-Tokyo on an anti-gravity bike with electric cyan light trails, cinematic 45 seconds male voiceover",
                enhancedPrompt = "Cinematic 45-second neo-Tokyo cyberpunk sequence rendered with Sora physics & Zero V3 kinetic motion. An anti-gravity interceptor accelerates through reflective rain-slicked skyscrapers, neon kanji holograms, and volumetric drone spotlights.",
                engine = VideoEngine.ZERO_V3,
                maleVoice = MaleVoiceProfile.MARCUS_BARITONE,
                targetDurationSeconds = 45,
                aspectRatio = VideoAspectRatio.WIDESCREEN_16_9,
                scenes = listOf(
                    SceneItem(
                        sceneIndex = 1,
                        startTimeSec = 0.0f,
                        endTimeSec = 11.0f,
                        title = "Act I: The Rain-Slick Metropolis",
                        visualPrompt = "Wide drone camera diving between towering neon mega-structures into the rainy streets of Neo-Tokyo. Cyan and magenta reflections glisten on wet asphalt.",
                        cameraMovement = "Downward vertical sweep transitioning into a low-angle tracking shot",
                        lightingAndAtmosphere = "Heavy rainfall, neon signage glow, volumetric street vapor",
                        voiceoverText = "Beneath the perpetual rain of Neo-Tokyo, the old world ended not with a whisper, but with the hum of zero-point fusion.",
                        sfxCue = "Distant thunder, low synthetic bass pulse, rhythmic rain hiss",
                        visualAssetType = "ZERO",
                        colorGrading = "Cyber Cyan & Magenta Contrast"
                    ),
                    SceneItem(
                        sceneIndex = 2,
                        startTimeSec = 11.0f,
                        endTimeSec = 22.0f,
                        title = "Act II: Ignition & Acceleration",
                        visualPrompt = "Close-up on anti-gravity turbine ignition. Electric blue plasma rings flare as the rider revs the engine, holographic HUD overlay illuminating his visor.",
                        cameraMovement = "Orbiting 360-degree close-up around the rear turbine and rider",
                        lightingAndAtmosphere = "High-voltage plasma arcs, sharp specular reflections on chrome",
                        voiceoverText = "Every grid line in this city holds a secret. Every second wasted is an eternity lost in the digital slipstream.",
                        sfxCue = "Turbine spooling up, high-frequency kinetic whine, heartbeat riser",
                        visualAssetType = "ZERO",
                        colorGrading = "High Dynamic Range Electric Blue"
                    ),
                    SceneItem(
                        sceneIndex = 3,
                        startTimeSec = 22.0f,
                        endTimeSec = 34.0f,
                        title = "Act III: The Highway Pursuit",
                        visualPrompt = "Ultra-high speed tracking shot beside the anti-gravity bike weaving through skyway traffic at 300 kph. Light trails blur dramatically.",
                        cameraMovement = "Side-by-side dynamic chase camera with motion blur and subtle camera shake",
                        lightingAndAtmosphere = "Streak lighting from passing monorails, dynamic speed bloom",
                        voiceoverText = "They believed the network was impenetrable. They forgot that human will moves faster than their silicon algorithms.",
                        sfxCue = "Sonic boom crack, roaring anti-grav drive, driving synthwave bassline",
                        visualAssetType = "ZERO",
                        colorGrading = "High Velocity Motion Streaks"
                    ),
                    SceneItem(
                        sceneIndex = 4,
                        startTimeSec = 34.0f,
                        endTimeSec = 45.0f,
                        title = "Act IV: The Horizon Breakthrough",
                        visualPrompt = "The bike leaps across a broken skyway ramp into open airspace overlooking the glowing megalopolis under twin moon clouds.",
                        cameraMovement = "Epic pull-back wide shot as the bike glides in slow motion against the sprawling skyline",
                        lightingAndAtmosphere = "Golden dawn twilight breaking through the upper smog layer",
                        voiceoverText = "Now we breach the perimeter. Welcome to the dawn of a new frequency.",
                        sfxCue = "Sudden silence followed by deep sub-drop and sweeping orchestral chime",
                        visualAssetType = "ZERO",
                        colorGrading = "Deep Midnight to Sunrise Gold"
                    )
                ),
                createdAt = System.currentTimeMillis() - 3600000 * 2,
                isRendered = true,
                fps = 60,
                resolution = "4K UHD",
                backgroundMusicTrack = "Cyberpunk Pulse 128BPM"
            )
        }

        fun createDefaultDeepSeaProject(): VideoProject {
            return VideoProject(
                id = 2,
                title = "Abyssal Discovery: The Sunken Citadel",
                prompt = "A deep sea research submarine descending into a glowing hydrothermal trench discovering an ancient bioluminescent civilization, 48s documentary male voice",
                enhancedPrompt = "48-second oceanic exploration epic in OpenAI Sora photorealism. Deep-water submersible descends through twilight ocean layers into the Mariana Abyss, encountering monumental bioluminescent spires.",
                engine = VideoEngine.SORA,
                maleVoice = MaleVoiceProfile.DAVID_NARRATOR,
                targetDurationSeconds = 48,
                aspectRatio = VideoAspectRatio.WIDESCREEN_16_9,
                scenes = listOf(
                    SceneItem(
                        sceneIndex = 1,
                        startTimeSec = 0.0f,
                        endTimeSec = 12.0f,
                        title = "Scene 1: Descent into the Twilight Zone",
                        visualPrompt = "Research submarine headlights cutting through midnight blue ocean depths. Marine snow particles drift past observation dome.",
                        cameraMovement = "Slow majestic downward tilt following the submarine hull",
                        lightingAndAtmosphere = "Deep oceanic gradient from cobalt to total black, dual xenon floodlights",
                        voiceoverText = "Seven thousand meters beneath the sunlit waves lies the final uncharted realm of our planet.",
                        sfxCue = "Hydrophone ambient drone, sonar ping, pressurized hull creak",
                        visualAssetType = "SORA",
                        colorGrading = "Abyssal Cobalt & Deep Teal"
                    ),
                    SceneItem(
                        sceneIndex = 2,
                        startTimeSec = 12.0f,
                        endTimeSec = 24.0f,
                        title = "Scene 2: The Bioluminescent Reef",
                        visualPrompt = "Floodlights illuminate crystalline coral pillars pulsing with organic emerald and violet light waves.",
                        cameraMovement = "Smooth dolly forward passing between massive glowing hydrothermal chimneys",
                        lightingAndAtmosphere = "Organic phosphorescence, shimmering heat mirage near vents",
                        voiceoverText = "Here, life does not depend on the sun. It flourishes in eternal volcanic warmth, untouched by human history.",
                        sfxCue = "Gentle bubble release, harmonic water resonance, low cello bow",
                        visualAssetType = "SORA",
                        colorGrading = "Bioluminescent Emerald & Indigo"
                    ),
                    SceneItem(
                        sceneIndex = 3,
                        startTimeSec = 24.0f,
                        endTimeSec = 36.0f,
                        title = "Scene 3: The Monumental Gates",
                        visualPrompt = "The submersible turns a trench corner to reveal towering stone arches carved with glowing oceanic glyphs.",
                        cameraMovement = "Slow ascending crane shot revealing the sheer scale of the underwater architecture",
                        lightingAndAtmosphere = "Submersible headlights illuminating intricate carvings in high relief",
                        voiceoverText = "And hidden among the seafloor vents... evidence of an architecture constructed before the dawn of recorded memory.",
                        sfxCue = "Deep orchestral swell, resonant gong chime, water pulse",
                        visualAssetType = "SORA",
                        colorGrading = "Photorealistic Oceanic Contrast"
                    ),
                    SceneItem(
                        sceneIndex = 4,
                        startTimeSec = 36.0f,
                        endTimeSec = 48.0f,
                        title = "Scene 4: The Citadel Awakens",
                        visualPrompt = "A central spire glows with pulsing blue energy rings that ripple across the entire seafloor valley.",
                        cameraMovement = "Epic wide pull-back into the vastness of the glowing abyss",
                        lightingAndAtmosphere = "Bioluminescent pulse lighting up the entire seafloor trench",
                        voiceoverText = "An ancient silent guardian, waiting quietly in the deep for our arrival.",
                        sfxCue = "Grand symphonic resolution, fading sonar ping",
                        visualAssetType = "SORA",
                        colorGrading = "Cinema Grade Deep Water HDR"
                    )
                ),
                createdAt = System.currentTimeMillis() - 3600000 * 8,
                isRendered = true,
                fps = 30,
                resolution = "1080p",
                backgroundMusicTrack = "Deep Oceanic Ambient"
            )
        }

        fun createDefaultSpaceOdysseyProject(): VideoProject {
            return VideoProject(
                id = 3,
                title = "Solaris V: The Twin Moon Crossing",
                prompt = "A fleet of exploration vessels orbiting floating crystalline islands above a gas giant with rings, 45 seconds stylized Nano V2 animation",
                enhancedPrompt = "45-second stylized sci-fi fantasy sequence rendered with Nano V2 Neural engine. Interstellar vessels navigate between floating crystal isles above a golden gas giant ring system.",
                engine = VideoEngine.NANO_V2,
                maleVoice = MaleVoiceProfile.JULIAN_CALM,
                targetDurationSeconds = 45,
                aspectRatio = VideoAspectRatio.WIDESCREEN_16_9,
                scenes = listOf(
                    SceneItem(
                        sceneIndex = 1,
                        startTimeSec = 0.0f,
                        endTimeSec = 11.0f,
                        title = "Scene 1: Orbit of Solaris Prime",
                        visualPrompt = "Floating crystal sky islands suspended above massive golden planetary rings with twin crescent moons in background.",
                        cameraMovement = "Sweeping orbital arc around the floating crystal spires",
                        lightingAndAtmosphere = "Warm golden star rays, crystalline light refractions, soft starry backdrop",
                        voiceoverText = "Beyond the boundaries of our solar cradle, the universe unfolds in quiet, breathtaking majesty.",
                        sfxCue = "Warm ambient synth pad, sparkling chime arpeggio",
                        visualAssetType = "NANO",
                        colorGrading = "Anime Fantasy Pastel & Gold"
                    ),
                    SceneItem(
                        sceneIndex = 2,
                        startTimeSec = 11.0f,
                        endTimeSec = 22.0f,
                        title = "Scene 2: Waterfalls of Starlight",
                        visualPrompt = "Liquid starlight cascading from island cliffs into the endless ring system below, glowing sky whales gliding through clouds.",
                        cameraMovement = "Tracking shot flying alongside soaring luminous creatures",
                        lightingAndAtmosphere = "Soft glowing particles, volumetric pastel clouds",
                        voiceoverText = "Here, gravity is merely a suggestion, and the light itself remembers how to sing.",
                        sfxCue = "Resonant harp, gentle wind glide, ethereal choir",
                        visualAssetType = "NANO",
                        colorGrading = "Vibrant Sky Blue & Rose Gold"
                    ),
                    SceneItem(
                        sceneIndex = 3,
                        startTimeSec = 22.0f,
                        endTimeSec = 34.0f,
                        title = "Scene 3: The Explorer Vessel",
                        visualPrompt = "A sleek white exploration vessel with solar sails glides silently between twin crystal waterfalls.",
                        cameraMovement = "Close flyby of the exploration ship with cockpit light glowing softly",
                        lightingAndAtmosphere = "Solar sail luminescence, prismatic crystal flares",
                        voiceoverText = "We travel not to conquer these distant skies, but to remember who we were destined to become.",
                        sfxCue = "Soft propulsion hum, warm piano chords",
                        visualAssetType = "NANO",
                        colorGrading = "High Key Stylized Crisp 3D"
                    ),
                    SceneItem(
                        sceneIndex = 4,
                        startTimeSec = 34.0f,
                        endTimeSec = 45.0f,
                        title = "Scene 4: The Endless Horizon",
                        visualPrompt = "The fleet convenes as the gas giant sets against the twin moons, casting a glorious sunset across the sky realm.",
                        cameraMovement = "Grand ascending camera into the cosmic twilight",
                        lightingAndAtmosphere = "Dual moon glow with sunset gradient across planetary rings",
                        voiceoverText = "Look upward, and rest easy. The cosmos is waiting for you.",
                        sfxCue = "Peaceful ambient fadeout with deep resonant chord",
                        visualAssetType = "NANO",
                        colorGrading = "Cosmic Sunset & Twin Moon Violet"
                    )
                ),
                createdAt = System.currentTimeMillis() - 3600000 * 24,
                isRendered = true,
                fps = 24,
                resolution = "1080p",
                backgroundMusicTrack = "Cosmic Meditation Tone"
            )
        }
    }
}

private fun VideoProjectEntity.toDomainModel(): VideoProject {
    val engine = when (engineName) {
        "SORA" -> VideoEngine.SORA
        "ZERO_V3" -> VideoEngine.ZERO_V3
        "NANO_V2" -> VideoEngine.NANO_V2
        else -> VideoEngine.AUTO_MULTI
    }

    val voice = when (maleVoiceId) {
        "marcus" -> MaleVoiceProfile.MARCUS_BARITONE
        "david" -> MaleVoiceProfile.DAVID_NARRATOR
        "alex" -> MaleVoiceProfile.ALEX_TECH
        "liam" -> MaleVoiceProfile.LIAM_DRAMATIC
        "julian" -> MaleVoiceProfile.JULIAN_CALM
        else -> MaleVoiceProfile.MARCUS_BARITONE
    }

    val ratio = when (aspectRatioName) {
        "VERTICAL_9_16" -> VideoAspectRatio.VERTICAL_9_16
        "SQUARE_1_1" -> VideoAspectRatio.SQUARE_1_1
        "ANAMORPHIC_239_1" -> VideoAspectRatio.ANAMORPHIC_239_1
        else -> VideoAspectRatio.WIDESCREEN_16_9
    }

    val sceneList = VideoRepository.deserializeScenes(scenesJson)

    return VideoProject(
        id = id,
        title = title,
        prompt = userPrompt,
        enhancedPrompt = enhancedPrompt,
        engine = engine,
        maleVoice = voice,
        targetDurationSeconds = durationSeconds,
        aspectRatio = ratio,
        scenes = sceneList,
        createdAt = createdAt,
        isRendered = isRendered,
        fps = fps,
        resolution = resolution,
        backgroundMusicTrack = bgMusicTrack
    )
}

private fun VideoProject.toEntity(): VideoProjectEntity {
    return VideoProjectEntity(
        id = id,
        title = title,
        userPrompt = prompt,
        enhancedPrompt = enhancedPrompt,
        engineName = engine.name,
        maleVoiceId = maleVoice.id,
        durationSeconds = targetDurationSeconds,
        aspectRatioName = aspectRatio.name,
        scenesJson = VideoRepository.serializeScenes(scenes),
        createdAt = createdAt,
        isRendered = isRendered,
        fps = fps,
        resolution = resolution,
        bgMusicTrack = backgroundMusicTrack
    )
}

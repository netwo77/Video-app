package com.example.data.generator

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

class VideoDirectorService {

    suspend fun generateCompleteVideoProject(
        userPrompt: String,
        engine: VideoEngine,
        maleVoice: MaleVoiceProfile,
        targetDurationSeconds: Int = 45,
        aspectRatio: VideoAspectRatio = VideoAspectRatio.WIDESCREEN_16_9
    ): VideoProject = withContext(Dispatchers.IO) {
        val duration = targetDurationSeconds.coerceIn(40, 50)
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        var projectResult: VideoProject? = null

        if (apiKey.isNotBlank() && !apiKey.contains("MY_GEMINI_API_KEY") && !apiKey.contains("TODO")) {
            try {
                projectResult = callGeminiDirectorApi(apiKey, userPrompt, engine, maleVoice, duration, aspectRatio)
            } catch (e: Exception) {
                Log.e("VideoDirectorService", "Gemini API error, falling back to local director: ${e.message}")
            }
        }

        if (projectResult == null) {
            projectResult = synthesizeIntelligentStoryboard(userPrompt, engine, maleVoice, duration, aspectRatio)
        }

        projectResult
    }

    private fun callGeminiDirectorApi(
        apiKey: String,
        userPrompt: String,
        engine: VideoEngine,
        maleVoice: MaleVoiceProfile,
        duration: Int,
        aspectRatio: VideoAspectRatio
    ): VideoProject? {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        val url = URL(endpoint)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        connection.connectTimeout = 15000
        connection.readTimeout = 15000

        val systemPrompt = """
            You are a master Hollywood AI Video Director and Voiceover Producer specialized in $engine and Deep Male Voice Narration.
            The user wants to convert their prompt into a complete $duration-second cinematic video.
            Return a JSON object with:
            - "title": A cinematic short title (3-6 words)
            - "enhancedPrompt": A high-end director prompt detailing physics, camera path, and resolution
            - "scenes": Array of exactly 4 scenes spanning from 0 to $duration seconds.
              Each scene object must contain:
              - "sceneIndex": 1 to 4
              - "startTimeSec": number
              - "endTimeSec": number
              - "title": string
              - "visualPrompt": detailed scene visual instruction
              - "cameraMovement": specific camera technique (e.g. 360 Orbit, Low-angle Dolly, Crane descent)
              - "lightingAndAtmosphere": volumetric lighting, weather, fog, specular bloom
              - "voiceoverText": dramatic male voiceover line (approx 15-25 words for this segment)
              - "sfxCue": audio and sound effect cues
              - "colorGrading": specific color grading palette
            Output ONLY raw JSON with no markdown formatting.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "System: $systemPrompt\nUser Prompt: $userPrompt\nEngine: ${engine.displayName}\nVoice: ${maleVoice.speakerName} (${maleVoice.styleTitle})")
                        })
                    })
                })
            }
            put("contents", contentsArray)
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
            })
        }

        OutputStreamWriter(connection.outputStream).use { it.write(jsonBody.toString()) }

        val responseCode = connection.responseCode
        if (responseCode == 200) {
            val responseText = connection.inputStream.bufferedReader().use { it.readText() }
            val root = JSONObject(responseText)
            val candidates = root.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val text = candidates.getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                val parsed = JSONObject(text.trim())
                val title = parsed.optString("title", "Cinematic Vision")
                val enhancedPrompt = parsed.optString("enhancedPrompt", userPrompt)
                val scenesArray = parsed.optJSONArray("scenes") ?: JSONArray()
                val scenes = mutableListOf<SceneItem>()

                for (i in 0 until scenesArray.length()) {
                    val sc = scenesArray.getJSONObject(i)
                    scenes.add(
                        SceneItem(
                            sceneIndex = sc.optInt("sceneIndex", i + 1),
                            startTimeSec = sc.optDouble("startTimeSec", (i * (duration / 4.0))).toFloat(),
                            endTimeSec = sc.optDouble("endTimeSec", ((i + 1) * (duration / 4.0))).toFloat(),
                            title = sc.optString("title", "Scene ${i + 1}"),
                            visualPrompt = sc.optString("visualPrompt", ""),
                            cameraMovement = sc.optString("cameraMovement", "Smooth Cinematic Pan"),
                            lightingAndAtmosphere = sc.optString("lightingAndAtmosphere", "Volumetric cinema lighting"),
                            voiceoverText = sc.optString("voiceoverText", ""),
                            sfxCue = sc.optString("sfxCue", "Low cinema pulse"),
                            visualAssetType = engine.id.uppercase(),
                            colorGrading = sc.optString("colorGrading", "Cinema Contrast")
                        )
                    )
                }

                return VideoProject(
                    title = title,
                    prompt = userPrompt,
                    enhancedPrompt = enhancedPrompt,
                    engine = engine,
                    maleVoice = maleVoice,
                    targetDurationSeconds = duration,
                    aspectRatio = aspectRatio,
                    scenes = scenes,
                    fps = engine.defaultFps,
                    resolution = "4K UHD Master"
                )
            }
        }
        return null
    }

    fun synthesizeIntelligentStoryboard(
        userPrompt: String,
        engine: VideoEngine,
        maleVoice: MaleVoiceProfile,
        duration: Int,
        aspectRatio: VideoAspectRatio
    ): VideoProject {
        val cleanPrompt = userPrompt.trim()
        val title = generateCinematicTitle(cleanPrompt)
        val s1End = (duration * 0.24f)
        val s2End = (duration * 0.50f)
        val s3End = (duration * 0.76f)
        val s4End = duration.toFloat()

        val enhancedPrompt = "Cinematic $duration-second production in ${engine.displayName}. " +
                "Photorealistic volumetric rendering with dynamic male voice narration (${maleVoice.speakerName}). " +
                "Composition: $cleanPrompt. Mastered at ${engine.defaultFps}fps with native physics and temporal coherence."

        val scenes = listOf(
            SceneItem(
                sceneIndex = 1,
                startTimeSec = 0f,
                endTimeSec = s1End,
                title = "Act I: The Genesis of $title",
                visualPrompt = "Establishing wide cinematic master shot. Volumetric atmosphere envelopes $cleanPrompt. Camera executes a majestic slow descent highlighting initial texture fidelity and deep contrast.",
                cameraMovement = "Slow descending crane shot transitioning into an ultra-wide anamorphic push",
                lightingAndAtmosphere = "Volumetric haze, soft directional rim lighting, and atmospheric dust particulate float",
                voiceoverText = "In the stillness before the world began, the vision took shape—not by chance, but by deliberate creation.",
                sfxCue = "Deep sub-bass swell (35Hz), distant ethereal wind, resonant low cello note",
                visualAssetType = engine.name,
                colorGrading = "Anamorphic Teal & Obsidian Shadow"
            ),
            SceneItem(
                sceneIndex = 2,
                startTimeSec = s1End,
                endTimeSec = s2End,
                title = "Act II: Momentum & Discovery",
                visualPrompt = "Dynamic camera track weaving closer into the heart of $cleanPrompt. Detailed micro-surface textures and rapid fluid particles react with dynamic physics.",
                cameraMovement = "Low-angle dynamic tracking shot with smooth 45-degree rotational orbit",
                lightingAndAtmosphere = "Sharp specular edge highlights, golden hour rays piercing through atmospheric mist",
                voiceoverText = "Every detail carries weight. Every frequency resonates with the silent power of a universe in motion.",
                sfxCue = "Accelerating mechanical turbine hum, crisp stereo riser, kinetic pulse",
                visualAssetType = engine.name,
                colorGrading = "High Dynamic Range High-Contrast Neon"
            ),
            SceneItem(
                sceneIndex = 3,
                startTimeSec = s2End,
                endTimeSec = s3End,
                title = "Act III: The Climax of Force",
                visualPrompt = "High-octane camera sweep alongside the focal subject of $cleanPrompt. Explosive visual depth, light rays, and kinetic velocity stretching across the frame.",
                cameraMovement = "Hyper-velocity parallel dolly tracking shot with subtle cinematic camera shake",
                lightingAndAtmosphere = "Blinding lens flare, high-intensity plasma illumination, deep ambient shadow",
                voiceoverText = "Here, boundaries dissolve. What was once imagination breaks free into absolute, undeniable reality.",
                sfxCue = "Massive orchestral brass hit, sonic boom crack, roaring synthesizer bassline",
                visualAssetType = engine.name,
                colorGrading = "Hyper-Saturated Gold & Deep Indigo"
            ),
            SceneItem(
                sceneIndex = 4,
                startTimeSec = s3End,
                endTimeSec = s4End,
                title = "Act IV: The Eternal Horizon",
                visualPrompt = "Grand panoramic pull-back revealing the breathtaking totality of $cleanPrompt against an expansive horizon under sweeping clouds.",
                cameraMovement = "Epic crane ascent pulling away into an infinite cinematic vista",
                lightingAndAtmosphere = "Sunset dusk twilight gradient, soft golden rim glow, starry sky emergence",
                voiceoverText = "Stand at the edge of the new frontier. The story is only beginning.",
                sfxCue = "Sustained peaceful orchestral chime, gentle ocean-like fade, warm sub resolve",
                visualAssetType = engine.name,
                colorGrading = "Cinema Master Grade 4K"
            )
        )

        return VideoProject(
            title = title,
            prompt = cleanPrompt,
            enhancedPrompt = enhancedPrompt,
            engine = engine,
            maleVoice = maleVoice,
            targetDurationSeconds = duration,
            aspectRatio = aspectRatio,
            scenes = scenes,
            fps = engine.defaultFps,
            resolution = "4K Master"
        )
    }

    private fun generateCinematicTitle(prompt: String): String {
        val words = prompt.split(" ").filter { it.isNotBlank() }
        if (words.size <= 3) return prompt.capitalizeWords()
        val keywords = words.filter { it.length > 4 && !it.equals("about", true) && !it.equals("video", true) && !it.equals("seconds", true) }
        return if (keywords.size >= 2) {
            "${keywords[0].capitalizeWords()} ${keywords[1].capitalizeWords()}: Odyssey"
        } else {
            "${words.take(3).joinToString(" ").capitalizeWords()}"
        }
    }

    private fun String.capitalizeWords(): String =
        this.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
}

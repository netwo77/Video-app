package com.example.data.gemini

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiVideoService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun optimizePrompt(rawPrompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
                val systemPrompt = "You are an expert Hollywood cinematographer and prompt director. Enhance the user's video prompt into a rich, photorealistic, 40-50 second sequence description with specific camera trajectories, lighting, and volumetric atmosphere. Keep it concise (1-2 powerful sentences)."

                val jsonPayload = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", "Direct this video concept: $rawPrompt")
                                })
                            })
                        })
                    })
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", systemPrompt)
                            })
                        })
                    })
                }

                val request = Request.Builder()
                    .url(url)
                    .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (body != null) {
                        val root = JSONObject(body)
                        val text = root.optJSONArray("candidates")
                            ?.optJSONObject(0)
                            ?.optJSONObject("content")
                            ?.optJSONArray("parts")
                            ?.optJSONObject(0)
                            ?.optString("text")
                        if (!text.isNullOrBlank()) {
                            return@withContext text.trim().removeSurrounding("\"")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("GeminiVideoService", "Prompt optimization fallback: ${e.message}")
            }
        }

        // Procedural prompt enhancement
        return@withContext "Cinematic 8K masterpiece: ${rawPrompt.trim()}. 35mm anamorphic lens, volumetric light refractions, photorealistic physical dynamics, and rich atmospheric depth."
    }

    suspend fun generate4ActStoryboard(
        prompt: String,
        engine: VideoEngine,
        maleVoice: MaleVoiceProfile,
        totalDurationSec: Int
    ): List<SceneItem> = withContext(Dispatchers.IO) {
        val duration = totalDurationSec.coerceIn(40, 50)
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val scenes = callGeminiForStoryboard(prompt, engine, maleVoice, duration)
                if (!scenes.isNullOrEmpty()) {
                    return@withContext scenes
                }
            } catch (e: Exception) {
                Log.w("GeminiVideoService", "Gemini storyboard fallback: ${e.message}")
            }
        }

        return@withContext generateProceduralScenes(prompt, engine, maleVoice, duration)
    }

    private fun callGeminiForStoryboard(
        prompt: String,
        engine: VideoEngine,
        voice: MaleVoiceProfile,
        duration: Int
    ): List<SceneItem>? {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val systemInstruction = """
            You are Omni Video Studio's AI Director. Generate 4 sequential cinematic acts for a $duration-second video.
            Engine: ${engine.displayName}.
            Narrator: ${voice.speakerName} (${voice.vocalTone}) - MALE VOICE ONLY.
            Respond ONLY with a valid JSON array of 4 scenes matching:
            [
              {
                "sceneIndex": 1,
                "startTimeSec": 0.0,
                "endTimeSec": 11.25,
                "title": "Act I: Establishing Shot",
                "visualPrompt": "Visual details for rendering",
                "cameraMovement": "Camera motion",
                "lightingAndAtmosphere": "Lighting description",
                "voiceoverText": "15-20 words male voiceover script",
                "sfxCue": "Sound effect design",
                "visualAssetType": "${engine.name}",
                "colorGrading": "LUT and color grade"
              }
            ]
        """.trimIndent()

        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Generate 4 acts for $duration seconds: $prompt")
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemInstruction)
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.7)
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return null

        val responseBody = response.body?.string() ?: return null
        val rootObj = JSONObject(responseBody)
        val text = rootObj.optJSONArray("candidates")
            ?.optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
            ?.optJSONObject(0)
            ?.optString("text") ?: return null

        val array = JSONArray(text)
        val scenes = mutableListOf<SceneItem>()
        val step = duration / 4.0f
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            scenes.add(
                SceneItem(
                    sceneIndex = obj.optInt("sceneIndex", i + 1),
                    startTimeSec = obj.optDouble("startTimeSec", (i * step).toDouble()).toFloat(),
                    endTimeSec = obj.optDouble("endTimeSec", ((i + 1) * step).toDouble()).toFloat(),
                    title = obj.optString("title", "Act ${i + 1}"),
                    visualPrompt = obj.optString("visualPrompt", prompt),
                    cameraMovement = obj.optString("cameraMovement", "Cinematic Motion"),
                    lightingAndAtmosphere = obj.optString("lightingAndAtmosphere", "Atmospheric volumetric lighting"),
                    voiceoverText = obj.optString("voiceoverText", ""),
                    sfxCue = obj.optString("sfxCue", "Cinematic drone"),
                    visualAssetType = engine.name,
                    colorGrading = obj.optString("colorGrading", "Cinema LUT")
                )
            )
        }
        return scenes
    }

    private fun generateProceduralScenes(
        prompt: String,
        engine: VideoEngine,
        voice: MaleVoiceProfile,
        duration: Int
    ): List<SceneItem> {
        val step = duration / 4.0f
        val cleanPrompt = if (prompt.isBlank()) "Cinematic horizon" else prompt.trim()

        return listOf(
            SceneItem(
                sceneIndex = 1,
                startTimeSec = 0f,
                endTimeSec = step,
                title = "Act I: Atmosphere & Establishing Vista",
                visualPrompt = "Grand sweeping panoramic opening of $cleanPrompt with deep atmospheric depth and pristine lighting physics.",
                cameraMovement = "Majestic crane descend shifting into slow forward dolly",
                lightingAndAtmosphere = "Volumetric ambient fog with golden hour highlights and rim lighting",
                voiceoverText = "In the boundless quiet of this new frontier, every frame reveals a world waiting to be discovered.",
                sfxCue = "Subtle cinematic low rumble, distant wind resonance, rising synth pad",
                visualAssetType = engine.name,
                colorGrading = "Rich Shadow Contrast & Volumetric Warmth"
            ),
            SceneItem(
                sceneIndex = 2,
                startTimeSec = step,
                endTimeSec = step * 2f,
                title = "Act II: Kinetic Acceleration & Motion",
                visualPrompt = "Dynamic tracking shot capturing the velocity and intricate details of $cleanPrompt with refractive particle trails.",
                cameraMovement = "High-speed 360-degree orbiting camera tracking subject at velocity",
                lightingAndAtmosphere = "Dynamic motion streaks, specular chrome refractions, neon illumination",
                voiceoverText = "Energy gathers in the slipstream. Motion transforms thought into unstoppable reality.",
                sfxCue = "Spooling kinetic turbine, sonic whoosh, rhythmic bass heartbeat",
                visualAssetType = engine.name,
                colorGrading = "High Velocity Electric Cyan & Luminescence"
            ),
            SceneItem(
                sceneIndex = 3,
                startTimeSec = step * 2f,
                endTimeSec = step * 3f,
                title = "Act III: The Climax & Core Revelation",
                visualPrompt = "Intense macro close-up and scale revelation of $cleanPrompt in photorealistic 8K resolution.",
                cameraMovement = "Dynamic whip pan transitioning into steady macro floating lens",
                lightingAndAtmosphere = "High-contrast dramatic key light with chromatic prism flares",
                voiceoverText = "Look closer. What seemed impossible from afar is now within our immediate grasp.",
                sfxCue = "Grand orchestral brass swell, crystal chime resonance, deep drop",
                visualAssetType = engine.name,
                colorGrading = "Vivid Dynamic Range & Deep Obsidian Blacks"
            ),
            SceneItem(
                sceneIndex = 4,
                startTimeSec = step * 3f,
                endTimeSec = duration.toFloat(),
                title = "Act IV: Horizon Horizon & Resolution",
                visualPrompt = "Epic wide pull-back shot framing $cleanPrompt under the vast expanse of the horizon skyline.",
                cameraMovement = "Slow majestic ascending crane pull-back into the twilight sky",
                lightingAndAtmosphere = "Sunset gradient shifting from golden amber into deep midnight violet",
                voiceoverText = "This is not merely a destination. It is the beginning of everything that follows.",
                sfxCue = "Resonant fading cello bow, warm ambient harmonic chord, gentle breath",
                visualAssetType = engine.name,
                colorGrading = "Cinema Master Gold to Midnight Violet Grade"
            )
        )
    }
}

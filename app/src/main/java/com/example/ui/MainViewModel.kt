package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.generator.VideoDirectorService
import com.example.data.model.*
import com.example.data.repository.VideoRepository
import com.example.data.tts.MaleVoiceService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VideoRepository
    private val directorService = VideoDirectorService()
    val voiceService: MaleVoiceService = MaleVoiceService(application)

    init {
        val db = AppDatabase.getInstance(application)
        repository = VideoRepository(db.videoProjectDao())
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    val allProjects: StateFlow<List<VideoProject>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Studio Input State (Sleek Interface default prompt)
    var currentPrompt = MutableStateFlow("A cinematic tracking shot of a futuristic neon city submerged in bioluminescent water, 4k, hyper-realistic, dark atmosphere.")
    var selectedEngine = MutableStateFlow(VideoEngine.SORA)
    var selectedMaleVoice = MutableStateFlow(MaleVoiceProfile.MARCUS_BARITONE)
    var targetDurationSeconds = MutableStateFlow(45) // 40 to 50 seconds
    var selectedAspectRatio = MutableStateFlow(VideoAspectRatio.WIDESCREEN_16_9)

    // Active Project & Playback State
    private val _activeProject = MutableStateFlow<VideoProject?>(null)
    val activeProject: StateFlow<VideoProject?> = _activeProject.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTimeSec = MutableStateFlow(0f)
    val currentTimeSec: StateFlow<Float> = _currentTimeSec.asStateFlow()

    private val _isVoiceEnabled = MutableStateFlow(true)
    val isVoiceEnabled: StateFlow<Boolean> = _isVoiceEnabled.asStateFlow()

    // Generation Progress State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationProgress = MutableStateFlow(0)
    val generationProgress: StateFlow<Int> = _generationProgress.asStateFlow()

    private val _generationStageText = MutableStateFlow("Initializing Video Engine...")
    val generationStageText: StateFlow<String> = _generationStageText.asStateFlow()

    // Navigation Tab (0: Create / Studio, 1: Player & Storyboard, 2: Gallery, 3: Narrator Studio)
    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private var playbackJob: Job? = null
    private var lastNarratedSceneIndex = -1

    init {
        // Automatically load the first project as active when available
        viewModelScope.launch {
            allProjects.collect { list ->
                if (_activeProject.value == null && list.isNotEmpty()) {
                    _activeProject.value = list.first()
                }
            }
        }
    }

    fun setActiveTab(tabIndex: Int) {
        _activeTab.value = tabIndex
    }

    fun updatePrompt(newPrompt: String) {
        currentPrompt.value = newPrompt
    }

    fun setEngine(engine: VideoEngine) {
        selectedEngine.value = engine
    }

    fun setMaleVoice(voice: MaleVoiceProfile) {
        selectedMaleVoice.value = voice
        voiceService.findAndApplyMaleVoice(voice)
    }

    fun setTargetDuration(seconds: Int) {
        targetDurationSeconds.value = seconds.coerceIn(40, 50)
    }

    fun setAspectRatio(ratio: VideoAspectRatio) {
        selectedAspectRatio.value = ratio
    }

    fun applyPreset(preset: PromptPreset) {
        currentPrompt.value = preset.shortPrompt
        selectedEngine.value = preset.recommendedEngine
        selectedMaleVoice.value = preset.recommendedVoice
        targetDurationSeconds.value = preset.durationSec
    }

    fun generateVideo() {
        val prompt = currentPrompt.value.trim()
        if (prompt.isBlank()) return

        _isGenerating.value = true
        _generationProgress.value = 5
        _generationStageText.value = "Parsing prompt semantics & kinematics..."
        stopPlayback()

        viewModelScope.launch {
            try {
                // Step 1: Prompt Analysis
                delay(600)
                _generationProgress.value = 25
                _generationStageText.value = "Synthesizing deep male voice script (${selectedMaleVoice.value.speakerName})..."

                // Step 2: Storyboard & Scene Planning
                delay(700)
                _generationProgress.value = 55
                _generationStageText.value = "Generating 4-scene volumetric raytracing in ${selectedEngine.value.displayName}..."

                val project = directorService.generateCompleteVideoProject(
                    userPrompt = prompt,
                    engine = selectedEngine.value,
                    maleVoice = selectedMaleVoice.value,
                    targetDurationSeconds = targetDurationSeconds.value,
                    aspectRatio = selectedAspectRatio.value
                )

                // Step 3: Render Master Pipeline
                delay(800)
                _generationProgress.value = 85
                _generationStageText.value = "Mastering 1080p video with 30fps temporal coherence..."

                delay(600)
                _generationProgress.value = 100
                _generationStageText.value = "Render complete! Loading player..."

                val savedId = repository.saveProject(project)
                val savedProject = project.copy(id = savedId)
                _activeProject.value = savedProject

                delay(400)
                _isGenerating.value = false
                _activeTab.value = 1 // Switch to player
                startPlayback()
            } catch (e: Exception) {
                e.printStackTrace()
                _isGenerating.value = false
            }
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    fun startPlayback() {
        val proj = _activeProject.value ?: return
        val maxDuration = proj.targetDurationSeconds.toFloat()

        if (_currentTimeSec.value >= maxDuration - 0.5f) {
            _currentTimeSec.value = 0f
            lastNarratedSceneIndex = -1
        }

        _isPlaying.value = true
        startPlaybackLoop(maxDuration)
    }

    fun pausePlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
        voiceService.stop()
    }

    fun stopPlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
        _currentTimeSec.value = 0f
        lastNarratedSceneIndex = -1
        voiceService.stop()
    }

    fun seekTo(seconds: Float) {
        val proj = _activeProject.value ?: return
        val clamped = seconds.coerceIn(0f, proj.targetDurationSeconds.toFloat())
        _currentTimeSec.value = clamped

        // Check if we jumped to a new scene
        val currentScene = proj.scenes.find { clamped >= it.startTimeSec && clamped < it.endTimeSec }
        if (currentScene != null && currentScene.sceneIndex != lastNarratedSceneIndex) {
            lastNarratedSceneIndex = currentScene.sceneIndex
            if (_isVoiceEnabled.value && _isPlaying.value) {
                voiceService.speakNarration(currentScene.voiceoverText, proj.maleVoice)
            }
        }
    }

    fun toggleVoice() {
        _isVoiceEnabled.value = !_isVoiceEnabled.value
        if (!_isVoiceEnabled.value) {
            voiceService.stop()
        } else if (_isPlaying.value) {
            val proj = _activeProject.value
            val currentScene = proj?.scenes?.find { _currentTimeSec.value >= it.startTimeSec && _currentTimeSec.value < it.endTimeSec }
            if (currentScene != null) {
                voiceService.speakNarration(currentScene.voiceoverText, proj.maleVoice)
            }
        }
    }

    fun previewVoiceSample(voice: MaleVoiceProfile) {
        selectedMaleVoice.value = voice
        voiceService.speakNarration(voice.sampleQuote, voice, "preview_${voice.id}")
    }

    fun selectProject(project: VideoProject) {
        stopPlayback()
        _activeProject.value = project
        selectedEngine.value = project.engine
        selectedMaleVoice.value = project.maleVoice
        targetDurationSeconds.value = project.targetDurationSeconds
        _activeTab.value = 1 // Switch to player
        startPlayback()
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            repository.deleteProject(id)
            if (_activeProject.value?.id == id) {
                _activeProject.value = allProjects.value.firstOrNull { it.id != id }
            }
        }
    }

    private fun startPlaybackLoop(maxDuration: Float) {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val tickRateMs = 50L
            while (_isPlaying.value && _currentTimeSec.value < maxDuration) {
                delay(tickRateMs)
                val newTime = _currentTimeSec.value + (tickRateMs / 1000f)
                _currentTimeSec.value = newTime

                // Handle synchronized male voiceover for scene transitions
                val proj = _activeProject.value
                if (proj != null && _isVoiceEnabled.value) {
                    val scene = proj.scenes.find { newTime >= it.startTimeSec && newTime < it.endTimeSec }
                    if (scene != null && scene.sceneIndex != lastNarratedSceneIndex) {
                        lastNarratedSceneIndex = scene.sceneIndex
                        voiceService.speakNarration(scene.voiceoverText, proj.maleVoice, "scene_${scene.sceneIndex}")
                    }
                }
            }

            if (_currentTimeSec.value >= maxDuration) {
                _isPlaying.value = false
                _currentTimeSec.value = maxDuration
                voiceService.stop()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceService.release()
    }
}

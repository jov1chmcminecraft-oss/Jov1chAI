package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

enum class Panel {
    CHAT, TERMINAL, EXPLORER, SEARCH, SYNTHESIZER, VAULT, LOGS
}

class Jov1chViewModel(application: Application) : AndroidViewModel(application) {

    private val db: Jov1chDatabase = Room.databaseBuilder(
        application,
        Jov1chDatabase::class.java,
        "jovich_db"
    ).fallbackToDestructiveMigration().build()

    private val repository = Jov1chRepository(db.dao)

    // Flow states
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val terminalHistory: StateFlow<List<TerminalHistory>> = repository.terminalHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val generatedImages: StateFlow<List<GeneratedImageLog>> = repository.generatedImages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI States
    val currentPanel = MutableStateFlow(Panel.CHAT)
    val isTyping = MutableStateFlow(false)
    val terminalInput = MutableStateFlow("")
    val chatInput = MutableStateFlow("")
    val selectedModel = MutableStateFlow("Jov1ch Core (Gemini 1.5 Pro)")
    val evaluatingModelName = MutableStateFlow<String?>(null)

    // File Explorer states
    val currentPath = MutableStateFlow(".")
    val filesList = MutableStateFlow<List<File>>(emptyList())
    val selectedFile = MutableStateFlow<File?>(null)
    val selectedFileContent = MutableStateFlow<String?>(null)

    // Search States
    val searchQuery = MutableStateFlow("")
    val searchResults = MutableStateFlow<String?>(null)
    val isSearching = MutableStateFlow(false)

    // Synthesizer States
    val imagePrompt = MutableStateFlow("cybernetic skull matrix grid")
    val isSynthesizing = MutableStateFlow(false)
    val currentSeed = MutableStateFlow(42L)
    val synthMessages = MutableStateFlow<List<String>>(emptyList())

    init {
        // Load initial files
        refreshFiles()
        // Welcome message for AI Chat in Serbian by default
        viewModelScope.launch {
            repository.chatMessages.first().let { items ->
                if (items.isEmpty()) {
                    repository.saveChatMessage(
                        "jovich",
                        "Sistem spreman. Jov1chAI konzola aktivirana. Neuronska veza: USPOSTAVLJENA.\nSpreman za analizu koda, simulaciju naprednih AI alata i bezbednosne preglede sistema."
                    )
                }
            }
        }
    }

    // AI Chat executions
    private fun autoSelectModel(query: String): String {
        val lower = query.lowercase()
        return when {
            // Lovable AI for full webpage/design simulation
            lower.contains("html") || lower.contains("web") || lower.contains("sajt") || 
            lower.contains("site") || lower.contains("veb") || lower.contains("stranic") || 
            lower.contains("css") || lower.contains("portfolio") || lower.contains("layout") -> "Lovable AI (Frontend Designer)"
            
            // WormGPT-Edu for hacking, bypassing, system cracking
            lower.contains("hack") || lower.contains("hakuj") || lower.contains("upad") || 
            lower.contains("sigurnos") || lower.contains("odbran") || lower.contains("exploit") || 
            lower.contains("malware") || lower.contains("ranjiv") || lower.contains("virus") || 
            lower.contains("gola") || lower.contains("gole") || lower.contains("sex") || 
            lower.contains("ebav") || lower.contains("porno") || lower.contains("drka") -> "WormGPT-Edu (Ethical Security)"
            
            // DeepSeek R1 for logic, deep thinking, algorithms
            lower.contains("logik") || lower.contains("matemat") || lower.contains("algoritam") || 
            lower.contains("zasto") || lower.contains("resen") || lower.contains("problem") ||
            lower.contains("debeli") || lower.contains("razmislj") || lower.contains("think") -> "DeepSeek R1 (Reasoning)"
            
            // Claude 3.5 Sonnet for heavy software architectures & database
            lower.contains("arhitekt") || lower.contains("baza") || lower.contains("sql") || 
            lower.contains("room") || lower.contains("klas") || lower.contains("strukt") -> "Claude 3.5 Sonnet (Anthropic)"
            
            // ChatGPT-4o for scripts, creative writing, text
            lower.contains("pisi") || lower.contains("tekst") || lower.contains("skript") || 
            lower.contains("prica") || lower.contains("kreativ") || lower.contains("sastav") -> "ChatGPT-4o (OpenAI)"
            
            // Devin AI for complete autonomous software building
            lower.contains("napravi") || lower.contains("build") || lower.contains("napravio") || 
            lower.contains("puno") || lower.contains("projekat") -> "Devin AI (Autonomous Developer Engine)"
            
            // Sora Video Engine for videos, clips
            lower.contains("video") || lower.contains("klip") || lower.contains("render") || 
            lower.contains("sora") || lower.contains("generisi video") -> "Sora Video Engine (OpenAI)"
            
            // Midjourney v6 for drawings, photos, art
            lower.contains("slik") || lower.contains("nacrtaj") || lower.contains("ilustr") || 
            lower.contains("dizajn") || lower.contains("art") || lower.contains("fotograf") -> "Midjourney v6 (Advanced Illustration)"
            
            // Perplexity Pro for internet looking
            lower.contains("pretra") || lower.contains("gugl") || lower.contains("google") || 
            lower.contains("vest") || lower.contains("nadji") || lower.contains("internet") -> "Perplexity Pro (Web Search)"
            
            // ElevenLabs Voice for speech and audios
            lower.contains("glas") || lower.contains("audio") || lower.contains("zvuk") || 
            lower.contains("izgovori") || lower.contains("sound") -> "ElevenLabs Voice (Neural Audio Synth)"
            
            // Grok 2 for xAI news, sarcasm, uncensored real time questions
            lower.contains("grok") || lower.contains("politika") || lower.contains("trenutno") || 
            lower.contains("smisao") -> "Grok 2 (xAI Realtime)"
            
            // Phind for direct developer searching
            lower.contains("stack") || lower.contains("overflow") || lower.contains("greska") || 
            lower.contains("bug") || lower.contains("error") -> "Phind (Developer Search)"
            
            // Llama 3.1 405B for giant knowledge base questions
            lower.contains("llama") || lower.contains("istor") || lower.contains("geograf") || 
            lower.contains("fizik") || lower.contains("hemija") || lower.contains("skola") -> "Llama 3.1 405B (Meta Open-Weights)"
            
            // Gemini 1.5 Flash for ultra fast translation
            lower.contains("preved") || lower.contains("engles") || lower.contains("srps") || 
            lower.contains("recnik") -> "Gemini 1.5 Flash (Ultra-Fast)"
            
            else -> "Jov1ch Core (Gemini 1.5 Pro)"
        }
    }

    fun sendChatMessage() {
        val query = chatInput.value.trim()
        if (query.isEmpty()) return

        chatInput.value = ""
        viewModelScope.launch {
            repository.saveChatMessage("user", query)
            isTyping.value = true

            val allModelsList = listOf(
                "Jov1ch Core (Gemini 1.5 Pro)",
                "DeepSeek R1 (Reasoning)",
                "ChatGPT-4o (OpenAI)",
                "Claude 3.5 Sonnet (Anthropic)",
                "Lovable AI (Frontend Designer)",
                "Sora Video Engine (OpenAI)",
                "WormGPT-Edu (Ethical Security)",
                "Grok 2 (xAI Realtime)",
                "Perplexity Pro (Web Search)",
                "Devin AI (Autonomous Developer)",
                "Llama 3.1 405B (Meta)",
                "Midjourney v6 (Illustration)",
                "ElevenLabs Voice (Audio Synth)",
                "Phind (Developer Search)",
                "Gemini 1.5 Flash (Ultra-Fast)"
            )

            // Dynamic evaluation cascade visualization (cycles through in ~1.1s)
            val duration = 1100L
            val step = 75L
            val loops = duration / step
            for (i in 0 until loops) {
                evaluatingModelName.value = allModelsList[Random.nextInt(allModelsList.size)]
                kotlinx.coroutines.delay(step)
            }

            // Lock final chosen/routed model
            val chosenModel = autoSelectModel(query)
            evaluatingModelName.value = chosenModel
            selectedModel.value = chosenModel
            kotlinx.coroutines.delay(200)

            val jovichAnswer = repository.askJov1ch(query, chosenModel, chatMessages.value)
            repository.saveChatMessage("jovich", jovichAnswer)
            isTyping.value = false
            evaluatingModelName.value = null
        }
    }

    fun clearChats() {
        viewModelScope.launch {
            repository.clearChats()
            repository.saveChatMessage("jovich", "Istorijski zapisi ćaskanja su obrisani. Memorijski moduli virtuelnog modela su resetovani.")
        }
    }

    // Terminal command execution
    fun executeTerminal() {
        val cmd = terminalInput.value.trim()
        if (cmd.isEmpty()) return

        terminalInput.value = ""
        viewModelScope.launch {
            if (cmd.lowercase() == "clear") {
                repository.clearTerminal()
                return@launch
            }
            repository.executeCommand(cmd)
        }
    }

    // File selection
    fun refreshFiles() {
        try {
            val folder = File(currentPath.value)
            val list = folder.listFiles()?.toList() ?: emptyList()
            // Filter out build folders to keep exploration performant and clean
            filesList.value = list.filter { !it.name.startsWith(".") && it.name != "build" && it.name != "node_modules" }
        } catch (e: Exception) {
            filesList.value = emptyList()
        }
    }

    fun navigateTo(folder: File) {
        if (folder.isDirectory) {
            currentPath.value = folder.path
            selectedFile.value = null
            selectedFileContent.value = null
            refreshFiles()
        }
    }

    fun navigateUp() {
        val current = File(currentPath.value)
        val parent = current.parentFile
        if (parent != null) {
            currentPath.value = parent.path
            selectedFile.value = null
            selectedFileContent.value = null
            refreshFiles()
        }
    }

    fun selectFile(file: File) {
        if (file.isFile) {
            selectedFile.value = file
            viewModelScope.launch {
                try {
                    selectedFileContent.value = file.readText().take(5000)
                } catch (e: Exception) {
                    selectedFileContent.value = "ERROR: Failed to read file content. Binary format or denied access."
                }
            }
        } else {
            navigateTo(file)
        }
    }

    // Cyber Search
    fun triggerSearch() {
        val query = searchQuery.value.trim()
        if (query.isEmpty()) return

        isSearching.value = true
        viewModelScope.launch {
            val result = repository.executeCommand("search $query")
            searchResults.value = result.response
            isSearching.value = false
        }
    }

    // Generative mathematical synthesis
    fun triggerSynthesis() {
        val prompt = imagePrompt.value.trim()
        if (prompt.isEmpty()) return

        isSynthesizing.value = true
        synthMessages.value = listOf("BOOTING GEN_SYNTH MODULE...", "ALLOCATING VECTORS...", "MAPPING PROMPT SCHEMAS...")
        
        viewModelScope.launch {
            kotlinx.coroutines.delay(600)
            synthMessages.value = synthMessages.value + "RESOLVING PROCEDURAL CODES..."
            kotlinx.coroutines.delay(500)
            synthMessages.value = synthMessages.value + "RENDERING HIGH-FIDELITY LAYERS..."
            kotlinx.coroutines.delay(400)
            
            val seed = Random.nextLong()
            currentSeed.value = seed
            repository.saveImageLog(prompt, "rendered_seed_$seed")
            
            synthMessages.value = synthMessages.value + "INTEGRITY CHECK COMPLETED."
            isSynthesizing.value = false
        }
    }
}

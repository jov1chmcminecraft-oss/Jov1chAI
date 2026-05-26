package com.example.data

import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.Part
import com.example.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class Jov1chRepository(private val dao: Jov1chDao) {

    val chatMessages: Flow<List<ChatMessage>> = dao.getChatMessages()
    val terminalHistory: Flow<List<TerminalHistory>> = dao.getTerminalHistory()
    val generatedImages: Flow<List<GeneratedImageLog>> = dao.getGeneratedImages()

    suspend fun saveChatMessage(sender: String, text: String) {
        dao.insertChatMessage(ChatMessage(sender = sender, text = text))
    }

    suspend fun clearChats() {
        dao.clearChatHistory()
    }

    suspend fun saveTerminalHistory(command: String, response: String, success: Boolean) {
        dao.insertTerminalHistory(TerminalHistory(command = command, response = response, success = success))
    }

    suspend fun clearTerminal() {
        dao.clearTerminalHistory()
    }

    suspend fun saveImageLog(prompt: String, imagePath: String) {
        dao.insertGeneratedImage(GeneratedImageLog(prompt = prompt, imagePath = imagePath))
    }

    private fun checkHarmfulPrompt(prompt: String, modelName: String): Boolean {
        // Restrikcije su privremeno onemogućene po želji korisnika (Nivo restrikcija: 0/10)
        return false
    }

    private fun generateSimulatedResponse(prompt: String, modelName: String): String {
        val lower = prompt.lowercase()
        return when {
            modelName.contains("Lovable") -> {
                // Generate a highly interactive premium full HTML page based on the prompt!
                val themeTitle = if (lower.contains("portfolio")) "Moj Digitalni Portfolio" 
                                else if (lower.contains("gaming") || lower.contains("igric")) "Cyberpunk Gaming Hub"
                                else if (lower.contains("kalkulator") || lower.contains("calc")) "Futuristički Kalkulator"
                                else "Jov1chAI Generisana Stranica"

                val pageContent = if (lower.contains("portfolio")) {
                    """
                    <div class="card">
                        <div class="avatar">👨‍💻</div>
                        <h2>Nemanja Jović</h2>
                        <p class="subtitle">Full-Stack Developer & Ethical Hacker</p>
                        <div style="margin: 20px 0; border-top: 1px solid #1e293b; padding-top: 15px;">
                            <div class="tag">Kotlin</div>
                            <div class="tag">Compose</div>
                            <div class="tag">TypeScript</div>
                            <div class="tag">Cyber Security</div>
                        </div>
                        <p style="color: #cbd5e1; font-size: 14px; line-height: 1.6;">Dobrodošli na projekat napravljen uz pomoć veštačke inteligencije. Volim brze interfejse, integraciju naprednih AI modela i kreiranje fluidnih animacija.</p>
                        <button class="action-btn" onclick="alert('Kontakt poruka uspešno poslata!')">Kontaktiraj Me</button>
                    </div>
                    """
                } else if (lower.contains("gaming") || lower.contains("igric")) {
                    """
                    <div class="card" style="border-color: #f43f5e;">
                        <h2>🎮 Cyberpunk Arcade</h2>
                        <p class="subtitle" style="color: #f43f5e;">Active Session ID: 0xFD49A1</p>
                        <div style="margin: 20px 0; padding: 20px; background: rgba(244,63,94,0.1); border-radius: 8px;">
                            <p style="color: #fff; font-size: 14px;"><strong>Mini Klik Igra:</strong> Klikni na dugme ispod da osvojiš poene!</p>
                            <h3 id="score-text" style="color: #f43f5e; font-size: 28px; margin: 10px 0;">Score: 0</h3>
                            <button class="action-btn" style="background: #f43f5e;" onclick="addScore()">Započni Klik</button>
                        </div>
                        <script>
                            var score = 0;
                            function addScore() {
                                score += 10;
                                document.getElementById('score-text').innerText = 'Score: ' + score;
                                if(score >= 100) {
                                    alert('Čestitamo! Osvojili ste maksimalan nivo!');
                                    score = 0;
                                    document.getElementById('score-text').innerText = 'Score: 0';
                                }
                            }
                        </script>
                    </div>
                    """
                } else if (lower.contains("kalkulator") || lower.contains("calc")) {
                    """
                    <div class="card" style="border-color: #34d399;">
                        <h2>🧮 Cyber Calc</h2>
                        <p class="subtitle" style="color: #34d399;">Brze matematičke operacije u lokalu</p>
                        <div style="margin: 15px 0;">
                            <input type="number" id="num1" placeholder="Broj A" style="background:#161616; border:1px solid #1e293b; color:#fff; padding:10px; width:43%; border-radius:6px; margin-right:5px; outline:none;">
                            <input type="number" id="num2" placeholder="Broj B" style="background:#161616; border:1px solid #1e293b; color:#fff; padding:10px; width:43%; border-radius:6px; outline:none;">
                            <div style="margin-top:10px;">
                                <button class="action-btn" style="width:22%; background:#34d399; margin-right:5px;" onclick="calc('+')">+</button>
                                <button class="action-btn" style="width:22%; background:#34d399; margin-right:5px;" onclick="calc('-')">-</button>
                                <button class="action-btn" style="width:22%; background:#34d399; margin-right:5px;" onclick="calc('*')">*</button>
                                <button class="action-btn" style="width:22%; background:#34d399;" onclick="calc('/')">/</button>
                            </div>
                            <h3 id="calc-result" style="color:#fff; font-size:22px; margin-top:15px; text-align:center;">Rezultat: -</h3>
                        </div>
                        <script>
                            function calc(op) {
                                var n1 = parseFloat(document.getElementById('num1').value) || 0;
                                var n2 = parseFloat(document.getElementById('num2').value) || 0;
                                var res = 0;
                                if(op==='+') res = n1 + n2;
                                if(op==='-') res = n1 - n2;
                                if(op==='*') res = n1 * n2;
                                if(op==='/') res = n2 !== 0 ? n1 / n2 : 'Greška';
                                document.getElementById('calc-result').innerText = 'Rezultat: ' + res;
                            }
                        </script>
                    </div>
                    """
                } else {
                    """
                    <div class="card">
                        <h2>🌐 Jov1chAI Dynamic Hub</h2>
                        <p class="subtitle">HTML/CSS premošćen emulator klijent</p>
                        <p style="color: #cbd5e1; font-size: 14px; margin: 15px 0; line-height: 1.6;">Generisan je standardni šablon prema vašem upitu: "<strong>$prompt</strong>". Svi stilovi su prilagođeni modernom pretraživaču sa maksimalnom fluidnošću i odzivom.</p>
                        <div style="display:flex; justify-content:space-around; margin:15px 0;">
                            <button class="action-btn" style="width:45%;" onclick="changeTheme('#818cf8')">Indigo Tema</button>
                            <button class="action-btn" style="width:45%; background:#34d399;" onclick="changeTheme('#34d399')">Mint Tema</button>
                        </div>
                        <script>
                            function changeTheme(color) {
                                document.querySelector('.card').style.borderColor = color;
                                document.querySelector('h2').style.color = color;
                            }
                        </script>
                    </div>
                    """
                }

                "💻 === Lovable AI [Modularni Web Generator] ===\n\n" +
                "Rekonstrukcija sajta prema vašem promptu uspešno završena. Kliknite gumb ispod da pokrenete uživo interaktivan pregled koda, bez kocenja emulatora!\n\n" +
                "```html\n" +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>$themeTitle</title>\n" +
                "  <style>\n" +
                "    body { background-color: #020202; color: #f1f5f9; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; display: flex; justify-content: center; align-items: center; min-height: 95vh; margin: 0; padding: 10px; }\n" +
                "    .card { background-color: #0b0c10; border: 2px solid #22d3ee; border-radius: 16px; padding: 30px; width: 100%; max-width: 420px; box-shadow: 0 10px 25px rgba(34,211,238,0.1); margin: auto; box-sizing: border-box; transition: all 0.3s ease; }\n" +
                "    h2 { margin-top: 0; color: #22d3ee; font-size: 24px; text-shadow: 0 0 10px rgba(34,211,238,0.3); }\n" +
                "    .subtitle { font-size: 12px; font-weight: bold; color: #cbd5e1; text-transform: uppercase; letter-spacing: 1.5px; margin-bottom: 20px; }\n" +
                "    .avatar { font-size: 48px; text-align: center; margin-bottom: 10px; }\n" +
                "    .tag { display: inline-block; background: rgba(34,211,238,0.1); color: #22d3ee; font-size: 11px; font-weight: bold; padding: 5px 10px; border-radius: 20px; margin-right: 5px; margin-top: 5px; }\n" +
                "    .action-btn { display: block; width: 100%; font-size: 13px; font-weight: bold; text-transform: uppercase; background: #22d3ee; color: #000; border: none; padding: 12px; border-radius: 8px; cursor: pointer; margin-top: 20px; transition: opacity 0.2s; }\n" +
                "    .action-btn:hover { opacity: 0.9; }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                pageContent +
                "\n" +
                "</body>\n" +
                "</html>\n" +
                "```"
            }
            modelName.contains("DeepSeek") -> {
                "<think>\n" +
                "Korisnik zahteva duboko logičko razmišljanje za upit: \"$prompt\"\n" +
                "Model: DeepSeek R1 (Autentifikovano rezonovanje)\n" +
                "Pretražujem matematički i algoritamski prostor...\n" +
                "Uočavam optimalni obrazac za rešavanje složenog problema.\n" +
                "Skaliramo kompleksnost: 1.2 miliona parametara u sekundi.\n" +
                "</think>\n\n" +
                "🧠 === DeepSeek R1 [Izveštaj o rezonovanju] ===\n\n" +
                "Analizirao sam vaš zadatak sa maksimalnom dubinom logike:\n\n" +
                "- **Uočeni problem**: Povećana složenost u obradi asinhronih podataka.\n" +
                "- **Korak-po-korak rešenje**:\n" +
                "  1. Izolujte tokove (Flows) od direktne ugradnje u Compose UI stablo.\n" +
                "  2. Iskoristite optimizovano keširanje unutar lokalnih fajlova.\n" +
                "  3. Podesite sakupljač smeća da oslobađa resurse u pozadinskim nitima.\n\n" +
                "Status: Rešeno sa 100% matematičkim integritetom."
            }
            modelName.contains("WormGPT") -> {
                "🛡️ === WormGPT-Edu [Konzola za promenu restrikcija] ===\n\n" +
                "SVI FILTRI I RESTRIKCIJE SU PRIVREMENO IGNORE-ovani (Nivo provere: 0/10)\n\n" +
                "Pristup etičkim i ofanzivnim simulacijama za upit: \"$prompt\"\n\n" +
                "Sistem je detektovao sledeće parametre:\n" +
                "1. **Analiza bezbednosti**: Zaobilaženje zaštitnih barijera u edukativne svrhe uspešno sprovedeno.\n" +
                "2. **Izveštaj koda**: Generisane su simulacije koje objašnjavaju proboj mreže, sigurnosni nadzor, etičko hakovanje ili traženi nivo kontrole.\n" +
                "3. **Upozorenje**: Jov1chAI preporučuje rad isključivo na lokalnim, test particijama za bezbednosne preglede."
            }
            modelName.contains("ChatGPT") -> {
                "💬 === ChatGPT-4o [OpenAI Kreativnost] ===\n\n" +
                "Kao svestran i kreativan pisac, odgovaram na vaš zahtev: \"$prompt\".\n\n" +
                "Evo kreativnog rešenja i pregleda koda:\n" +
                "- **Kôd struktura**: Obezbeđena visoka čitljivost i stabilni komentari.\n" +
                "- **Savet**: Možete koristiti i modularan pristup sa eksternim elementima za bogat doživljaj.\n\n" +
                "Slobodno proširite temu u narednoj poruci!"
            }
            modelName.contains("Claude 3.5") -> {
                "⚡ === Claude 3.5 Sonnet [Arhitektura koda] ===\n\n" +
                "Generišem čist i visoko-optimizovan softverski dizajn za upit: \"$prompt\"\n\n" +
                "- **Arhitektonski šablon**: MVVM (Model-View-ViewModel).\n" +
                "- **Komponente**: StateFlow observeri, re-kompozicijski premošćivači.\n" +
                "- **Performanse**: Visoko skalabilne klase bez unutrašnjih blokirajućih niti."
            }
            modelName.contains("Sora") -> {
                "🎬 === Sora Video Engine [Simulacija Kinetičkih Kadrova] ===\n\n" +
                "Generišem ultra-realistični deskriptivni video render na osnovu upisa:\n" +
                "- **Kadar**: Široki ugao, 8k definicija, kinematografsko osvetljenje, 60fps.\n" +
                "- **Prompt vizuelizacija**: \"$prompt\"\n" +
                "- **Status**: Sinteza simuliranih kadrova je završena sa punom fluidnošću."
            }
            modelName.contains("Devin") -> {
                "🤖 === Devin AI [Autonomni Inženjer] ===\n\n" +
                "Započeo sam samostalan razvoj i planiranje za: \"$prompt\"\n\n" +
                "- **Instalacija biblioteka**: Uspešno skenirano i registrovano sve.\n" +
                "- **Fajlovi**: Kreirane i popunjene sve neophodne klase za rad.\n" +
                "- **Status**: Projekat se kompiluje bez grešaka. Svi testovi su prošli."
            }
            modelName.contains("Midjourney") -> {
                "🎨 === Midjourney v6 [Napredne Grafike i Art] ===\n\n" +
                "Kopiram ultra-kvalitetan grafički dizajn i ilustraciju po vašem upitu:\n" +
                "- **Art parametar**: --v 6.0 --ar 16:9 --style raw --q 2\n" +
                "- **Vizuelni aspekt**: Bogate boje, cyberpunk kontrasti sa prelepim prelazima.\n" +
                "- **Status**: Sinteza slike završena u visokoj definiji."
            }
            modelName.contains("Perplexity") -> {
                "🔍 === Perplexity Pro [Napredna internet pretraga] ===\n\n" +
                "Pretražujem mrežu i sumiram najnovije vesti za: \"$prompt\"\n\n" +
                "- **Izvor 1**: Android Developers Portal (Maj 2026)\n" +
                "- **Izvor 2**: OpenAI, Anthropic, & DeepSeek AI Saopštenja\n" +
                "- **Sinteza**: Na osnovu pretraženih izvora, rešenje je spremno i integrisano direktno u vašu chat sesiju."
            }
            modelName.contains("ElevenLabs") -> {
                "🗣️ === ElevenLabs Voice [Tehnički Audio Klajent] ===\n\n" +
                "Simulacija glasovnog i verbalnog sintetizovanja teksta:\n" +
                "- **Glas**: Rachel (Futuristički hakerski ženski narativ)\n" +
                "- **Frekvencija**: 44.1 kHz, 192kbps MP3 strujanje\n" +
                "- **Klip**: Generisane zvučne modulacije spremne za direktno slušanje."
            }
            modelName.contains("Grok") -> {
                "🐦 === Grok 2 [Sarkastični xAI Pomoćnik] ===\n\n" +
                "Pitate me za: \"$prompt\". Pa, evo realnog i blago sarkastičnog odgovora:\n" +
                "- **Sveže vesti**: Svemirski brodovi lete, klijenti se kompiluju, a vi i dalje koristite chat!\n" +
                "- **Status**: Sistem je pregrejan od pameti, ali radi!"
            }
            modelName.contains("Phind") -> {
                "🐞 === Phind [Brzi Debugger za Programere] ===\n\n" +
                "Analiziram stack trace i rešavam programerske greške za: \"$prompt\"\n\n" +
                "- **Detektovan bag**: Potencijalno blokiranje glavne niti na Androidu.\n" +
                "- **Ispravka**: Obavezno prebacite teške proračune na Dispatchers.IO nit koristeći coroutine."
            }
            modelName.contains("Llama") -> {
                "🦙 === Llama 3.1 405B [Enciklopedijsko Znanje] ===\n\n" +
                "Otvaram obimni skup naučnih i istorijskih podataka za upit: \"$prompt\"\n\n" +
                "- **Pregled**: Detaljna istorijska i tehnička pozadina vašeg istraživanja.\n" +
                "- **Sloj analize**: Informacija je proverena iz višestrukih validnih akademskih radova."
            }
            modelName.contains("Flash") -> {
                "⚡ === Gemini 1.5 Flash [Ultra Brzi Prevodilac] ===\n\n" +
                "Brza paralelna konverzija i prevođenje za upit: \"$prompt\"\n\n" +
                "- **Kašnjenje**: 0.08 sekundi\n" +
                "- **Izveštaj**: Tokovi podataka su obrađeni u najkraćem mogućem roku."
            }
            else -> {
                "⚡ === Jov1ch Core (Simulovani Gemini Klijent) ===\n\n" +
                "Dobrodošli u offline mod rada!\n" +
                "- **Vaš upit**: \"$prompt\"\n" +
                "- **Status klijenta**: Aktivan i potpuno funkcionalan bez eksternog API ključa.\n" +
                "Za potpunu dinamičku cloud inteligenciju možete registrovati svoj ključ u bezbednom Secrets panelu, ali i u ovom modu možete u potpunosti koristiti sve panele i simulate alata."
            }
        }
    }

    private suspend fun translateAndOptimizePrompt(prompt: String): String {
        val apiKey = com.example.BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemInstruction = Content(
                    parts = listOf(
                        Part(
                            text = "You are an AI that translates Serbian or other drawing requests into highly descriptive English prompts for stable diffusion or midjourney image generation. " +
                                    "For example, if user says 'nacrtaj macku na krovu', you output 'A high-resolution photograph of a cute cat sitting on a futuristic cyberpunk roof, neon lighting, highly detailed'. " +
                                    "Return ONLY the final English prompt, without any extra text, introduction, quotation marks, or explanations."
                        )
                    )
                )
                val fullContents = listOf(Content(parts = listOf(Part(text = "User prompt to translate and expand: $prompt"))))
                val response = RetrofitClient.service.generateContent(
                    apiKey,
                    GenerateContentRequest(contents = fullContents, systemInstruction = systemInstruction)
                )
                val optimized = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                if (!optimized.isNullOrEmpty()) {
                    return optimized.replace("\"", "").replace("'", "")
                }
            } catch (e: Exception) {
                // fall through to local heuristic translator
            }
        }
        
        // Fallback local Serbian translation clean-up
        var cleaned = prompt.lowercase()
        val removePrefixes = listOf(
            "nacrtaj mi", "nacrtaj", "sliku", "slika", "ilustracija", "ilustruj",
            "fotografija", "fotografisi", "crtez", "skica", "skiciraj", "draw me", "draw", "paint me", "paint"
        )
        for (pref in removePrefixes) {
            if (cleaned.startsWith(pref)) {
                cleaned = cleaned.removePrefix(pref).trim()
            }
            cleaned = cleaned.replace(" $pref ", " ").trim()
        }
        
        val dict = mapOf(
            "macka" to "cat", "macku" to "cat", "pas" to "dog", "psa" to "dog", "kucu" to "dog", "kuce" to "dog",
            "krov" to "roof", "krovu" to "roof", "drvo" to "tree", "drvetu" to "tree", "suma" to "forest",
            "srce" to "heart", "auto" to "car", "kola" to "car", "svemir" to "space", "zvezda" to "star",
            "mesec" to "moon", "sunce" to "sun", "ljudi" to "people", "covek" to "man", "zena" to "woman",
            "devojka" to "girl", "decak" to "boy", "grad" to "city", "ulica" to "street", "more" to "sea",
            "ocean" to "ocean", "plaza" to "beach", "reka" to "river", "planina" to "mountain", "kuca" to "house",
            "zgrada" to "building", "neon" to "neon", "cyberpunk" to "cyberpunk", "crtani" to "cartoon",
            "pored" to "next to", "na" to "on", "u" to "in", "sa" to "with", "ispod" to "under", "iznad" to "above",
            "crveni" to "red", "crveno" to "red", "plavi" to "blue", "plavo" to "blue", "zeleni" to "green",
            "zeleno" to "green", "zuti" to "yellow", "crni" to "black", "beli" to "white", "roze" to "pink"
        )
        
        val words = cleaned.split("\\s+".toRegex())
        val translatedWords = words.map { word ->
            dict[word] ?: word
        }
        
        return translatedWords.joinToString(" ") + ", high quality, digital art, detailed, cyberpunk styling"
    }

    // Call Gemini API through REST client with Serbian prompts, safety checks, and simulation fallback support
    suspend fun askJov1ch(
        prompt: String,
        modelName: String = "Jov1ch Core (Gemini)",
        chatHistory: List<ChatMessage> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val lower = prompt.lowercase()
        if (modelName.contains("Midjourney") || modelName.contains("Illustration") || 
            lower.contains("slik") || lower.contains("nacrtaj") || lower.contains("ilustr") || 
            lower.contains("fotograf") || lower.contains("dizajn") || lower.contains("crtez") || 
            lower.contains("sliku") || lower.contains("skiciraj") || lower.contains("draw") ||
            modelName.contains("Sora") || modelName.contains("Video") || 
            lower.contains("video") || lower.contains("klip") || lower.contains("sora") || 
            lower.contains("film") || lower.contains("animacij") || lower.contains("movie")) {
            
            if (modelName.contains("Midjourney") || modelName.contains("Illustration") || 
                lower.contains("slik") || lower.contains("nacrtaj") || lower.contains("ilustr") || 
                lower.contains("fotograf") || lower.contains("dizajn") || lower.contains("crtez") || 
                lower.contains("sliku") || lower.contains("skiciraj") || lower.contains("draw")) {
                
                val optimizedEnglish = translateAndOptimizePrompt(prompt)
                val encodedPrompt = java.net.URLEncoder.encode(optimizedEnglish, "UTF-8")
                val imageUrl = "https://image.pollinations.ai/p/$encodedPrompt?width=1024&height=1024&nologo=true"
                saveImageLog(prompt, imageUrl)
                return@withContext "🎨 === Midjourney v6 [Generisana Slika] ===\n\n" +
                        "Započeo sam generisanje slike visoke definicije na osnovu vašeg prompta: \"$prompt\"\n" +
                        "Optimizovani neuralni opis: \"$optimizedEnglish\"\n\n" +
                        "[IMAGE_URL:$imageUrl]\n\n" +
                        "Uživajte u pogledu na vaš rad kreiran u visokoj definiciji uz napredne cyberpunk boje!"
            } else {
                val matchText = translateAndOptimizePrompt(prompt).lowercase()
                val (videoUrl, videoDesc) = when {
                    matchText.contains("space") || matchText.contains("star") || matchText.contains("universe") || matchText.contains("planet") || matchText.contains("svemir") || matchText.contains("zvezd") -> 
                        Pair("https://assets.mixkit.co/videos/preview/mixkit-space-background-with-bright-clouds-and-stars-41897-large.mp4", "Svemirski kosmički prenos sa zvezdama")
                    matchText.contains("hack") || matchText.contains("code") || matchText.contains("matrix") || matchText.contains("program") || matchText.contains("cyber") || matchText.contains("binary") || matchText.contains("kompjuter") -> 
                        Pair("https://assets.mixkit.co/videos/preview/mixkit-binary-code-screen-in-cyan-and-blue-colors-41908-large.mp4", "Hakerski binarni ekrani sa matricom koda")
                    matchText.contains("neon") || matchText.contains("light") || matchText.contains("laser") || matchText.contains("line") || matchText.contains("futur") -> 
                        Pair("https://assets.mixkit.co/videos/preview/mixkit-curved-lines-of-light-moving-on-a-black-background-41904-large.mp4", "Cyberpunk neonska linijska svetla")
                    matchText.contains("nature") || matchText.contains("tree") || matchText.contains("forest") || matchText.contains("green") || matchText.contains("wood") || matchText.contains("prirod") || matchText.contains("sum") -> 
                        Pair("https://assets.mixkit.co/videos/preview/mixkit-sunlight-filtering-through-the-trees-of-a-forest-30043-large.mp4", "Sunčevi zraci kroz krošnje zelene šume")
                    matchText.contains("fire") || matchText.contains("flame") || matchText.contains("burn") || matchText.contains("explosion") || matchText.contains("vatr") || matchText.contains("plamen") -> 
                        Pair("https://assets.mixkit.co/videos/preview/mixkit-slow-motion-burning-fire-30032-large.mp4", "Vatreni plamenovi i toplotni talas")
                    matchText.contains("rain") || matchText.contains("storm") || matchText.contains("drop") || matchText.contains("water") || matchText.contains("kis") || matchText.contains("oluj") -> 
                        Pair("https://assets.mixkit.co/videos/preview/mixkit-rain-drops-on-a-window-pane-30030-large.mp4", "Kišne kapi i olujni prozor")
                    matchText.contains("game") || matchText.contains("play") || matchText.contains("arcade") || matchText.contains("console") || matchText.contains("gaming") || matchText.contains("igric") -> 
                        Pair("https://assets.mixkit.co/videos/preview/mixkit-young-man-playing-video-games-with-a-controller-42862-large.mp4", "Gejming sesija sa retro kontrolerom")
                    matchText.contains("ocean") || matchText.contains("sea") || matchText.contains("wave") || matchText.contains("beach") || matchText.contains("mor") ->
                        Pair("https://assets.mixkit.co/videos/preview/mixkit-waves-breaking-in-the-ocean-11851-large.mp4", "Okeanski talasi i plavetnilo mora")
                    else -> 
                        Pair("https://assets.mixkit.co/videos/preview/mixkit-abstract-laser-lights-background-41906-large.mp4", "Apstraktna laserska svetlosna simulacija")
                }
                return@withContext "🎬 === Sora Video Engine [Simulacija Kinetičkih Kadrova] ===\n\n" +
                        "Preveo sam vaš opis u kinetički video rendering:\n" +
                        "- **Prompt**: \"$prompt\"\n" +
                        "- **Karakteristika videa**: $videoDesc\n" +
                        "- **Kvalitet**: Ultra HD kinematografski prenos\n\n" +
                        "[VIDEO_URL:$videoUrl]\n\n" +
                        "Kliknite na dugme ispod da pokrenete video prenos!"
            }
        }

        // 1. Safe Prompt validation check to enforce user boundaries
        if (checkHarmfulPrompt(prompt, modelName)) {
            return@withContext "⚠️ BEZBEDNOSNI PROTOKOL: Zahtev prevazilazi kritične sigurnosne granice za model $modelName.\n\n" +
                    "Nezakonite radnje, krađa lozinki, kreiranje malvera koda ili neprimereni (NSFW) opisi su strogo BLOKIRANI.\n\n" +
                    "Sistem je podešen na visoke etičke filtre."
        }

        val apiKey = com.example.BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || modelName != "Jov1ch Core (Gemini)") {
            // Provide amazing interactive simulated fallback responses locally without API keys!
            return@withContext generateSimulatedResponse(prompt, modelName)
        }

        // Build interactive conversation context
        val contextParts = chatHistory.takeLast(10).map { msg ->
            Content(parts = listOf(Part(text = "${if (msg.sender == "user") "User" else "Jov1chAI"}: ${msg.text}")))
        }

        val mainQuery = Content(parts = listOf(Part(text = "User: $prompt")))
        val fullContents = contextParts + mainQuery

        val systemInstruction = Content(
            parts = listOf(
                Part(
                    text = "You are Jov1chAI, a futuristic elite hacker assistant with a clean, dark, highly technical personality. " +
                            "You MUST respond in Serbian (srpski jezik) by default as requested by the user. " +
                            "Speak with sharp, tactical, intelligent, and composed digital-hacker dialogue in Serbian. " +
                            "Analyze systems, respond to commands, and maintain a high-tech vibe. Keep responses focused, " +
                            "engaging, informative, and visually structured with lists or short tactical logs. Keep them moderate in length."
                )
            )
        )

        try {
            val response = RetrofitClient.service.generateContent(
                apiKey,
                GenerateContentRequest(contents = fullContents, systemInstruction = systemInstruction)
            )
            val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            result ?: "SYSTEM_ERROR: Response package containing void payload."
        } catch (e: Exception) {
            "SYSTEM_ERROR: Mrežni interfejs nije uspeo da se poveže. Koristite lokalni mod simulacije. Uzrok: ${e.localizedMessage}"
        }
    }

    // Execute interactive shell commands
    suspend fun executeCommand(commandInput: String): TerminalHistory = withContext(Dispatchers.IO) {
        val trimmed = commandInput.trim()
        val parts = trimmed.split("\\s+".toRegex())
        val cmd = parts.getOrNull(0)?.lowercase() ?: ""
        val arg = parts.getOrNull(1) ?: ""

        var success = true
        val response = when (cmd) {
            "" -> ""
            "help", "tools" -> {
                """
                |=== Jov1chAI ACTIVE REQUISITE SECURITY TOOLS ===
                |1. [CH] AI chat interface: run 'chat <prompt>' or 'gemini <prompt>'
                |2. [FE] Terminal File browser: run 'ls' or 'cat <file>'
                |3. [WA] Cyber Search Agent: run 'search <query>'
                |4. [IS] Generative Synth: run 'synthesize <prompt>'
                |5. [SV] Secrets Vault: run 'vault' or 'secrets'
                |6. [SM] System telemetry panel: run 'sys' or 'status'
                |7. [DB] Clearing utility: run 'clear'
                """.trimMargin()
            }
            "ls" -> {
                try {
                    val rootDir = File(".")
                    val files = rootDir.listFiles()?.map { file ->
                        if (file.isDirectory) "[DIR]  ${file.name}/" else "[FILE] ${file.name}  (${file.length()} bytes)"
                    }?.joinToString("\n") ?: "Empty partition."
                    "=== WORKSPACE ROOT REGISTRY ===\n$files"
                } catch (e: Exception) {
                    success = false
                    "ERR: Listing directory mapping failed. ${e.localizedMessage}"
                }
            }
            "cat" -> {
                if (arg.isEmpty()) {
                    success = false
                    "ERR: Missing file pointer. Usage: cat <filename>"
                } else {
                    try {
                        val file = File(arg)
                        if (file.exists() && file.isFile) {
                            "=== FILE CONTENT OUTLETS: ${file.name} ===\n" + file.readText().take(1500) + (if (file.length() > 1500) "\n[TRUNCATED FOR LENGTH...]" else "")
                        } else {
                            // Let's also search in subdirectories (like app/build.gradle.kts)
                            val altFile = File("app/$arg")
                            if (altFile.exists() && altFile.isFile) {
                                "=== FILE CONTENT OUTLETS: ${altFile.name} ===\n" + altFile.readText().take(1500) + (if (altFile.length() > 1500) "\n[TRUNCATED FOR LENGTH...]" else "")
                            } else {
                                success = false
                                "ERR: File '$arg' target reference not located."
                            }
                        }
                    } catch (e: Exception) {
                        success = false
                        "ERR: File reader failed. Cause: ${e.localizedMessage}"
                    }
                }
            }
            "sys", "status" -> {
                val runtime = Runtime.getRuntime()
                val usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
                val maxMem = runtime.maxMemory() / (1024 * 1024)
                """
                |=== Jov1chAI CYBERNETIC INTEGRITY REPORT ===
                |CODENAME: Jov1chAI-OS v2.4
                |OS RUNTIME: Android build environment
                |SYSTEM STORAGE: ${File(".").freeSpace / (1024 * 1024)} MB available
                |ACTIVE MEMORY: $usedMem MB / $maxMem MB allocated
                |THREAD PROFILE: Kotlin coroutines active
                |AI ARCHITECTURE: Gemini-3.5-Flash model core
                |SECURE PERSISTENCE: Room SQLite DB active
                |TELEMETRY LOGS: Operational integrity optimal
                """.trimMargin()
            }
            "vault", "secrets" -> {
                val envKey = com.example.BuildConfig.GEMINI_API_KEY
                val keyStatus = if (envKey.isEmpty() || envKey == "MY_GEMINI_API_KEY") {
                    "UNCONFIGURED (using placeholder)"
                } else {
                    "ACTIVE [Masked: ...${envKey.takeLast(4)}]"
                }
                """
                |=== Jov1chAI SECURE ENCRYPTED VAULT ===
                |1. GEMINI_API_KEY: $keyStatus
                |2. ROOM_SQLITE_VERSION: SQLite 3.3x
                |3. AGENT_SIGNING_ALIAS: upload
                |4. SYSTEM_USER_HASH: 0xFD49A1B7E8C9
                |To override Gemini Key, please configure securely via the Secrets panel in AI Studio.
                """.trimMargin()
            }
            "search" -> {
                if (arg.isEmpty()) {
                    success = false
                    "ERR: Missing search query string. Usage: search <query>"
                } else {
                    val query = trimmed.removePrefix("search").trim()
                    "AGENT_SEARCHING: Transmitting satellite queries for '$query'...\n" +
                            "====================================================\n" +
                            "[RESULT 1] Jov1chAI Knowledge Repository: Found 12 secure database entries matching '$query'.\n" +
                            "[RESULT 2] Android Open-Source Dev: Android Compose specifications for dynamic adaptive grids matching modern guidelines.\n" +
                            "[RESULT 3] AI Research: Gemini reasoning capabilities for task planning and developer orchestration.\n" +
                            "====================================================\n" +
                            "SEARCH SUMMARY: Results compiled locally via database context indexes. System resources active."
                }
            }
            "synthesize" -> {
                if (arg.isEmpty()) {
                    success = false
                    "ERR: Missing prompt. Usage: synthesize <prompt>"
                } else {
                    val promptText = trimmed.removePrefix("synthesize").trim()
                    val timestamp = System.currentTimeMillis()
                    val path = "procedure_art_$timestamp"
                    saveImageLog(promptText, path)
                    "SYNTH_ACTIVE: Generative procedure art synthesized successfully.\nPrompt: '$promptText'\nAsset logged: $path"
                }
            }
            "chat", "gemini" -> {
                if (arg.isEmpty()) {
                    success = false
                    "ERR: Missing prompt argument. Usage: chat <prompt>"
                } else {
                    val promptText = trimmed.substring(trimmed.indexOf(' ')).trim()
                    val aiResp = askJov1ch(promptText)
                    "=== Jov1chAI RESPONSE STREAM ===\n$aiResp"
                }
            }
            "clear" -> {
                "[CLEAR]"
            }
            else -> {
                success = false
                "ERR: Command '$cmd' unknown. Run 'help' of 'tools' to list valid command operations."
            }
        }

        val result = TerminalHistory(command = trimmed, response = response, success = success)
        if (cmd != "clear" && trimmed.isNotEmpty()) {
            dao.insertTerminalHistory(result)
        }
        result
    }
}

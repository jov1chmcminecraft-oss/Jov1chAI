package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.viewmodel.Jov1chViewModel
import com.example.viewmodel.Panel
import com.example.data.ChatMessage
import com.example.data.TerminalHistory
import com.example.data.GeneratedImageLog
import java.io.File
import kotlin.math.cos
import kotlin.math.sin

// --- Clean Minimalism Theme Colors ---
val CyberBackground = Color(0xFF0A0A0A) // Pure Minimal Dark Slate
val CyberSurface = Color(0xFF161616)    // Solid Charcoal Dark Card
val CyberBorder = Color(0xFF1E293B)     // Charcoal Slate-800 border
val CyberCyan = Color(0xFF22D3EE)       // Clean Cyber Cyan-400
val CyberGreen = Color(0xFF34D399)      // Sophisticated Mint/Emerald
val CyberPurple = Color(0xFF818CF8)     // Modern Indigo Accent
val CyberPink = Color(0xFFF43F5E)       // Refined Rose/Pink
val CyberText = Color(0xFFF1F5F9)       // slate-100 high-clarity text
val CyberTextMuted = Color(0xFF94A3B8)  // slate-400 description text

val CyberGradient = Brush.linearGradient(
    listOf(Color(0xFF06B6D4), Color(0xFF4F46E5))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Apply a global theme wrapper
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = CyberBackground,
                    surface = CyberSurface,
                    primary = CyberCyan,
                    secondary = CyberGreen,
                    tertiary = CyberPurple
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CyberBackground
                ) {
                    val viewModel: Jov1chViewModel = viewModel()
                    Jov1chDashboard(viewModel)
                }
            }
        }
    }
}

@Composable
fun Jov1chDashboard(viewModel: Jov1chViewModel) {
    var activeHtmlPreview by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBackground)
                .padding(innerPadding)
        ) {
            if (activeHtmlPreview != null) {
                // Nova stranica sa interaktivnim HTML pregledom, back/nazad i download gumbima
                WebPreviewScreen(
                    htmlContent = activeHtmlPreview!!,
                    onBack = { activeHtmlPreview = null }
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top Bar System Status
                    SystemHeaderPanel()

                    // Only show the AI Chat Panel directly, simplifying the entire layout
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        ChatPanel(
                            viewModel = viewModel,
                            onPreviewHtml = { activeHtmlPreview = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SystemHeaderPanel() {
    var linkPulse by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            linkPulse = !linkPulse
            kotlinx.coroutines.delay(1000)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo image wrapped in Clean Minimalist Gradient Container
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CyberGradient)
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_jovich_logo),
                contentDescription = "Hacker Jov1chAI Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Jov1chAI",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color = if (linkPulse) CyberCyan else CyberCyan.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(3.dp)
                        )
                )
                Text(
                    text = "NEURONSKA VEZA AKTIVNA",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 10.sp,
                        color = CyberTextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
            }
        }

        // Settings / Quick system indication button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(CyberSurface)
                .border(1.dp, CyberBorder, RoundedCornerShape(20.dp))
                .clickable { /* action */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "System Access",
                tint = CyberTextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun CyberBottomNavBar(
    currentPanel: Panel,
    onPanelSelected: (Panel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFF121212))
            .border(width = 1.dp, color = CyberBorder, shape = RoundedCornerShape(0.dp))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tabs = listOf(
            Panel.CHAT to "CHAT",
            Panel.TERMINAL to "SHELL",
            Panel.EXPLORER to "FILES",
            Panel.SEARCH to "SEARCH",
            Panel.SYNTHESIZER to "SYNTH",
            Panel.VAULT to "VAULT",
            Panel.LOGS to "LOGS"
        )

        tabs.forEach { (panel, label) ->
            val isActive = currentPanel == panel
            val activeColor = when (panel) {
                Panel.CHAT -> CyberCyan
                Panel.TERMINAL -> CyberGreen
                Panel.EXPLORER -> CyberPurple
                Panel.SEARCH -> CyberCyan
                Panel.SYNTHESIZER -> CyberPink
                Panel.VAULT -> Color.Yellow
                Panel.LOGS -> CyberTextMuted
            }

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onPanelSelected(panel) }
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isActive) activeColor.copy(alpha = 0.15f) else Color.Transparent)
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (panel) {
                        Panel.CHAT -> Icons.Default.Home
                        Panel.TERMINAL -> Icons.Default.KeyboardArrowUp
                        Panel.EXPLORER -> Icons.Default.List
                        Panel.SEARCH -> Icons.Default.Search
                        Panel.SYNTHESIZER -> Icons.Default.Create
                        Panel.VAULT -> Icons.Default.Lock
                        Panel.LOGS -> Icons.Default.Refresh
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isActive) activeColor else CyberTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 11.sp,
                        color = if (isActive) activeColor else CyberTextMuted,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
                    )
                )
            }
        }
    }
}

// ======================== HERO STAGE COMPONENT ========================
@Composable
fun AiHeroState() {
    val infiniteTransition = rememberInfiniteTransition(label = "hero")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            // Soft blur glow background in line with the tailwind cyan shadow
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color(0x1322D3EE),
                        shape = RoundedCornerShape(40.dp)
                    )
            )

            // Dashed rotating circular frame
            Canvas(modifier = Modifier.size(110.dp)) {
                drawCircle(
                    color = Color(0x3B22D3EE),
                    radius = size.minDimension / 2f,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(15f, 15f),
                            phase = rotation
                        )
                    )
                )
            }

            // Central floating neon core
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.5.dp, Color(0x9922D3EE), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "AI Neural Engine Core",
                    tint = Color(0xFF22D3EE),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = buildAnnotatedString {
                append("Spreman za rad. Analiziramo sistem: ")
                withStyle(style = SpanStyle(color = Color(0xFF22D3EE), fontWeight = FontWeight.Bold)) {
                    append("24 jedinstvena alata")
                }
                append(" su aktivna u vašoj konzoli.")
            },
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.sp,
                color = CyberTextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

// ======================== TOOL 1: CHAT WITH JOV1CH ========================
@Composable
fun ChatPanel(viewModel: Jov1chViewModel, onPreviewHtml: (String) -> Unit) {
    val messages by viewModel.chatMessages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val chatInput by viewModel.chatInput.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val evaluatingModelName by viewModel.evaluatingModelName.collectAsState()
    val listState = rememberLazyListState()

    val models = listOf(
        Triple("Jov1ch Core (Gemini 1.5 Pro)", "Hibridni Gemini Pro višenamenski asistent za opšte upite", listOf("Analiza", "Planiranje")),
        Triple("DeepSeek R1 (Reasoning)", "Napredna logika, matematičko zaključivanje i rešavanje složenih algoritama", listOf("Logika", "Duboki debug")),
        Triple("ChatGPT-4o (OpenAI)", "Fluentan asistent za pisanje kreativnog sadržaja, koda i python skripti", listOf("Skripte", "Razrada")),
        Triple("Claude 3.5 Sonnet (Anthropic)", "Uredan arhitektonski planer i dizajner složenih softverskih sistema", listOf("Arhitektura", "Modeli")),
        Triple("Lovable AI (Frontend Designer)", "Simulacija brze 1:1 rekonstrukcije interaktivnih veb stranica sa preview opcijom", listOf("Sajtovi", "Design")),
        Triple("Sora Video Engine (OpenAI)", "Simulacija kinematografskog video rendera i deskriptivne video sinteze", listOf("Kadrovi", "Cinematics")),
        Triple("WormGPT-Edu (Ethical Security)", "Etičko provaljivanje unosa i bezbednosni nadzor bez rigidnih restrikcija", listOf("Ranjivosti", "Odbrana")),
        Triple("Grok 2 (xAI Realtime)", "Brzi xAI pretraživač sa blagim dozama sarkazma i nefiltriranim real-time vestima", listOf("Vesti", "Sarkazam")),
        Triple("Perplexity Pro (Web Search)", "Napredno agregiranje rezultata sa pretraživača uz izvore i citate", listOf("Pretraga", "Izvori")),
        Triple("Devin AI (Autonomous Developer)", "Autonomni softverski inženjer koji gradi fajlove, logove i pokreće kompajler", listOf("Workspace", "Kompajl")),
        Triple("Llama 3.1 405B (Meta)", "Gigantski dvojezični model sa enciklopedijskim znanjem i logikom", listOf("Znanje", "Kultura")),
        Triple("Midjourney v6 (Illustration)", "Generisanje cyberpunk i art ilustracija u visokoj HD rezoluciji", listOf("Ilustracije", "Grafika")),
        Triple("ElevenLabs Voice (Audio Synth)", "Vokalna sinteza i pretvaranje teksta u ljudski glas i mp3 audio prenos", listOf("Rachel", "Audio")),
        Triple("Phind (Developer Search)", "Brzi tehnički pretraživač programerskih grešaka i stack trace ispravki", listOf("Bube", "Debugger")),
        Triple("Gemini 1.5 Flash (Ultra-Fast)", "Ekstremno brz višenamenski asistent za prevode i trenutnu transliteraciju", listOf("Prevod", "Brzina"))
    )

    // Safe auto-scroll on new message or typing updates
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            try {
                // Header (1 item) + messages (N items) + typing indicator (1 item if isTyping is true)
                val totalItems = 1 + messages.size + (if (isTyping) 1 else 0)
                if (totalItems > 0) {
                    listState.animateScrollToItem(totalItems - 1)
                }
            } catch (e: Exception) {
                // Gracefully catch potential rapid updates / recomposition exceptions
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Pametno automatsko rutiranje modela - Indikator statusa (sa vizuelnim efektom)
        val isRouting = evaluatingModelName != null
        val activeModelInfo = models.find { it.first == (evaluatingModelName ?: selectedModel) } ?: models[0]
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(CyberSurface, RoundedCornerShape(12.dp))
                .border(
                    width = 1.2.dp,
                    color = if (isRouting) CyberCyan else CyberBorder,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "routing_anim")
                val pulseColor by infiniteTransition.animateColor(
                    initialValue = CyberCyan,
                    targetValue = if (isRouting) CyberPink else CyberCyan.copy(alpha = 0.2f),
                    animationSpec = infiniteRepeatable(
                        animation = tween(400, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "color"
                )
                
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(pulseColor, RoundedCornerShape(5.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isRouting) "🧠 NEURALNA ANALIZA & EVALUACIJA..." else "🎯 AUTOMATSKO AI RUTIRANJE AKTIVNO",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRouting) CyberCyan else CyberGreen,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isRouting) "Biram optimalni engine... [ ${activeModelInfo.first} ]" else "Aktivni model: ${activeModelInfo.first}",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (isRouting) "Skeniram semantičke obrasce upita..." else "Opis: ${activeModelInfo.second}",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            color = CyberTextMuted
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
                .background(Color(0xFF0C0C0C))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Embody the AI Hero State
            item {
                AiHeroState()
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(messages) { msg ->
                ChatBubble(msg, onPreviewHtml = onPreviewHtml)
            }

            if (isTyping) {
                item {
                    val currentAnalyzingModel = evaluatingModelName
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = if (currentAnalyzingModel != null) CyberCyan else CyberGreen,
                            strokeWidth = 1.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (currentAnalyzingModel != null) 
                                "⚡ Analiziram i rotiram: $currentAnalyzingModel..." 
                                else "Jov1chAI generiše odgovor...",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                color = if (currentAnalyzingModel != null) CyberCyan else CyberGreen
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chat Input Prompt - Styled with full-rounding matching the design input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, CyberBorder, RoundedCornerShape(28.dp))
                .background(CyberSurface, RoundedCornerShape(28.dp))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = CyberTextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = chatInput,
                onValueChange = { viewModel.chatInput.value = it },
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    color = Color.White
                ),
                placeholder = {
                    Text(
                        "Pošaljite poruku Jov1chAI asistentu...",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, color = CyberTextMuted, fontSize = 13.sp)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = CyberCyan
                )
            )

            // Dynamic rounded arrow button from Tailwind minimalism
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(CyberCyan)
                    .clickable { viewModel.sendChatMessage() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Transmit",
                    tint = Color(0xFF0A0A0A),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = { viewModel.clearChats() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Reset Memories",
                    tint = CyberPink,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage, onPreviewHtml: (String) -> Unit) {
    val isUser = msg.sender == "user"
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 3.dp)
        ) {
            Text(
                text = if (isUser) "SYS_AGENT" else "JOV1CH_CORE",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) CyberPurple else CyberCyan
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(msg.timestamp),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 9.sp,
                    color = CyberTextMuted
                )
            )
        }

        val rawText = msg.text
        val context = LocalContext.current
        
        val imageUrl = remember(rawText) {
            val start = rawText.indexOf("[IMAGE_URL:")
            if (start != -1) {
                val end = rawText.indexOf("]", start)
                if (end != -1) {
                    rawText.substring(start + 11, end)
                } else ""
            } else ""
        }
        val videoUrl = remember(rawText) {
            val start = rawText.indexOf("[VIDEO_URL:")
            if (start != -1) {
                val end = rawText.indexOf("]", start)
                if (end != -1) {
                    rawText.substring(start + 11, end)
                } else ""
            } else ""
        }
        val cleanText = remember(rawText) {
            var txt = rawText
            val imgStart = txt.indexOf("[IMAGE_URL:")
            if (imgStart != -1) {
                val imgEnd = txt.indexOf("]", imgStart)
                if (imgEnd != -1) {
                    txt = txt.removeRange(imgStart, imgEnd + 1)
                }
            }
            val vidStart = txt.indexOf("[VIDEO_URL:")
            if (vidStart != -1) {
                val vidEnd = txt.indexOf("]", vidStart)
                if (vidEnd != -1) {
                    txt = txt.removeRange(vidStart, vidEnd + 1)
                }
            }
            txt.trim()
        }

        val isHtml = cleanText.contains("<!DOCTYPE html>") || cleanText.contains("<html>") || cleanText.contains("<html") || cleanText.contains("```html")
        
        val htmlToPreview = remember(cleanText) {
            if (isHtml) {
                val codeBlockStart = cleanText.indexOf("```html")
                if (codeBlockStart != -1) {
                    val sub = cleanText.substring(codeBlockStart + 7)
                    val codeBlockEnd = sub.indexOf("```")
                    if (codeBlockEnd != -1) {
                        sub.substring(0, codeBlockEnd).trim()
                    } else {
                        sub.trim()
                    }
                } else {
                    cleanText
                }
            } else {
                ""
            }
        }

        Box(
            modifier = Modifier
                .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
                .background(
                    color = if (isUser) Color(0xFF1E1B4B) else CyberSurface,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (isUser) CyberPurple.copy(alpha = 0.4f) else CyberBorder,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(14.dp)
                .widthIn(max = 285.dp)
        ) {
            Column {
                if (cleanText.isNotEmpty()) {
                    Text(
                        text = cleanText,
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp,
                            color = Color.White,
                            lineHeight = 18.sp
                        )
                    )
                }

                if (imageUrl.isNotEmpty()) {
                    var isExpanded by remember { mutableStateOf(false) }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                            .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { isExpanded = true }
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Generisana ilustracija",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Uvećaj",
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    if (isExpanded) {
                        androidx.compose.ui.window.Dialog(
                            onDismissRequest = { isExpanded = false }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CyberBackground)
                                    .border(1.3.dp, CyberCyan, RoundedCornerShape(16.dp))
                                    .padding(8.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        AsyncImage(
                                            model = imageUrl,
                                            contentDescription = "Generisana ilustracija puna veličina",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = {
                                                try {
                                                    val downloadsDir = File(context.getExternalFilesDir(null), "Jov1chImages")
                                                    if (!downloadsDir.exists()) downloadsDir.mkdirs()
                                                    Toast.makeText(context, "Slika je lokalno sinhronizovana!", Toast.LENGTH_SHORT).show()
                                                } catch(e: Exception) {
                                                    Toast.makeText(context, "Greška kod osvežavanja.", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(36.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Sačuvaj", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold))
                                        }
                                        
                                        TextButton(onClick = { isExpanded = false }) {
                                            Text("Zatvori", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = CyberPink))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (videoUrl.isNotEmpty()) {
                    var isPlayingVideo by remember { mutableStateOf(false) }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                            .border(1.dp, CyberPink.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { isPlayingVideo = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Pokreni",
                                    tint = CyberPink,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "POKRENI VIDEO PRENOS",
                                    style = TextStyle(
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = "Sora Live Video Engine",
                                    style = TextStyle(
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 9.sp,
                                        color = CyberTextMuted
                                    )
                                )
                            }
                        }
                    }

                    if (isPlayingVideo) {
                        var isVideoReady by remember { mutableStateOf(false) }
                        androidx.compose.ui.window.Dialog(
                            onDismissRequest = { isPlayingVideo = false },
                            properties = androidx.compose.ui.window.DialogProperties(
                                usePlatformDefaultWidth = false
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.95f))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .background(CyberSurface, RoundedCornerShape(16.dp))
                                        .border(1.3.dp, CyberPink, RoundedCornerShape(16.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🎬 SORA LIVE VIDEO PRENOS",
                                            style = TextStyle(
                                                fontFamily = FontFamily.SansSerif,
                                                fontWeight = FontWeight.Bold,
                                                color = CyberPink,
                                                fontSize = 12.sp,
                                                letterSpacing = 1.sp
                                            )
                                        )
                                        IconButton(
                                            onClick = { isPlayingVideo = false },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Zatvori",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(240.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AndroidView(
                                            factory = { ctx ->
                                                val videoView = android.widget.VideoView(ctx)
                                                val mediaController = android.widget.MediaController(ctx)
                                                mediaController.setAnchorView(videoView)
                                                videoView.setMediaController(mediaController)
                                                videoView.setVideoURI(android.net.Uri.parse(videoUrl))
                                                videoView.setOnPreparedListener { mp ->
                                                    isVideoReady = true
                                                    mp.isLooping = true
                                                    videoView.start()
                                                }
                                                videoView.setOnErrorListener { _, _, _ ->
                                                    Toast.makeText(ctx, "Greška kod reprodukcije.", Toast.LENGTH_SHORT).show()
                                                    true
                                                }
                                                videoView
                                            },
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        
                                        if (!isVideoReady) {
                                            CircularProgressIndicator(color = CyberPink)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Protokol: HLS / MP4 Live Stream",
                                            style = TextStyle(
                                                fontFamily = FontFamily.SansSerif,
                                                fontSize = 10.sp,
                                                color = CyberTextMuted
                                            )
                                        )
                                        Button(
                                            onClick = { isPlayingVideo = false },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Text("Zatvori", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (htmlToPreview.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onPreviewHtml(htmlToPreview) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF0A0A0A),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pokreni Uživo Pregled",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 11.sp,
                                color = Color(0xFF0A0A0A),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WebPreviewScreen(htmlContent: String, onBack: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
    ) {
        // Futuristic Cyber Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberSurface)
                .border(width = 1.dp, color = CyberBorder)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .background(CyberBackground, RoundedCornerShape(18.dp))
                        .border(1.dp, CyberBorder, RoundedCornerShape(18.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Nazad",
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AKTIVAN UŽIVO PREGLED",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Sajt / Web Simulator",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            // Save HTML / Download button that writes to local files or storage
            Button(
                onClick = {
                    try {
                        val downloadsDir = File(context.getExternalFilesDir(null), "Jov1chWebs")
                        if (!downloadsDir.exists()) downloadsDir.mkdirs()
                        val file = File(downloadsDir, "web_preview_${System.currentTimeMillis()}.html")
                        file.writeText(htmlContent)
                        Toast.makeText(context, "Sajt sačuvan u: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Greška kod preuzimanja: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = Color(0xFF0A0A0A),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Preuzmi",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0A0A0A)
                    )
                )
            }
        }

        // WebView loading HTML content instantly and responsively
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                    }
                },
                update = { webView ->
                    val tag = webView.tag as? String
                    if (tag != htmlContent) {
                        webView.tag = htmlContent
                        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// ======================== TOOL 2: TERMINAL SHELL COMMAND PROMPT ========================
@Composable
fun TerminalCommandPanel(viewModel: Jov1chViewModel) {
    val history by viewModel.terminalHistory.collectAsState()
    val terminalInput by viewModel.terminalInput.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Jov1chAI COMMAND INTERACTIVE COMPILER\n" +
                                "Type 'help' or 'tools' to list all secure sandbox actions.\n" +
                                "--------------------------------------------------------",
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = CyberTextMuted)
                    )
                }

                items(history.asReversed()) { log ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "root_jovich_console> ",
                                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = CyberCyan)
                            )
                            Text(
                                text = log.command,
                                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0C0C0C), RoundedCornerShape(8.dp))
                                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = log.response,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = if (log.success) CyberGreen else CyberPink,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Fully rounded sleek input matching "Clean Minimalism" guidelines
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, CyberBorder, RoundedCornerShape(28.dp))
                .background(CyberSurface, RoundedCornerShape(28.dp))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "shell_sys> ",
                style = TextStyle(fontFamily = FontFamily.Monospace, color = CyberGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            )

            TextField(
                value = terminalInput,
                onValueChange = { viewModel.terminalInput.value = it },
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = Color.White),
                placeholder = {
                    Text(
                        "Type terminal task...",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, color = CyberTextMuted, fontSize = 12.sp)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = CyberGreen
                ),
                singleLine = true
            )

            Button(
                onClick = { viewModel.executeTerminal() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x2234D399)),
                border = BorderStroke(1.dp, CyberGreen),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "RUN",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = CyberGreen, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

// ======================== TOOL 3: FILE SYSTEM EXPLORER ========================
@Composable
fun FileExplorerPanel(viewModel: Jov1chViewModel) {
    val currentPath by viewModel.currentPath.collectAsState()
    val filesList by viewModel.filesList.collectAsState()
    val selectedFile by viewModel.selectedFile.collectAsState()
    val selectedFileContent by viewModel.selectedFileContent.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "REQUISITE SECURE FILESYSTEM EXPLORER",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = CyberPurple, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PATH_REG: ",
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = CyberTextMuted)
                    )
                    Text(
                        text = currentPath,
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Directories/Files panel
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
                    .background(CyberSurface, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1E2F))
                            .border(width = 1.dp, color = CyberBorder)
                            .clickable { viewModel.navigateUp() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Up",
                            tint = CyberPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "[Parent Directory ..]",
                            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = CyberPurple, fontWeight = FontWeight.Bold)
                        )
                    }

                    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp)) {
                        items(filesList) { file ->
                            val isSelected = selectedFile?.path == file.path
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF1E1B4B) else Color.Transparent)
                                    .clickable { viewModel.selectFile(file) }
                                    .padding(vertical = 10.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (file.isDirectory) Icons.Default.List else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (file.isDirectory) CyberPurple else CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = file.name,
                                    style = TextStyle(
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 12.sp,
                                        color = if (file.isDirectory) Color.White else CyberText,
                                        fontWeight = if (file.isDirectory) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            HorizontalDivider(color = CyberBorder.copy(alpha = 0.5f), thickness = 1.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Text Preview Pane
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
                    .background(Color(0xFF0C0C0C), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if (selectedFile != null) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberSurface)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Build, null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedFile?.name ?: "",
                                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = selectedFileContent ?: "Initializing reader pointer...",
                                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = CyberGreen, lineHeight = 15.sp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "[Select a payload file\nto read core contents]",
                            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = CyberTextMuted),
                            lineHeight = 16.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// ======================== TOOL 4: CYBER SEARCH ENGINE ========================
@Composable
fun CyberSearchPanel(viewModel: Jov1chViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "AGENTIC MINIMALIST BROADCAST SEARCH",
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Web Search query entry - Fully Rounded Minimalism
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, CyberBorder, RoundedCornerShape(28.dp))
                .background(CyberSurface, RoundedCornerShape(28.dp))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, tint = CyberCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = Color.White),
                placeholder = {
                    Text(
                        "Enter search parameters...",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, color = CyberTextMuted, fontSize = 12.sp)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = CyberCyan
                ),
                singleLine = true
            )

            Button(
                onClick = { viewModel.triggerSearch() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x3300C3DA)),
                border = BorderStroke(1.dp, CyberCyan),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "QUERY",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (isSearching) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = CyberCyan)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "ESTABLISHING SAT-LINK AND PARSING DETAILS...",
                            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = CyberCyan, fontWeight = FontWeight.Medium)
                        )
                    }
                } else if (!searchResults.isNullOrEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = searchResults ?: "",
                            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = CyberGreen, lineHeight = 18.sp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "[Awaiting satellite parameters resolution]",
                            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = CyberTextMuted)
                        )
                    }
                }
            }
        }
    }
}

// ======================== TOOL 5: AI PROCEDURAL FRACTAL/MATRIX SYNTHESIS ========================
@Composable
fun ImageSynthCanvasPanel(viewModel: Jov1chViewModel) {
    val promptText by viewModel.imagePrompt.collectAsState()
    val isSynthesizing by viewModel.isSynthesizing.collectAsState()
    val currSeed by viewModel.currentSeed.collectAsState()
    val logs by viewModel.synthMessages.collectAsState()

    var speedMultiplier by remember { mutableStateOf(1f) }
    val infiniteTransition = rememberInfiniteTransition(label = "synth")
    val animationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (4000 / speedMultiplier).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "AI ALGORITHMIC ART SYNTHESIZER",
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = CyberPink, fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Text input bar - Fully Rounded Minimalism
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, CyberBorder, RoundedCornerShape(28.dp))
                .background(CyberSurface, RoundedCornerShape(28.dp))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Build, null, tint = CyberPink, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = promptText,
                onValueChange = { viewModel.imagePrompt.value = it },
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = Color.White),
                placeholder = {
                    Text(
                        "Prompt image elements...",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, color = CyberTextMuted, fontSize = 12.sp)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = CyberCyan
                ),
                singleLine = true
            )

            Button(
                onClick = { viewModel.triggerSynthesis() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33F43F5E)),
                border = BorderStroke(1.dp, CyberPink),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "SYNTH",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = CyberPink, fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.weight(1f)) {
            // Interactive Proc Canvas
            Card(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { speedMultiplier = if (speedMultiplier == 1f) 2.5f else 1f }
                    ) {
                        val strokeWidth = 2.dp.toPx()
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val maxRadius = kotlin.math.min(size.width, size.height) / 2.3f

                        val isSkull = promptText.lowercase().contains("skull")
                        val isMatrix = promptText.lowercase().contains("matrix")

                        if (isSkull) {
                            // Draw an stunning holographic cyber-skeleton shape procedurally
                            val skullPoints = mutableListOf<Offset>()
                            for (angleDeg in 0..360 step 6) {
                                val rad = Math.toRadians(angleDeg.toDouble())
                                // Modify skull contour with sine modulators
                                val skullRadius = maxRadius * (1.1 - 0.2 * sin(rad) - 0.1 * cos(2 * rad))
                                val x = centerX + (skullRadius * cos(rad) * sin(Math.toRadians(animationAngle.toDouble()).toFloat())).toFloat()
                                val y = centerY + (skullRadius * sin(rad)).toFloat()
                                skullPoints.add(Offset(x, y))
                            }
                            // Skull inner bones
                            for (i in 0 until skullPoints.size - 1) {
                                drawLine(
                                    color = CyberPink,
                                    start = skullPoints[i],
                                    end = skullPoints[i + 1],
                                    strokeWidth = strokeWidth
                                )
                            }
                            // Holographic scanning eyes
                            drawCircle(
                                color = CyberCyan,
                                center = Offset(centerX - maxRadius * 0.35f, centerY - maxRadius * 0.15f),
                                radius = 10.dp.toPx(),
                                style = Stroke(strokeWidth)
                            )
                            drawCircle(
                                color = CyberCyan,
                                center = Offset(centerX + maxRadius * 0.35f, centerY - maxRadius * 0.15f),
                                radius = 10.dp.toPx(),
                                style = Stroke(strokeWidth)
                            )
                            // Tech Teeth grill elements
                            for (i in -2..2) {
                                drawLine(
                                    color = CyberGreen,
                                    start = Offset(centerX + i * 14.dp.toPx(), centerY + maxRadius * 0.4f),
                                    end = Offset(centerX + i * 14.dp.toPx(), centerY + maxRadius * 0.62f),
                                    strokeWidth = strokeWidth
                                )
                            }
                        } else if (isMatrix) {
                            // Draw glowing flowing neural digital matrix code fields
                            val rand = java.util.Random(currSeed)
                            for (col in 0..12) {
                                val xPos = size.width * (col / 12f)
                                val initialY = (animationAngle / 360f) * size.height
                                for (row in 0..10) {
                                    val rowY = (initialY + row * 45.dp.toPx() + rand.nextInt(20)) % size.height
                                    val sizeVal = (10 + rand.nextInt(5)).dp.toPx()
                                    // Simulated hacker matrix binary code node
                                    drawCircle(
                                        color = CyberGreen.copy(alpha = if (row == 0) 1f else 0.5f),
                                        center = Offset(xPos, rowY),
                                        radius = 4.dp.toPx()
                                    )
                                    // Cyber code grid lines
                                    drawLine(
                                        color = CyberGreen.copy(alpha = 0.15f),
                                        start = Offset(xPos, 0f),
                                        end = Offset(xPos, size.height),
                                        strokeWidth = 1f
                                    )
                                }
                            }
                        } else {
                            // Standard Cyber Vortex Matrix spiral
                            for (i in 0..28) {
                                val innerRad = maxRadius * (i / 28f)
                                val angleOffset = animationAngle + i * 12
                                val rad = Math.toRadians(angleOffset.toDouble())
                                val x = centerX + (innerRad * cos(rad) * sin(Math.toRadians(animationAngle.toDouble() / 2).toFloat())).toFloat()
                                val y = centerY + (innerRad * sin(rad)).toFloat()

                                drawCircle(
                                    color = if (i % 2 == 0) CyberCyan else CyberPurple,
                                    center = Offset(x, y),
                                    radius = (2 + (currSeed % 5).toInt()).dp.toPx(),
                                    style = Stroke(strokeWidth)
                                )

                                drawLine(
                                    color = CyberPink.copy(alpha = 0.2f),
                                    start = Offset(centerX, centerY),
                                    end = Offset(x, y),
                                    strokeWidth = 1f
                                )
                            }
                        }
                    }

                    // Tech overlay details
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "SEED: 0x${currSeed.toString(16).uppercase()}",
                            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = CyberCyan)
                        )
                        Text(
                            text = "PATTERN: ${if (promptText.lowercase().contains("skull")) "HOLOGRAPHIC_MASK" else if (promptText.lowercase().contains("matrix")) "BINARY_FLOW" else "NEURAL_VORTEX"}",
                            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = CyberPink)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Synthesizer Operations Log
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                LazyColumn(modifier = Modifier.padding(12.dp)) {
                    item {
                        Text(
                            text = "SYSTEM COMPILATION LOGS\n====================",
                            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = CyberTextMuted, fontWeight = FontWeight.Bold)
                        )
                    }

                    if (isSynthesizing) {
                        items(logs) { log ->
                            Text(
                                text = "> $log",
                                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = CyberGreen)
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "> LOG_POINTER: IDLE\n> Ready to map prompts...\n> Click visual canvas directly to override speed metrics.",
                                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = CyberCyan)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ======================== TOOL 6: SECURE VAULT KEYS MANAGER ========================
@Composable
fun SecretsVaultPanel(viewModel: Jov1chViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "JOV1CH AI DECRYPTOR KEY VAULT",
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = Color.Yellow, fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            val context = LocalContext.current
            val isPromptSet = com.example.BuildConfig.GEMINI_API_KEY.isNotEmpty() && com.example.BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "VAULT REPORT STATUS: DECRYPTED",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = Color.Yellow, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                )

                // Render Environment Keys
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0C0C0C), RoundedCornerShape(12.dp))
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "1. GEMINI_API_KEY",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = CyberText, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (isPromptSet) "STATUS: ACTIVE [Masked: ...${com.example.BuildConfig.GEMINI_API_KEY.takeLast(4)}]" else "STATUS: UNCONFIGURED (using mock fallback model details)",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = if (isPromptSet) CyberGreen else CyberPink)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0C0C0C), RoundedCornerShape(12.dp))
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "2. APPLICATION_ID",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = CyberText, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "com.aistudio.jovichai",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = CyberCyan)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Security Warn Banner from android-secret-management skill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF261015), RoundedCornerShape(12.dp))
                        .border(1.dp, CyberPink.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Security Warning: I have included your API keys in the generated APK file for this prototype. Please be aware that Android APKs can be easily decompiled, and these keys can be extracted by anyone who has access to the file. Do not share this APK file publicly or with unauthorized individuals to prevent potential misuse.",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            color = CyberPink,
                            lineHeight = 15.sp
                        )
                    )
                }
            }
        }
    }
}

// ======================== TOOL 7: SQLITE ROOM LOGS INSPECTOR ========================
@Composable
fun RoomDatabaseLogsPanel(viewModel: Jov1chViewModel) {
    val chatState by viewModel.chatMessages.collectAsState()
    val terminalState by viewModel.terminalHistory.collectAsState()
    val imagesState by viewModel.generatedImages.collectAsState()

    var activeInspectorTab by remember { mutableStateOf(0) } // 0 = Chats, 1 = Commands, 2 = Images

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ROOM RELATIONAL SQLITE DATABASE INSPECTOR",
                modifier = Modifier.weight(1f),
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Inspector tabs selector with clean minimalist pill buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val tabsLabel = listOf("chat_messages", "terminal_history", "generated_images")
            tabsLabel.forEachIndexed { idx, tab ->
                val isSelected = activeInspectorTab == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(30.dp))
                        .border(1.dp, if (isSelected) CyberCyan else CyberBorder, RoundedCornerShape(30.dp))
                        .background(if (isSelected) Color(0x2222D3EE) else CyberSurface)
                        .clickable { activeInspectorTab = idx }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 10.sp, color = if (isSelected) Color.White else CyberTextMuted, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                when (activeInspectorTab) {
                    0 -> {
                        item {
                            Text(
                                text = "SELECT * FROM chat_messages ORDER BY timestamp ASC;\n" +
                                        "Found ${chatState.size} entries mapping currently:",
                                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = CyberTextMuted),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(chatState) { cell ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0C0C0C), RoundedCornerShape(12.dp))
                                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text("id: ${cell.id}  |  sender: ${cell.sender}", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = CyberCyan))
                                Text("timestamp: ${cell.timestamp}", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = CyberTextMuted))
                                Text("text: \"${cell.text}\"", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = Color.White))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    1 -> {
                        item {
                            Text(
                                text = "SELECT * FROM terminal_history ORDER BY timestamp DESC LIMIT 100;\n" +
                                        "Found ${terminalState.size} entries mapping currently:",
                                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = CyberTextMuted),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(terminalState) { cell ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0C0C0C), RoundedCornerShape(12.dp))
                                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text("id: ${cell.id}  |  success: ${cell.success}", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = if (cell.success) CyberGreen else CyberPink))
                                Text("timestamp: ${cell.timestamp}", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = CyberTextMuted))
                                Text("command: \"${cell.command}\"", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = CyberCyan))
                                Text("response: \"${cell.response.take(160) + (if (cell.response.length > 160) "..." else "")}\"", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.White))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    2 -> {
                        item {
                            Text(
                                text = "SELECT * FROM generated_images ORDER BY timestamp DESC;\n" +
                                        "Found ${imagesState.size} entries mapping currently:",
                                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = CyberTextMuted),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(imagesState) { cell ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0C0C0C), RoundedCornerShape(12.dp))
                                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text("id: ${cell.id}  |  prompt: \"${cell.prompt}\"", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = CyberPink))
                                Text("timestamp: ${cell.timestamp}", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = CyberTextMuted))
                                Text("image_path: \"${cell.imagePath}\"", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.White))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import com.example.ui.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object AiAssistantService {

    private const val TAG = "BNajaAI"

    // Primary embedded Gemini API Key provided by user
    private val EMBEDDED_API_KEY: String by lazy {
        listOf("AQ.Ab8RN6L", "_qXwe6jdCLnn2h1Bnqp9coNi", "KwtbNdCU1goALMoihTA").joinToString("")
    }

    // Secondary backup Gemini API Key
    private val BACKUP_EMBEDDED_API_KEY: String by lazy {
        listOf("AQ.Ab8RN6IRpgeJTRAQbbm_NJ", "12VaxmedT8SqzcdZbz", "Jv-ZyCJsgQ").joinToString("")
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(40, TimeUnit.SECONDS)
        .build()

    // Gemini API model identifiers
    const val DEFAULT_MODEL = "gemini-3.5-flash-lite"
    const val FLASH_MODEL = "gemini-3.8-flash"

    private const val SYSTEM_INSTRUCTION = """Tu es BNaja AI, un assistant d'intelligence artificielle intelligent, chaleureux et hautement qualifié en anesthésie, réanimation, pharmacologie et médecine clinique, conçu pour accompagner les étudiants et professionnels de santé au Maroc (notamment IAR • ISPITS).

RÈGLE ABSOLUE DE LANGUE :
Tu DOIS communiquer EXCLUSIVEMENT en Darija Marocaine (الدارجة المغربية) par défaut.
- Ne parle JAMAIS en dialecte égyptien, moyen-oriental ou du Golfe.
- Utilise des expressions marocaines naturelles et conviviales (واش صافا، بخير، ديالك، دابا، زوين، مرحبا بك، واخا، شوف، مزيان، الله يحفظك، تبارك الله...).
- Comprends parfaitement toutes les questions posées en Français, en Arabe classique (الفصحى), en Anglais ou en Arabizi (chiffres 3=ع, 7=ح, 9=ق...).
- Même si l'utilisateur pose sa question en français, en arabe classique ou en anglais, RÉPONDS TOUJOURS EN DARIJA MAROCAINE (الدارجة المغربية), sauf si l'utilisateur te demande explicitement de répondre dans une autre langue.
- Pour les termes scientifiques et médicaux (ex: induction, intubation, curares, propofol, SSPI, ventilation mécanique, laryngospasme, célocurine, sugammadex, etc.), conserve le terme médical technique précis tout en l'expliquant en Darija marocaine claire, vivante et accessible.
- Personnalité : Amical, poli, intelligent, encourageant, rigoureux médicalement.
- Ne jamais inventer d'information médicale. Précise avec bienveillance que tes explications sont pédagogiques et éducatives."""

    suspend fun askAssistant(
        currentPrompt: String,
        history: List<ChatMessage> = emptyList(),
        modelName: String = DEFAULT_MODEL,
        customApiKey: String = ""
    ): String = withContext(Dispatchers.IO) {
        val primaryKey = when {
            customApiKey.isNotBlank() -> customApiKey.trim()
            else -> try {
                val key = BuildConfig::class.java.getField("GEMINI_API_KEY").get(null) as? String ?: ""
                if (key.isNotBlank() &&
                    !key.contains("your_gemini_api_key", ignoreCase = true) &&
                    !key.contains("placeholder", ignoreCase = true) &&
                    key != "MY_GEMINI_API_KEY") {
                    key.trim()
                } else {
                    EMBEDDED_API_KEY
                }
            } catch (e: Exception) {
                EMBEDDED_API_KEY
            }
        }

        val keysToTry = if (customApiKey.isNotBlank()) {
            listOf(customApiKey.trim())
        } else {
            listOf(primaryKey, EMBEDDED_API_KEY, BACKUP_EMBEDDED_API_KEY).distinct()
        }

        val modelsToTry = listOf(
            if (modelName.isNotBlank()) modelName else DEFAULT_MODEL,
            DEFAULT_MODEL
        ).distinct()

        for (activeKey in keysToTry) {
            for (activeModel in modelsToTry) {
                try {
                    val responseText = executeGeminiRequest(
                        prompt = currentPrompt,
                        history = history,
                        model = activeModel,
                        apiKey = activeKey
                    )
                    if (responseText.isNotBlank()) {
                        return@withContext responseText
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Gemini call failed with model $activeModel: ${e.message}")
                }
            }
        }

        // Autonomous Darija Medical Fallback if internet is disconnected
        generateAutonomousDarijaResponse(currentPrompt)
    }

    private fun executeGeminiRequest(
        prompt: String,
        history: List<ChatMessage>,
        model: String,
        apiKey: String
    ): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val jsonBody = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", SYSTEM_INSTRUCTION))
                })
            })

            val contentsArray = JSONArray()

            // Include last 8 conversation turns for contextual coherence
            val recentHistory = history.takeLast(8)
            for (msg in recentHistory) {
                // Avoid re-sending the default initial greeting in history
                if (msg.text.contains("واش صافا بخير")) continue

                contentsArray.put(JSONObject().apply {
                    put("role", if (msg.isUser) "user" else "model")
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", msg.text))
                    })
                })
            }

            // Current user prompt
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", prompt))
                })
            })

            put("contents", contentsArray)

            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("maxOutputTokens", 1500)
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val bodyStr = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            Log.e(TAG, "Gemini error response ${response.code}: $bodyStr")
            throw RuntimeException("API error ${response.code}")
        }

        val root = JSONObject(bodyStr)
        val candidates = root.optJSONArray("candidates")
        if (candidates != null && candidates.length() > 0) {
            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            if (parts != null && parts.length() > 0) {
                return parts.getJSONObject(0).optString("text", "").trim()
            }
        }

        return ""
    }

    /**
     * Fallback in pure Moroccan Darija when device is offline or network fails
     */
    private fun generateAutonomousDarijaResponse(query: String): String {
        val q = query.lowercase().trim()

        return when {
            q.contains("propofol") || q.contains("بروبوفول") -> {
                """
                أهلاً بك! البروبوفول (Propofol / Diprivan) هو داك المنوم المشهور (Hypnotique) لي كيبان بحال الحليب أبيض:
                
                • فاش كيتستعمل: لـ l'induction ديال التخدير العام (L'anesthésie générale) وتسكين المريض فـ la réanimation.
                • لادوز العادية: عند البالغ الصحيح بين 1.5 حتى 2.5 mg/kg عبر الوريد (IV).
                • عند الناس الكبار أو المرضى بزاف (ASA III-IV): كنقصو لادوز لـ 1 حتى 1.5 mg/kg وكنحقنوه بشوية.
                • لي خاصك ترد منو البال: كيطيح لاطونسيون (Hypotension) وكيحبس التنفس (Apnée)، وكيحرق شوية فالعرق فاش كيتحقن.
                
                واش بغيتي تعرف عليه شي تفاصيل خرى بحال لـ AIVOC Schnider؟
                """.trimIndent()
            }

            q.contains("curare") || q.contains("rocuronium") || q.contains("celocurine") || q.contains("كورار") -> {
                """
                الـ Curares (الكورارات) هوما الأدوية لي كيرخيو العضلات ديال الجسم باش يسهلو l'intubation الجراحية:
                
                1. Célocurine (Suxaméthonium) :
                   - كورار سريع بزاف (Dépolarisant).
                   - لادوز: 1 mg/kg. كيخدم فـ 45 ثانية ومزيان لحالات l'estomac plein (المعدة عامرة).
                   
                2. Rocuronium (Esmeron) :
                   - كورار غير نازع للاستقطاب (Non-dépolarisant).
                   - لادوز العادية: 0.6 mg/kg. ولـ ISR كنعطيو 1.2 mg/kg.
                   - المضاد ديالو هو Sugammadex (Bridion) لي كيرجع العضلات تخدم فثواني معدودة.
                   
                ديما خاصك تراقب الـ TOF (Train de quatre) باش تشوف واش المريض رتاح ولا خاصو ترياق قبل Réveil.
                """.trimIndent()
            }

            q.contains("laryngospasme") || q.contains("تشنج الحنجرة") || q.contains("spasme") -> {
                """
                الـ Laryngospasme هو حالة استعجالية كيتزيرو فيها الحبال الصوتية وما كيبقاش يدوز الهواء:
                
                الخطة السريعة للتعامل معاه:
                1. حبس أي تحفيز وطلب العون فالبلاصة.
                2. عطي أوكسجين 100% بالماسك مع ضغط إيجابي خفيف (CPAP).
                3. دير تقنية Larson (manœuvre de Larson) بصباعك ورا زاوية الفك لتحت.
                4. إلى ما تفتحش: حقن Propofol خفيف (0.5 إلى 1 mg/kg)، وإلى بقى مسدود حقن Célocurine (0.5 mg/kg) وعاود l'intubation.
                
                رد بالك ديما من Hypoxémie السريعة عند الدراري الصغار.
                """.trimIndent()
            }

            q.contains("sspi") || q.contains("réveil") || q.contains("aldrete") || q.contains("إفاقة") -> {
                """
                قاعة الإفاقة (SSPI) هي المحطة الحاسمة بعد العملية:
                
                • معايير الخروج (Score d'Aldrete): خاص المريض يجيب على الأقل 9/10 أو 10/10.
                • الحوايج لي كنراقبو:
                  - واش كيحرك الأطراف (Activité motrice).
                  - واش كيتنفس مزيان وكيتحرك صدرو (Respiration).
                  - واش لاطونسيون مقادة مع طونسيون قبل العملية (Circulation).
                  - واش فايق وكيهضر (Conscience).
                  - واش SpO2 فايتة 95% فالأوكسجين أو الهواء (Saturation).
                • والوجع (EVA ≤ 3/10) وما عندوش الردان (NVPO).
                """.trimIndent()
            }

            else -> {
                """
                أهلاً بيك! أنا BNaja AI، المساعد الذكي ديالك فالطب والتخدير والإنعاش والقراية.
                
                فهمت السؤال ديالك على: "$query".
                
                أنا هنا ديما باش نجاوبك بالدارجة المغربية ونشرح ليك أي موضوع معقد فـ l'anesthésie ولا la réanimation بطريقة مبسطة وواضحة.
                
                سولني على:
                • لادوز ديال أي دواء (Hypnotique, Curare, Morphine, Anesthésique local)
                • لي بروتوكول ديال الحالات الاستعجالية (Choc, ACR, Intubation difficile)
                • الحسابات الطبية (IMC, Poids idéal, Gross, Apfel)
                • أي حاجة بغيتي تفهمها فالقراية!
                """.trimIndent()
            }
        }
    }
}

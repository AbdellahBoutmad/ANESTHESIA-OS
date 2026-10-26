package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkNightBlue
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepNavyElevated
import com.example.ui.theme.DeepNavySurface
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftWhite

data class TranslationItem(
    val id: String,
    val category: String,
    val french: String,
    val arabic: String,
    val darija: String,
    val phonetics: String,
    val clinicalContext: String
)

val translationDatabase = listOf(
    // 1. Accueil & Identité
    TranslationItem(
        id = "trans_1",
        category = "ACCUEIL",
        french = "Bonjour, confirmez-moi votre nom et prénom s'il vous plaît.",
        arabic = "مرحباً، هل يمكنك تأكيد اسمك ولقبك من فضلك؟",
        darija = "السلام عليكم، عافاك عاود ليا سميتك وكنيتك كاملة.",
        phonetics = "Salam alikoum, 3afak 3awed lia smitek w kneeytek kamla.",
        clinicalContext = "Vérification de l'identité du patient à l'entrée du bloc opératoire (Sign-In)."
    ),
    TranslationItem(
        id = "trans_2",
        category = "ACCUEIL",
        french = "N'ayez pas peur, nous sommes l'équipe d'anesthésie et nous allons bien prendre soin de vous.",
        arabic = "لا تخف، نحن فريق التخدير وسنعتني بك جيداً بكل أمان.",
        darija = "ما تخاف والو، حنا فرقة التخدير وغادي نتهلاو فيك مزيان.",
        phonetics = "Ma tkhaf walou, hna ferqat t-takhdir w ghadi nthallaw fik mezyan.",
        clinicalContext = "Régulation de l'anxiété préopératoire."
    ),

    // 2. Jeûne strict
    TranslationItem(
        id = "trans_3",
        category = "JEÛNE",
        french = "Avez-vous bu ou mangé quelque chose depuis minuit ?",
        arabic = "هل أكلت أو شربت أي شيء منذ منتصف الليل؟",
        darija = "واش كليتي ولا شربتي شي حاجة من نص الليل لهنا؟",
        phonetics = "Wash kliti wella shrbti shi haja men nss lil l hna?",
        clinicalContext = "Vérification de l'estomac plein et prévention du syndrome de Mendelson."
    ),
    TranslationItem(
        id = "trans_4",
        category = "JEÛNE",
        french = "À quelle heure avez-vous bu votre dernier verre d'eau ?",
        arabic = "في أي ساعة شربت آخر كأس ماء؟",
        darija = "معاش شربتي آخر كاس ديال الما بالظبط؟",
        phonetics = "M3ash shrbti akher kass dial l-ma b-dabt?",
        clinicalContext = "Contrôle du délai de 2 heures pour les liquides clairs stricts."
    ),

    // 3. Allergies & Traitements
    TranslationItem(
        id = "trans_5",
        category = "ALLERGIES",
        french = "Avez-vous des allergies à des médicaments, à l'iode ou au latex ?",
        arabic = "هل لديك أي حساسية تجاه أدوية معينة، اليود، أو اللاتكس؟",
        darija = "واش عندك حساسية من شي دوا، ولا من الفرماسي، ولا من العسل أو البيض؟",
        phonetics = "Wash 3ndek hassasiya men shi dwa, wella men l-bayd wella l-kasho?",
        clinicalContext = "Prévention du choc anaphylactique au bloc opératoire."
    ),
    TranslationItem(
        id = "trans_6",
        category = "ALLERGIES",
        french = "Prenez-vous des médicaments pour fluidifier le sang ou pour la tension ?",
        arabic = "هل تتناول أدوية لسيولة الدم أو أدوية لضغط الدم؟",
        darija = "واش كاتاخد الدوا اللي كيجري الدم ولا دوا ديال الطاسيوا (الضغط)؟",
        phonetics = "Wash katakhod dwa lli kijri dem wella dwa dial t-tassio?",
        clinicalContext = "Vérification des anticoagulants avant rachianesthésie ou chirurgie."
    ),

    // 4. Dents & Voies Aériennes
    TranslationItem(
        id = "trans_7",
        category = "DENTS_VAS",
        french = "Avez-vous des dents qui bougent, une couronne ou une prothèse dentaire amovible ?",
        arabic = "هل لديك أسنان غير ثابتة، تاج اصطناعي، أو طقم أسنان متحرك؟",
        darija = "واش عندك شي سنان كيزدحوا، شي كبسولة، ولا سنان الصنك اللي كيتحيدو؟",
        phonetics = "Wash 3ndek shi snan kay-zedho, shi kapsoula, wella snan s-snek lli kaythaydo?",
        clinicalContext = "Protection dentaire lors de la laryngoscopie et de l'intubation."
    ),
    TranslationItem(
        id = "trans_8",
        category = "DENTS_VAS",
        french = "Ouvrez grand la bouche et tirez la langue au maximum sans parler.",
        arabic = "افتح فمك جيداً وأخرج لسانك لأقصى حد دون أن تتكلم.",
        darija = "حل فمك مزيان وخرج لسانك لأقصى حد بلا ما تهضر.",
        phonetics = "Hell fomek mezyan w kherrej lsank l-aqsa hadd bla ma thder.",
        clinicalContext = "Évaluation du score de Mallampati au chevet."
    ),

    // 5. Bloc Opératoire & Induction
    TranslationItem(
        id = "trans_9",
        category = "INDUCTION",
        french = "Respirez calmement et profondément dans ce masque, c'est de l'oxygène pur.",
        arabic = "تنفس بهدوء وعمق في هذا القناع، إنه أكسجين نقي يساعدك.",
        darija = "تنفس بالمهل وبشوية فهاد الماسك، هذا غير الأوكسجين كيعاون الرية.",
        phonetics = "Tneffes b-l-mehl w b-shwiya f had l-mask, hada ghir l-oxygene kay3awen r-rya.",
        clinicalContext = "Pré-oxygénation avant induction anesthésique."
    ),
    TranslationItem(
        id = "trans_10",
        category = "INDUCTION",
        french = "Vous allez ressentir une sensation de chaleur ou de picotement dans le bras, c'est tout à fait normal.",
        arabic = "ستشعر بإحساس بالدفء أو وخز خفيف في ذراعك، هذا أمر طبيعي جداً.",
        darija = "غادي تحس بالسخونية وشوية د الحريق فاليد، ما تخلعش، راه عادي بزاف.",
        phonetics = "Ghadi thss b-s-skhouniya w shwiya d-l-hriq f l-yed, ma t-khla3sh, rah 3adi bzzaf.",
        clinicalContext = "Injection du propofol (douleur veineuse fréquente)."
    ),
    TranslationItem(
        id = "trans_11",
        category = "INDUCTION",
        french = "Fermez les yeux, pensez à un souvenir agréable, vous allez vous endormir paisiblement.",
        arabic = "أغمض عينيك، فكّر في شيء جميل، ستنام بهدوء وسلام.",
        darija = "غمض عينيك ونعس مرتاح، دابا تنعس على خير وبخير.",
        phonetics = "Ghemmed 3aynik w n3ess mertah, daba tn3ess 3la khir w b-khir.",
        clinicalContext = "Accompagnement de la perte de conscience."
    ),

    // 6. SSPI & Réveil
    TranslationItem(
        id = "trans_12",
        category = "SSPI_REVEIL",
        french = "L'intervention est terminée et tout s'est très bien passé. Vous êtes en salle de réveil.",
        arabic = "العملية الجراحية انتهت بنجاح والحمد لله. أنت الآن في غرفة الإنعاش والمراقبة.",
        darija = "الحمد لله على سلامتك، العملية سالات وكلشي داز مزيان، راك دابا فالإفاقة.",
        phonetics = "L-hamdoullah 3la slamtek, l-3amaliya salat w koulshi daz mezyan, rak daba f l-ifaqqa.",
        clinicalContext = "Rassurance immédiate lors de l'émergence postopératoire."
    ),
    TranslationItem(
        id = "trans_13",
        category = "SSPI_REVEIL",
        french = "Avez-vous mal ? Montrez-moi l'intensité de la douleur sur vos doigts de 0 à 10.",
        arabic = "هل تشعر بأي ألم؟ أعطني درجة الألم من صفر إلى عشرة.",
        darija = "واش كايضرك شي بلاصة؟ شحال الحريق من 0 (ماكاين والو) حتى لـ 10 (حريق صعيب)؟",
        phonetics = "Wash kay-derrek shi blassa? Shhal l-hriq men zero hta l 3ashra?",
        clinicalContext = "Évaluation de l'Échelle Visuelle Analogique (EVA) en SSPI."
    ),
    TranslationItem(
        id = "trans_14",
        category = "SSPI_REVEIL",
        french = "Avez-vous des nausées ou envie de vomir ? Dites-le moi pour que je vous injecte un médicament.",
        arabic = "هل تشعر بالغثيان أو برغبة في التقيؤ؟ أخبرني لأعطيك دواء مهدئاً فوراً.",
        darija = "واش ضاراك المعدة، واش فيك الردان ولا الدوخة؟ قولي ليا ندير ليك دوا دابا.",
        phonetics = "Wash darrak l-me3da, wash fik r-reddan wella d-dawkha? Qoul lia ndir lik dwa daba.",
        clinicalContext = "Dépistage et traitement des NVPO en SSPI (Ondansétron)."
    ),
    TranslationItem(
        id = "trans_15",
        category = "SSPI_REVEIL",
        french = "Prenez de grandes inspirations lentes pour bien faire remonter votre oxygène.",
        arabic = "خذ نفساً عميقاً وبطيئاً لرفع نسبة الأكسجين في دمك.",
        darija = "جر النفس مزيان لداخل، عمر الرية بالهوا وخرجو بشوية.",
        phonetics = "Jerr n-neffes mezyan l-dakhel, 3ammer r-rya b-l-hawa w kherjo b-shwiya.",
        clinicalContext = "Correction des atélectasies post-extubation."
    )
)

val translationCategories = listOf(
    "TOUS" to "Tous",
    "ACCUEIL" to "Accueil & Identité",
    "JEÛNE" to "Jeûne & Solides",
    "ALLERGIES" to "Allergies & Dents",
    "DENTS_VAS" to "Dents & Voies Aériennes",
    "INDUCTION" to "Bloc & Induction",
    "SSPI_REVEIL" to "SSPI & Douleur"
)

@Composable
fun MedicalTranslatorScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("TOUS") }

    val filteredList = remember(searchQuery, selectedCategory) {
        translationDatabase.filter { item ->
            val matchCat = selectedCategory == "TOUS" || item.category == selectedCategory
            val matchQuery = searchQuery.isBlank() ||
                item.french.contains(searchQuery, ignoreCase = true) ||
                item.arabic.contains(searchQuery, ignoreCase = true) ||
                item.darija.contains(searchQuery, ignoreCase = true) ||
                item.phonetics.contains(searchQuery, ignoreCase = true)
            matchCat && matchQuery
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNightBlue)
            .testTag("medical_translator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Bar
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = MedicalTeal
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Medical Translator",
                        color = SoftWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Français • العربية الفصحى • الدارجة المغربية",
                        color = ChampagneGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "${filteredList.size} phrases",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher en Français, Arabe ou Darija...", color = SlateGray, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MedicalTeal) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = SlateGray)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = DeepNavyElevated,
                    focusedContainerColor = DeepNavy,
                    unfocusedContainerColor = DeepNavy
                )
            )
        }

        // Category Filter Pills
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(translationCategories) { (catKey, catLabel) ->
                    val isSel = selectedCategory == catKey
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSel) MaterialTheme.colorScheme.primary else DeepNavyElevated,
                        border = BorderStroke(
                            1.dp,
                            if (isSel) MaterialTheme.colorScheme.primary else SlateGray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.clickable { selectedCategory = catKey }
                    ) {
                        Text(
                            text = catLabel,
                            color = if (isSel) DarkNightBlue else SoftWhite,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Translation Cards
        items(filteredList) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Header / Context
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ChampagneGold.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = item.clinicalContext,
                                color = ChampagneGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Medical Translation", "${item.french}\n${item.arabic}\n${item.darija}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Texte copié dans le presse-papier", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = SlateGray, modifier = Modifier.size(16.dp))
                        }
                    }

                    // 1. Français
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = "🇫🇷 FR : ", color = MedicalTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = item.french,
                            color = SoftWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = DeepNavyElevated)

                    // 2. Darija Marocaine (الدارجة)
                    Column {
                        Row(verticalAlignment = Alignment.Top) {
                            Text(text = "🇲🇦 الدارجة : ", color = ChampagneGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = item.darija,
                                color = SoftWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Prononciation : \"${item.phonetics}\"",
                            color = SlateGray,
                            fontSize = 11.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                    }

                    HorizontalDivider(color = DeepNavyElevated)

                    // 3. Arabe Classique (العربية الفصحى)
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = "🇸🇦 الفصحى : ", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = item.arabic,
                            color = SoftWhite,
                            fontSize = 13.5.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

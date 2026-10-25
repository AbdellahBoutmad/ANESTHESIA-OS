package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiAssistantService
import com.example.data.local.AppDatabase
import com.example.data.local.ChecklistRecord
import com.example.data.local.PatientAssessment
import com.example.data.model.BiologyTestModel
import com.example.data.model.ChecklistItem
import com.example.data.model.DrugModel
import com.example.data.model.ProtocolModel
import com.example.data.model.QuizQuestion
import com.example.data.repository.MedicalKnowledgeRepository
import com.example.util.ClinicalCalculators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME, PATIENTS, PHARMACO, PROTOCOLS, AI
}

enum class SubScreen {
    NONE, CHECKLIST, BIOLOGY, CALCULATORS, QUIZ, CONSULTATION_WIZARD, PROTOCOL_DETAIL, DRUG_DETAIL
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val patientDao = db.patientDao()
    private val checklistDao = db.checklistDao()

    // Navigation state
    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _activeSubScreen = MutableStateFlow(SubScreen.NONE)
    val activeSubScreen: StateFlow<SubScreen> = _activeSubScreen.asStateFlow()

    // Universal Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Data from Room
    val savedAssessments: StateFlow<List<PatientAssessment>> = patientDao.getAllAssessments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedChecklists: StateFlow<List<ChecklistRecord>> = checklistDao.getAllChecklists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Medical Knowledge
    val drugsList: List<DrugModel> = MedicalKnowledgeRepository.drugs
    val protocolsList: List<ProtocolModel> = MedicalKnowledgeRepository.protocols
    val biologyList: List<BiologyTestModel> = MedicalKnowledgeRepository.biologyTests
    val quizQuestions: List<QuizQuestion> = MedicalKnowledgeRepository.quizQuestions

    // Selection states
    private val _selectedDrug = MutableStateFlow<DrugModel?>(null)
    val selectedDrug: StateFlow<DrugModel?> = _selectedDrug.asStateFlow()

    private val _selectedProtocol = MutableStateFlow<ProtocolModel?>(null)
    val selectedProtocol: StateFlow<ProtocolModel?> = _selectedProtocol.asStateFlow()

    private val _drugCategory = MutableStateFlow("TOUS")
    val drugCategory: StateFlow<String> = _drugCategory.asStateFlow()

    private val _protocolCategory = MutableStateFlow("TOUS")
    val protocolCategory: StateFlow<String> = _protocolCategory.asStateFlow()

    // Real-time dose calculation weight
    private val _calculatorWeight = MutableStateFlow(70.0)
    val calculatorWeight: StateFlow<Double> = _calculatorWeight.asStateFlow()

    // Equipment & WHO Checklists live states
    private val _equipmentChecklist = MutableStateFlow(MedicalKnowledgeRepository.checklistEquipmentItems.map { it.copy() })
    val equipmentChecklist: StateFlow<List<ChecklistItem>> = _equipmentChecklist.asStateFlow()

    private val _whoChecklist = MutableStateFlow(MedicalKnowledgeRepository.checklistOmsItems.map { it.copy() })
    val whoChecklist: StateFlow<List<ChecklistItem>> = _whoChecklist.asStateFlow()

    // Consultation Wizard State
    private val _wizardStep = MutableStateFlow(1)
    val wizardStep: StateFlow<Int> = _wizardStep.asStateFlow()

    // Wizard Form fields
    val wizPatientId = MutableStateFlow("PAT-${System.currentTimeMillis() % 10000}")
    val wizName = MutableStateFlow("")
    val wizAge = MutableStateFlow("45")
    val wizGender = MutableStateFlow("Homme")
    val wizWeight = MutableStateFlow("75")
    val wizHeight = MutableStateFlow("175")
    val wizSurgery = MutableStateFlow("Chirurgie digestive programmée")
    val wizSurgeryType = MutableStateFlow("Digestive")
    val wizUrgency = MutableStateFlow("Programmée")

    val wizMedicalHistory = MutableStateFlow("HTA sous IEC, pas de cardiopathie connue.")
    val wizSurgicalHistory = MutableStateFlow("Appendicectomie sous AG (sans incident).")
    val wizAnesthesiaHistory = MutableStateFlow("Aucun antécédent d'intubation difficile.")
    val wizAllergies = MutableStateFlow("Pas d'allergie médicamenteuse connue.")
    val wizTreatments = MutableStateFlow("Ramipril 5mg/j (arrêté la veille au soir).")
    val wizFastingStatus = MutableStateFlow("Jeun strict respecté (> 6h solides, > 2h liquides clairs)")

    val wizCvExam = MutableStateFlow("TA 130/80 mmHg, FC 72 bpm, bruits du coeur réguliers, pas de souffle.")
    val wizRespExam = MutableStateFlow("Murmure vésiculaire symétrique, pas de râles.")
    val wizMallampati = MutableStateFlow("Classe II")
    val wizMouthOpening = MutableStateFlow("Normale (> 35 mm)")
    val wizThyromental = MutableStateFlow("Normale (> 65 mm)")
    val wizCervicalMobility = MutableStateFlow("Conservée")
    val wizDentalStatus = MutableStateFlow("Bon état, pas de prothèse amovible")

    val wizAsaScore = MutableStateFlow("ASA II")
    val wizDifficultAirwayRisk = MutableStateFlow(false)
    val wizAspirationRisk = MutableStateFlow(false)
    val wizPonvScore = MutableStateFlow(2)
    val wizSummaryNotes = MutableStateFlow("Patient éligible à l'anesthésie générale avec intubation orotrachéale. Surveillance tensionnelle renforcée.")

    // AI Chat State
    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                isUser = false,
                text = "Bonjour 👋 Je suis ANESTHESIA AI, votre assistant intelligent en anesthésie-réanimation. Posez-moi une question clinique, demandez un protocole d'urgence, une posologie ou une explication biologique."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    // Quiz State
    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _selectedQuizOption = MutableStateFlow<Int?>(null)
    val selectedQuizOption: StateFlow<Int?> = _selectedQuizOption.asStateFlow()

    private val _isQuizSubmitted = MutableStateFlow(false)
    val isQuizSubmitted: StateFlow<Boolean> = _isQuizSubmitted.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    init {
        // Pre-populate with sample patient if DB is empty on first run
        viewModelScope.launch {
            if (patientDao.getAssessmentById(1L) == null) {
                patientDao.insertAssessment(
                    PatientAssessment(
                        patientId = "PAT-2026-089",
                        patientName = "Dupont Marc",
                        age = 58,
                        gender = "Homme",
                        weightKg = 78.0,
                        heightCm = 176.0,
                        bmi = 25.2,
                        surgery = "Cholécystectomie par cœlioscopie",
                        surgeryType = "Digestive",
                        urgency = "Programmée",
                        medicalHistory = "Hypertension artérielle modérée, reflux gastro-œsophagien occasionnel.",
                        surgicalHistory = "Méniscectomie droite en 2018 sous rachianesthésie.",
                        anesthesiaHistory = "Aucune complication anesthésique rapportée.",
                        allergies = "Pénicilline (urticaire dans l'enfance).",
                        currentTreatments = "Amlodipine 5mg matin.",
                        fastingStatus = "Jeûn strict > 8h.",
                        cvExam = "TA 135/85 mmHg, FC 68 bpm régulier.",
                        respExam = "Poumons libres et symétriques.",
                        mallampati = "Classe II",
                        mouthOpening = "Normale (> 35 mm)",
                        thyromentalDistance = "Normale (> 65 mm)",
                        cervicalMobility = "Conservée",
                        dentalStatus = "Dents saines.",
                        asaScore = "ASA II",
                        difficultAirwayRisk = false,
                        aspirationRisk = false,
                        ponvApfelScore = 2,
                        summaryNotes = "Anesthésie générale balancée, intubation orotrachéale. Prévention NVPO par Dexaméthasone 4mg + Ondansétron 4mg."
                    )
                )
            }
        }
    }

    // Navigation functions
    fun setTab(tab: MainTab) {
        _currentTab.value = tab
        _activeSubScreen.value = SubScreen.NONE
    }

    fun openSubScreen(screen: SubScreen) {
        _activeSubScreen.value = screen
    }

    fun closeSubScreen() {
        _activeSubScreen.value = SubScreen.NONE
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDrugCategory(category: String) {
        _drugCategory.value = category
    }

    fun selectDrug(drug: DrugModel?) {
        _selectedDrug.value = drug
        if (drug != null) {
            _activeSubScreen.value = SubScreen.DRUG_DETAIL
        }
    }

    fun setProtocolCategory(category: String) {
        _protocolCategory.value = category
    }

    fun selectProtocol(protocol: ProtocolModel?) {
        _selectedProtocol.value = protocol
        if (protocol != null) {
            _activeSubScreen.value = SubScreen.PROTOCOL_DETAIL
        }
    }

    fun setCalculatorWeight(weight: Double) {
        _calculatorWeight.value = weight.coerceIn(2.0, 300.0)
    }

    // Checklist toggles
    fun toggleEquipmentItem(id: String) {
        _equipmentChecklist.value = _equipmentChecklist.value.map { item ->
            if (item.id == id) {
                val nextStatus = when (item.status) {
                    "PENDING" -> "VERIFIED"
                    "VERIFIED" -> "ACTION_REQUIRED"
                    else -> "PENDING"
                }
                item.copy(status = nextStatus)
            } else item
        }
    }

    fun toggleWhoItem(id: String) {
        _whoChecklist.value = _whoChecklist.value.map { item ->
            if (item.id == id) {
                val nextStatus = when (item.status) {
                    "PENDING" -> "VERIFIED"
                    "VERIFIED" -> "ACTION_REQUIRED"
                    else -> "PENDING"
                }
                item.copy(status = nextStatus)
            } else item
        }
    }

    fun saveChecklistRecord(type: String, room: String, verifiedBy: String) {
        viewModelScope.launch {
            val list = if (type == "MATERIEL_BLOC") _equipmentChecklist.value else _whoChecklist.value
            val verified = list.count { it.status == "VERIFIED" }
            val total = list.size
            val isComplete = verified == total

            checklistDao.insertChecklist(
                ChecklistRecord(
                    checklistType = type,
                    roomName = room.ifBlank { "Salle 1 (Bloc Principal)" },
                    verifiedBy = verifiedBy.ifBlank { "IAR / MAR en charge" },
                    verifiedCount = verified,
                    totalCount = total,
                    isComplete = isComplete,
                    details = "Vérification de $verified / $total éléments."
                )
            )
        }
    }

    // Wizard navigation
    fun setWizardStep(step: Int) {
        _wizardStep.value = step.coerceIn(1, 5)
    }

    fun nextWizardStep() {
        if (_wizardStep.value < 5) {
            _wizardStep.value += 1
        }
    }

    fun prevWizardStep() {
        if (_wizardStep.value > 1) {
            _wizardStep.value -= 1
        }
    }

    fun saveAssessment(onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            val w = wizWeight.value.toDoubleOrNull() ?: 70.0
            val h = wizHeight.value.toDoubleOrNull() ?: 170.0
            val calculatedBmi = ClinicalCalculators.calculateBmi(w, h, wizGender.value == "Femme").bmi

            val assessment = PatientAssessment(
                patientId = wizPatientId.value.ifBlank { "PAT-${System.currentTimeMillis() % 10000}" },
                patientName = wizName.value.ifBlank { "Patient Anonyme" },
                age = wizAge.value.toIntOrNull() ?: 50,
                gender = wizGender.value,
                weightKg = w,
                heightCm = h,
                bmi = calculatedBmi,
                surgery = wizSurgery.value,
                surgeryType = wizSurgeryType.value,
                urgency = wizUrgency.value,
                medicalHistory = wizMedicalHistory.value,
                surgicalHistory = wizSurgicalHistory.value,
                anesthesiaHistory = wizAnesthesiaHistory.value,
                allergies = wizAllergies.value,
                currentTreatments = wizTreatments.value,
                fastingStatus = wizFastingStatus.value,
                cvExam = wizCvExam.value,
                respExam = wizRespExam.value,
                mallampati = wizMallampati.value,
                mouthOpening = wizMouthOpening.value,
                thyromentalDistance = wizThyromental.value,
                cervicalMobility = wizCervicalMobility.value,
                dentalStatus = wizDentalStatus.value,
                asaScore = wizAsaScore.value,
                difficultAirwayRisk = wizDifficultAirwayRisk.value,
                aspirationRisk = wizAspirationRisk.value,
                ponvApfelScore = wizPonvScore.value,
                summaryNotes = wizSummaryNotes.value
            )
            patientDao.insertAssessment(assessment)
            resetWizard()
            _activeSubScreen.value = SubScreen.NONE
            _currentTab.value = MainTab.PATIENTS
            onSaved()
        }
    }

    fun deleteAssessment(assessment: PatientAssessment) {
        viewModelScope.launch {
            patientDao.deleteAssessment(assessment)
        }
    }

    fun resetWizard() {
        _wizardStep.value = 1
        wizPatientId.value = "PAT-${System.currentTimeMillis() % 10000}"
        wizName.value = ""
        wizAge.value = "45"
        wizWeight.value = "75"
        wizHeight.value = "175"
        wizSurgery.value = "Intervention programmée"
        wizMedicalHistory.value = ""
        wizAllergies.value = "Aucune"
        wizTreatments.value = "Aucun"
        wizSummaryNotes.value = ""
    }

    // AI Chat
    fun sendAiPrompt(prompt: String) {
        if (prompt.isBlank() || _isAiGenerating.value) return
        val userMsg = ChatMessage(isUser = true, text = prompt)
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiGenerating.value = true

        viewModelScope.launch {
            val responseText = AiAssistantService.askAssistant(prompt)
            val aiMsg = ChatMessage(isUser = false, text = responseText)
            _chatMessages.value = _chatMessages.value + aiMsg
            _isAiGenerating.value = false
        }
    }

    // Quiz
    fun selectQuizAnswer(index: Int) {
        if (!_isQuizSubmitted.value) {
            _selectedQuizOption.value = index
        }
    }

    fun submitQuizAnswer() {
        if (_selectedQuizOption.value == null || _isQuizSubmitted.value) return
        val currentQ = quizQuestions[_currentQuizIndex.value]
        if (_selectedQuizOption.value == currentQ.correctIndex) {
            _quizScore.value += 1
        }
        _isQuizSubmitted.value = true
    }

    fun nextQuizQuestion() {
        if (_currentQuizIndex.value < quizQuestions.size - 1) {
            _currentQuizIndex.value += 1
            _selectedQuizOption.value = null
            _isQuizSubmitted.value = false
        }
    }

    fun resetQuiz() {
        _currentQuizIndex.value = 0
        _selectedQuizOption.value = null
        _isQuizSubmitted.value = false
        _quizScore.value = 0
    }
}

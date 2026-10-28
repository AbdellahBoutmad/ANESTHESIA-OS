package com.example.data.escaperoom

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class EscapeRoomEngine(
    private val scope: CoroutineScope
) {

    private val _activeScenario = MutableStateFlow<EscapeRoomScenario?>(null)
    val activeScenario: StateFlow<EscapeRoomScenario?> = _activeScenario.asStateFlow()

    private val _currentPhase = MutableStateFlow<ScenarioPhase?>(null)
    val currentPhase: StateFlow<ScenarioPhase?> = _currentPhase.asStateFlow()

    private val _currentVitals = MutableStateFlow<SimulatedVitals?>(null)
    val currentVitals: StateFlow<SimulatedVitals?> = _currentVitals.asStateFlow()

    private val _timeRemainingSeconds = MutableStateFlow(60)
    val timeRemainingSeconds: StateFlow<Int> = _timeRemainingSeconds.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _currentScore = MutableStateFlow(60)
    val currentScore: StateFlow<Int> = _currentScore.asStateFlow()

    private val _actionHistory = MutableStateFlow<List<ActionRecord>>(emptyList())
    val actionHistory: StateFlow<List<ActionRecord>> = _actionHistory.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    private val _lastActionFeedback = MutableStateFlow<Pair<String, Boolean>?>(null)
    val lastActionFeedback: StateFlow<Pair<String, Boolean>?> = _lastActionFeedback.asStateFlow()

    private val _simulationResult = MutableStateFlow<SimulationResult?>(null)
    val simulationResult: StateFlow<SimulationResult?> = _simulationResult.asStateFlow()

    private var timerJob: Job? = null

    fun startScenario(scenario: EscapeRoomScenario) {
        timerJob?.cancel()
        _activeScenario.value = scenario
        val startPhase = scenario.phases[scenario.startPhaseId]
            ?: scenario.phases.values.firstOrNull()

        _currentPhase.value = startPhase
        _currentVitals.value = startPhase?.vitals ?: scenario.initialVitals
        _timeRemainingSeconds.value = startPhase?.timeLimitSeconds ?: 60
        _elapsedSeconds.value = 0
        _currentScore.value = 60
        _actionHistory.value = emptyList()
        _isPaused.value = false
        _isFinished.value = false
        _lastActionFeedback.value = null
        _simulationResult.value = null

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch(Dispatchers.Default) {
            while (isActive && !_isFinished.value) {
                delay(1000L)
                if (!_isPaused.value && !_isFinished.value) {
                    _elapsedSeconds.value += 1
                    if (_timeRemainingSeconds.value > 0) {
                        _timeRemainingSeconds.value -= 1
                    } else {
                        // Phase timeout triggered
                        handlePhaseTimeout()
                    }
                }
            }
        }
    }

    private fun handlePhaseTimeout() {
        val scenario = _activeScenario.value ?: return
        val phase = _currentPhase.value ?: return

        val timeoutTargetId = phase.timeoutPhaseId
        if (timeoutTargetId != null && scenario.phases.containsKey(timeoutTargetId)) {
            transitionToPhase(timeoutTargetId, reason = "Délai expiré !")
        } else {
            // General failure if timeout has no specific next step
            finishSimulation(SessionOutcome.TIME_EXPIRED)
        }
    }

    fun performAction(action: ClinicalAction) {
        if (_isFinished.value || _isPaused.value) return
        val scenario = _activeScenario.value ?: return
        val phase = _currentPhase.value ?: return

        // 1. Advance elapsed time by action cost
        _elapsedSeconds.value += action.timeCostSeconds
        _timeRemainingSeconds.value = (_timeRemainingSeconds.value - action.timeCostSeconds).coerceAtLeast(0)

        // 2. Adjust score
        val newScore = (_currentScore.value + action.scoreDelta).coerceIn(0, 100)
        _currentScore.value = newScore

        // 3. Mutate vitals
        val currentV = _currentVitals.value ?: phase.vitals
        val mutatedVitals = action.vitalsModifier(currentV)
        _currentVitals.value = mutatedVitals

        // 4. Record action in Black Box
        val record = ActionRecord(
            timestampSeconds = _elapsedSeconds.value,
            actionId = action.id,
            actionTitle = action.title,
            category = action.category,
            scoreDelta = action.scoreDelta,
            vitalsSnapshot = mutatedVitals,
            wasHarmful = action.isHarmful,
            wasCritical = action.isCriticalCorrect,
            rationale = action.rationale,
            consequenceText = action.consequenceText
        )
        _actionHistory.value = _actionHistory.value + record

        // 5. Provide immediate feedback prompt
        _lastActionFeedback.value = action.consequenceText to !action.isHarmful

        // 6. Check branching
        val nextPhaseId = action.nextPhaseId
        if (nextPhaseId != null && scenario.phases.containsKey(nextPhaseId)) {
            transitionToPhase(nextPhaseId, reason = action.consequenceText)
        }
    }

    private fun transitionToPhase(targetPhaseId: String, reason: String) {
        val scenario = _activeScenario.value ?: return
        val targetPhase = scenario.phases[targetPhaseId] ?: return

        _currentPhase.value = targetPhase
        _currentVitals.value = targetPhase.vitals
        _timeRemainingSeconds.value = targetPhase.timeLimitSeconds

        // Check if target phase is an end-game phase
        if (scenario.successPhaseIds.contains(targetPhaseId)) {
            val outcome = if (_currentScore.value >= 75) {
                SessionOutcome.SURVIVED_EXCELLENT
            } else {
                SessionOutcome.SURVIVED_COMPLICATED
            }
            finishSimulation(outcome)
        } else if (scenario.failurePhaseIds.contains(targetPhaseId)) {
            finishSimulation(SessionOutcome.CARDIAC_ARREST_FAILURE)
        }
    }

    private fun finishSimulation(outcome: SessionOutcome) {
        _isFinished.value = true
        timerJob?.cancel()

        val scenario = _activeScenario.value ?: return
        val history = _actionHistory.value

        val criticalsCount = history.count { it.wasCritical }
        val harmfulCount = history.count { it.wasHarmful }

        val result = SimulationResult(
            scenarioId = scenario.id,
            scenarioTitle = scenario.title,
            totalTimeSeconds = _elapsedSeconds.value,
            finalScore = _currentScore.value,
            outcome = outcome,
            actionHistory = history,
            missedObservations = if (outcome.isSuccess) emptyList() else listOf("Manque d'anticipation de la détérioration ventilatoire ou hémodynamique"),
            incorrectDecisionsCount = harmfulCount,
            criticalDecisionsAchieved = criticalsCount,
            totalCriticalDecisions = 3,
            aiPersonalizedDebriefing = null
        )

        _simulationResult.value = result
    }

    fun setAiDebriefing(text: String) {
        _simulationResult.value = _simulationResult.value?.copy(aiPersonalizedDebriefing = text)
    }

    fun togglePause() {
        _isPaused.value = !_isPaused.value
    }

    fun restart() {
        val scenario = _activeScenario.value ?: return
        startScenario(scenario)
    }

    fun exit() {
        timerJob?.cancel()
        _activeScenario.value = null
        _currentPhase.value = null
        _isFinished.value = false
        _isPaused.value = false
        _simulationResult.value = null
    }
}

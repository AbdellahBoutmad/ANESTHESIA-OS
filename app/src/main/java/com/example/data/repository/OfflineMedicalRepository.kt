package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.DrugEntity
import com.example.data.local.ProtocolEntity
import com.example.data.model.DrugModel
import com.example.data.model.ProtocolModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OfflineMedicalRepository(private val database: AppDatabase) {

    private val drugDao = database.drugDao()
    private val protocolDao = database.protocolDao()

    suspend fun initializeIfNeeded() = withContext(Dispatchers.IO) {
        val drugCount = drugDao.getCount()
        if (drugCount == 0) {
            val drugEntities = MedicalKnowledgeRepository.drugs.map { it.toEntity() }
            drugDao.insertAll(drugEntities)
        }

        val protocolCount = protocolDao.getCount()
        if (protocolCount == 0) {
            val protocolEntities = MedicalKnowledgeRepository.protocols.map { it.toEntity() }
            protocolDao.insertAll(protocolEntities)
        }
    }

    fun getDrugs(query: String, category: String): Flow<List<DrugModel>> {
        val flow = when {
            query.isNotBlank() -> drugDao.searchDrugs(query.trim())
            category != "TOUS" -> drugDao.getDrugsByCategory(category)
            else -> drugDao.getAllDrugs()
        }
        return flow.map { entities ->
            if (entities.isEmpty() && query.isBlank() && category == "TOUS") {
                MedicalKnowledgeRepository.drugs
            } else {
                entities.map { it.toModel() }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getProtocols(query: String, category: String): Flow<List<ProtocolModel>> {
        val flow = when {
            query.isNotBlank() -> protocolDao.searchProtocols(query.trim())
            category != "TOUS" -> protocolDao.getProtocolsByCategory(category)
            else -> protocolDao.getAllProtocols()
        }
        return flow.map { entities ->
            if (entities.isEmpty() && query.isBlank() && category == "TOUS") {
                MedicalKnowledgeRepository.protocols
            } else {
                entities.map { it.toModel() }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun toggleDrugFavorite(drugId: String, currentFav: Boolean) = withContext(Dispatchers.IO) {
        drugDao.updateFavorite(drugId, !currentFav)
    }

    suspend fun toggleProtocolFavorite(protocolId: String, currentFav: Boolean) = withContext(Dispatchers.IO) {
        protocolDao.updateFavorite(protocolId, !currentFav)
    }
}

// Extension mappers
fun DrugModel.toEntity(isFav: Boolean = false): DrugEntity {
    return DrugEntity(
        id = this.id,
        name = this.name,
        brandNames = this.brandNames,
        category = this.category,
        mechanism = this.mechanism,
        indications = this.indications,
        presentations = this.presentations,
        minDosePerKg = this.minDosePerKg,
        maxDosePerKg = this.maxDosePerKg,
        doseUnit = this.doseUnit,
        fixedDoseText = this.fixedDoseText,
        maintenanceDose = this.maintenanceDose,
        routes = this.routes,
        onset = this.onset,
        duration = this.duration,
        contraindicationsText = this.contraindications.joinToString("|"),
        adverseEffectsText = this.adverseEffects.joinToString("|"),
        terrainCautions = this.terrainCautions,
        monitoringPointsText = this.monitoringPoints.joinToString("|"),
        antidoteOrAntagonist = this.antidoteOrAntagonist,
        sources = this.sources,
        isFavorite = isFav
    )
}

fun DrugEntity.toModel(): DrugModel {
    return DrugModel(
        id = this.id,
        name = this.name,
        brandNames = this.brandNames,
        category = this.category,
        mechanism = this.mechanism,
        indications = this.indications,
        presentations = this.presentations,
        minDosePerKg = this.minDosePerKg,
        maxDosePerKg = this.maxDosePerKg,
        doseUnit = this.doseUnit,
        fixedDoseText = this.fixedDoseText,
        maintenanceDose = this.maintenanceDose,
        routes = this.routes,
        onset = this.onset,
        duration = this.duration,
        contraindications = if (this.contraindicationsText.isBlank()) emptyList() else this.contraindicationsText.split("|"),
        adverseEffects = if (this.adverseEffectsText.isBlank()) emptyList() else this.adverseEffectsText.split("|"),
        terrainCautions = this.terrainCautions,
        monitoringPoints = if (this.monitoringPointsText.isBlank()) emptyList() else this.monitoringPointsText.split("|"),
        antidoteOrAntagonist = this.antidoteOrAntagonist,
        sources = this.sources
    )
}

fun ProtocolModel.toEntity(isFav: Boolean = false): ProtocolEntity {
    return ProtocolEntity(
        id = this.id,
        title = this.title,
        category = this.category,
        urgencyLevel = this.urgencyLevel,
        subtitle = this.subtitle,
        description = this.description,
        algorithmStepsText = this.algorithmSteps.joinToString("|"),
        keyDrugsText = this.keyDrugs.joinToString("|"),
        specificRisksText = this.specificRisks.joinToString("|"),
        postOpMonitoringText = this.postOpMonitoring.joinToString("|"),
        guidelinesSource = this.guidelinesSource,
        isFavorite = isFav
    )
}

fun ProtocolEntity.toModel(): ProtocolModel {
    return ProtocolModel(
        id = this.id,
        title = this.title,
        category = this.category,
        urgencyLevel = this.urgencyLevel,
        subtitle = this.subtitle,
        description = this.description,
        algorithmSteps = if (this.algorithmStepsText.isBlank()) emptyList() else this.algorithmStepsText.split("|"),
        keyDrugs = if (this.keyDrugsText.isBlank()) emptyList() else this.keyDrugsText.split("|"),
        specificRisks = if (this.specificRisksText.isBlank()) emptyList() else this.specificRisksText.split("|"),
        postOpMonitoring = if (this.postOpMonitoringText.isBlank()) emptyList() else this.postOpMonitoringText.split("|"),
        guidelinesSource = this.guidelinesSource
    )
}

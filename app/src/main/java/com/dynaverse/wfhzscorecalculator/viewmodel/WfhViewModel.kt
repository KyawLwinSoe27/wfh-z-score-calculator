package com.dynaverse.wfhzscorecalculator.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.dynaverse.wfhzscorecalculator.data.WhoTableRepository
import com.dynaverse.wfhzscorecalculator.model.Gender
import com.dynaverse.wfhzscorecalculator.model.NutritionalStatus
import com.dynaverse.wfhzscorecalculator.model.WfhLookup
import com.dynaverse.wfhzscorecalculator.model.WfhRecord
import com.dynaverse.wfhzscorecalculator.model.WfhResult
import com.dynaverse.wfhzscorecalculator.model.WfhUiState
import com.dynaverse.wfhzscorecalculator.model.estimateZScore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

class WfhViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(WfhUiState())
    val uiState: StateFlow<WfhUiState> = _uiState.asStateFlow()

    private val prefs = application.getSharedPreferences("wfh_records", Context.MODE_PRIVATE)

    init {
        loadRecords()
    }

    fun onGenderSelected(isBoy: Boolean) {
        _uiState.update { it.copy(isBoy = isBoy) }
    }

    fun onChildNameEdited(name: String) {
        _uiState.update { it.copy(childName = name) }
    }

    fun onAgeIncrement() {
        _uiState.update { it.copy(ageMonths = (it.ageMonths + 1).coerceIn(0, 60)) }
    }

    fun onAgeDecrement() {
        _uiState.update { it.copy(ageMonths = (it.ageMonths - 1).coerceIn(0, 60)) }
    }

    fun onAgeEdited(raw: String) {
        val value = raw.trim().toDoubleOrNull()?.roundToInt() ?: return
        _uiState.update { it.copy(ageMonths = value.coerceIn(0, 60)) }
    }

    fun onHeightIncrement() {
        _uiState.update { it.copy(heightCm = (it.heightCm + 0.5).coerceIn(1.0, 250.0)) }
    }

    fun onHeightDecrement() {
        _uiState.update { it.copy(heightCm = (it.heightCm - 0.5).coerceIn(1.0, 250.0)) }
    }

    fun onWeightIncrement() {
        _uiState.update { it.copy(weightKg = (it.weightKg + 0.1).coerceIn(0.0, 80.0)) }
    }

    fun onWeightDecrement() {
        _uiState.update { it.copy(weightKg = (it.weightKg - 0.1).coerceIn(0.0, 80.0)) }
    }

    fun onHeightEdited(raw: String) {
        val value = raw.trim().toDoubleOrNull() ?: return
        _uiState.update { it.copy(heightCm = value.coerceIn(1.0, 250.0)) }
    }

    fun onWeightEdited(raw: String) {
        val value = raw.trim().toDoubleOrNull() ?: return
        _uiState.update { it.copy(weightKg = value.coerceIn(0.0, 80.0)) }
    }

    fun onErrorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onCalculate() {
        val state = _uiState.value
        val roundedHeight = state.heightCm.roundToInt()
        val lookup = WhoTableRepository.getReferenceRow(
            context = getApplication(),
            ageMonths = state.ageMonths,
            isBoy = state.isBoy,
            heightCm = roundedHeight
        )
        when (lookup) {
            is WfhLookup.Found -> {
                val status = NutritionalStatus.classify(state.weightKg, lookup.row)
                val zScore = estimateZScore(state.weightKg, lookup.row)
                val result = WfhResult(
                    childName = state.childName.trim(),
                    gender = if (state.isBoy) Gender.BOY else Gender.GIRL,
                    ageMonths = state.ageMonths,
                    heightCm = state.heightCm,
                    weightKg = state.weightKg,
                    referenceRow = lookup.row,
                    status = status,
                    zScore = zScore
                )
                _uiState.update {
                    it.copy(
                        result = result,
                        showResultSheet = true,
                        isViewingHistory = false,
                        errorMessage = null
                    )
                }
            }
            WfhLookup.OlderThanFive -> _uiState.update {
                it.copy(
                    errorMessage = "BMI-for-Age is required for children older than 5 years. " +
                        "WFH Z-Score applies only up to 59 months."
                )
            }
            WfhLookup.OutOfRange -> _uiState.update {
                it.copy(
                    errorMessage = "Height ${roundedHeight} cm is outside the WHO table range " +
                        "for this age group. Please check the measurement."
                )
            }
        }
    }

    fun onDismissResult() {
        _uiState.update { it.copy(showResultSheet = false) }
    }

    fun onRecordSelected(record: WfhRecord) {
        val lookup = WhoTableRepository.getReferenceRow(
            context = getApplication(),
            ageMonths = record.ageMonths,
            isBoy = record.gender == Gender.BOY,
            heightCm = record.heightCm.roundToInt()
        )
        val row = (lookup as? WfhLookup.Found)?.row
        if (row == null) {
            _uiState.update {
                it.copy(errorMessage = "This record references a height outside the WHO table range.")
            }
            return
        }
        val result = WfhResult(
            childName = record.childName,
            gender = record.gender,
            ageMonths = record.ageMonths,
            heightCm = record.heightCm,
            weightKg = record.weightKg,
            referenceRow = row,
            status = record.status,
            zScore = record.zScore
        )
        _uiState.update {
            it.copy(
                result = result,
                showResultSheet = true,
                isViewingHistory = true,
                errorMessage = null
            )
        }
    }

    fun onSaveResult() {
        val state = _uiState.value
        val result = state.result ?: return
        val record = WfhRecord(
            id = System.currentTimeMillis(),
            timestamp = System.currentTimeMillis(),
            childName = result.childName,
            gender = result.gender,
            ageMonths = result.ageMonths,
            heightCm = result.heightCm,
            weightKg = result.weightKg,
            status = result.status,
            zScore = result.zScore
        )
        _uiState.update {
            it.copy(
                records = (listOf(record) + it.records).take(100),
                showResultSheet = false,
                confirmationId = it.confirmationId + 1
            )
        }
        persistRecords()
    }

    private fun persistRecords() {
        val array = JSONArray()
        _uiState.value.records.forEach { record ->
            array.put(
                JSONObject()
                    .put("id", record.id)
                    .put("timestamp", record.timestamp)
                    .put("childName", record.childName)
                    .put("gender", record.gender.name)
                    .put("ageMonths", record.ageMonths)
                    .put("heightCm", record.heightCm)
                    .put("weightKg", record.weightKg)
                    .put("status", record.status.name)
                    .put("zScore", record.zScore)
            )
        }
        prefs.edit().putString(KEY_RECORDS, array.toString()).apply()
    }

    private fun loadRecords() {
        val raw = prefs.getString(KEY_RECORDS, null) ?: return
        runCatching {
            val array = JSONArray(raw)
            val records = buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val gender = runCatching { Gender.valueOf(obj.getString("gender")) }
                        .getOrDefault(Gender.BOY)
                    val status = runCatching { NutritionalStatus.valueOf(obj.getString("status")) }
                        .getOrDefault(NutritionalStatus.NORMAL)
                    add(
                        WfhRecord(
                            id = obj.getLong("id"),
                            timestamp = obj.getLong("timestamp"),
                            childName = obj.optString("childName"),
                            gender = gender,
                            ageMonths = obj.getInt("ageMonths"),
                            heightCm = obj.getDouble("heightCm"),
                            weightKg = obj.getDouble("weightKg"),
                            status = status,
                            zScore = obj.optDouble("zScore", 0.0)
                        )
                    )
                }
            }
            _uiState.update { it.copy(records = records.sortedByDescending(WfhRecord::timestamp)) }
        }.onFailure {
            prefs.edit().remove(KEY_RECORDS).apply()
        }
    }

    private companion object {
        const val KEY_RECORDS = "records_json"
    }
}

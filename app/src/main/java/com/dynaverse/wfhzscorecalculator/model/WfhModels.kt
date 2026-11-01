package com.dynaverse.wfhzscorecalculator.model

data class WfhRow(
    val lengthHeightCm: Int,
    val sdMinus3: Double,
    val sdMinus2: Double,
    val sdMinus1: Double,
    val median: Double
)

enum class Gender(val label: String) {
    BOY("Boy"),
    GIRL("Girl")
}

enum class NutritionalStatus(val label: String, val colorArgb: Long) {
    SAM("Severe Acute Malnutrition", 0xFFE53935),
    MAM("Moderate Acute Malnutrition", 0xFFFB8C00),
    AT_RISK("At Risk / Mild Malnutrition", 0xFFFDD835),
    NORMAL("Normal / Well-Nourished", 0xFF2E7D32),
    ABOVE_MEDIAN("Above Median Weight", 0xFF1976D2);

    companion object {
        fun classify(weight: Double, row: WfhRow): NutritionalStatus = when {
            weight < row.sdMinus3 -> SAM
            weight < row.sdMinus2 -> MAM
            weight < row.sdMinus1 -> AT_RISK
            weight <= row.median -> NORMAL
            else -> ABOVE_MEDIAN
        }
    }
}

fun estimateZScore(weight: Double, row: WfhRow): Double {
    if (weight == row.median) return 0.0
    if (weight < row.median) {
        val anchors = listOf(
            -3.0 to row.sdMinus3,
            -2.0 to row.sdMinus2,
            -1.0 to row.sdMinus1,
            0.0 to row.median
        )
        return interpolateDown(weight, anchors)
    }
    val halfWidth = row.median - row.sdMinus1
    val over = weight - row.median
    return over / halfWidth.coerceAtLeast(0.1)
}

private fun interpolateDown(weight: Double, anchors: List<Pair<Double, Double>>): Double {
    val sorted = anchors.sortedByDescending { it.second }
    for (i in 0 until sorted.size - 1) {
        val (zHigh, wHigh) = sorted[i]
        val (zLow, wLow) = sorted[i + 1]
        if (weight > wLow) {
            val t = (weight - wLow) / (wHigh - wLow)
            return zLow + t * (zHigh - zLow)
        }
    }
    val (lowestZ, lowestW) = sorted.last()
    val (secondLowestZ, secondLowestW) = sorted[sorted.size - 2]
    val slope = (lowestZ - secondLowestZ) / (lowestW - secondLowestW)
    return lowestZ + (weight - lowestW) * slope
}

fun clampSpectrumZ(z: Double): Double = z.coerceIn(-4.0, 3.0)

fun spectrumFraction(z: Double): Float = ((clampSpectrumZ(z) + 4.0) / 7.0).toFloat()

data class WfhResult(
    val childName: String,
    val gender: Gender,
    val ageMonths: Int,
    val heightCm: Double,
    val weightKg: Double,
    val referenceRow: WfhRow,
    val status: NutritionalStatus,
    val zScore: Double
)

data class WfhRecord(
    val id: Long,
    val timestamp: Long,
    val childName: String,
    val gender: Gender,
    val ageMonths: Int,
    val heightCm: Double,
    val weightKg: Double,
    val status: NutritionalStatus,
    val zScore: Double
)

data class WfhUiState(
    val childName: String = "",
    val isBoy: Boolean = true,
    val ageMonths: Int = 18,
    val heightCm: Double = 82.5,
    val weightKg: Double = 11.4,
    val records: List<WfhRecord> = emptyList(),
    val result: WfhResult? = null,
    val showResultSheet: Boolean = false,
    val isViewingHistory: Boolean = false,
    val errorMessage: String? = null,
    val confirmationId: Long = 0
)

sealed interface WfhLookup {
    data class Found(val row: WfhRow) : WfhLookup
    data object OlderThanFive : WfhLookup
    data object OutOfRange : WfhLookup
}

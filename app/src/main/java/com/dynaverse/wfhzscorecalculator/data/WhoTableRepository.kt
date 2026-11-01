package com.dynaverse.wfhzscorecalculator.data

import android.content.Context
import com.dynaverse.wfhzscorecalculator.model.WfhLookup
import com.dynaverse.wfhzscorecalculator.model.WfhRow
import org.json.JSONObject

object WhoTableRepository {

    @Volatile
    private var loaded = false
    private val lock = Any()

    private var toddlerTable: Map<Boolean, Map<Int, WfhRow>> = emptyMap()
    private var childTable: Map<Boolean, Map<Int, WfhRow>> = emptyMap()

    fun getReferenceRow(
        context: Context,
        ageMonths: Int,
        isBoy: Boolean,
        heightCm: Int
    ): WfhLookup {
        if (!loaded) ensureLoaded(context)
        if (ageMonths > 59) return WfhLookup.OlderThanFive

        val table = if (ageMonths < 24) toddlerTable else childTable
        val row = (table[isBoy] ?: emptyMap())[heightCm] ?: return WfhLookup.OutOfRange
        return WfhLookup.Found(row)
    }

    private fun ensureLoaded(context: Context) {
        synchronized(lock) {
            if (loaded) return
            val root = context.assets.open("who_wfh_tables.json")
                .bufferedReader()
                .use { it.readText() }
                .let { JSONObject(it) }

            toddlerTable = parseBucket(root.getJSONObject("under_24_months"))
            childTable = parseBucket(root.getJSONObject("24_to_59_months"))
            loaded = true
        }
    }

    private fun parseBucket(bucket: JSONObject): Map<Boolean, Map<Int, WfhRow>> =
        mapOf(
            true to parseGender(bucket.getJSONObject("boys")),
            false to parseGender(bucket.getJSONObject("girls"))
        )

    private fun parseGender(genderObject: JSONObject): Map<Int, WfhRow> {
        val rows = mutableMapOf<Int, WfhRow>()
        val keys = genderObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val cm = key.toIntOrNull() ?: continue
            val row = genderObject.getJSONObject(key)
            rows[cm] = WfhRow(
                lengthHeightCm = cm,
                sdMinus3 = row.getDouble("sd_minus_3"),
                sdMinus2 = row.getDouble("sd_minus_2"),
                sdMinus1 = row.getDouble("sd_minus_1"),
                median = row.getDouble("median")
            )
        }
        return rows
    }
}

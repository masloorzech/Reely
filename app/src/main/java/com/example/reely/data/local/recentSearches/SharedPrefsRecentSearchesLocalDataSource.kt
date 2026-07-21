package com.example.reely.data.local.recentSearches

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.reely.data.local.RecentSearchesLocalDataSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.json.JSONArray
import javax.inject.Inject

class SharedPrefsRecentSearchesLocalDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : RecentSearchesLocalDataSource {

    private companion object {
        const val KEY_RECENT_SEARCHES = "recent_searches"
        const val MAX_SIZE = 6
    }

    override fun observeRecentSearches(): Flow<List<String>> =
        callbackFlow {
            fun emitCurrentValues() {
                trySend(readStoredValues().reversed())
            }

            val listener =
                SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key == KEY_RECENT_SEARCHES || key == null) {
                        emitCurrentValues()
                    }
                }

            emitCurrentValues()

            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

            awaitClose {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(
                    listener
                )
            }
        }.distinctUntilChanged()

    override suspend fun add(query: String) {
        val normalizedQuery = query.trim()

        if (normalizedQuery.isEmpty()) return

        val searches = readStoredValues().toMutableList()

        searches.remove(normalizedQuery)
        searches.add(normalizedQuery)

        save(searches.takeLast(MAX_SIZE))
    }

    override suspend fun clear() {
        sharedPreferences.edit {
            remove(KEY_RECENT_SEARCHES)
        }
    }

    private fun readStoredValues(): List<String> {
        val json = sharedPreferences.getString(
            KEY_RECENT_SEARCHES,
            null
        ) ?: return emptyList()

        return runCatching {
            val jsonArray = JSONArray(json)

            List(jsonArray.length()) { index ->
                jsonArray.getString(index)
            }
        }.getOrDefault(emptyList())
    }

    private fun save(values: List<String>) {
        val jsonArray = JSONArray()

        values.forEach(jsonArray::put)

        sharedPreferences.edit {
            putString(KEY_RECENT_SEARCHES, jsonArray.toString())
        }
    }
}
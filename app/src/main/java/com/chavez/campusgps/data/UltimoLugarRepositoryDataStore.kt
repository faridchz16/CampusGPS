package com.chavez.campusgps.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ultimo_lugar_prefs")
class UltimoLugarRepositoryDataStore(private val context: Context) : UltimoLugarRepository {

    private object Claves {
        val ULTIMO_LUGAR_ID = intPreferencesKey("ultimo_lugar_id")
    }

    override val ultimoLugarId: Flow<Int?> = context.dataStore.data
        .catch { error ->
            if (error is IOException) {
                Log.e("UltimoLugarRepo", "No se pudo leer el último lugar", error)
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferencias -> preferencias[Claves.ULTIMO_LUGAR_ID] }

    override suspend fun guardarUltimoLugarId(id: Int) {
        try {
            context.dataStore.edit { preferencias ->
                preferencias[Claves.ULTIMO_LUGAR_ID] = id
            }
        } catch (error: IOException) {
            Log.e("UltimoLugarRepo", "No se pudo guardar el último lugar", error)
        }
    }
}
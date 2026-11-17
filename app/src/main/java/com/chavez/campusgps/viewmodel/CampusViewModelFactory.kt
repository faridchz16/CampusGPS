package com.chavez.campusgps.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chavez.campusgps.data.LugaresRepositoryLocal
import com.chavez.campusgps.data.UbicacionRepositoryGoogle
import com.chavez.campusgps.data.UltimoLugarRepositoryDataStore

class CampusViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapaViewModel::class.java)) {
            val appContext = context.applicationContext
            return MapaViewModel(
                ubicacionRepo = UbicacionRepositoryGoogle(appContext),
                lugaresRepo = LugaresRepositoryLocal(),
                ultimoLugarRepo = UltimoLugarRepositoryDataStore(appContext)
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
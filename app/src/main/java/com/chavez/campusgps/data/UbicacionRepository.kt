package com.chavez.campusgps.data

import com.chavez.campusgps.model.Posicion
import kotlinx.coroutines.flow.Flow

interface UbicacionRepository {
    suspend fun obtenerPosicion(): Result<Posicion>

    fun obtenerUbicacionContinua(): Flow<Posicion>

    fun playServicesDisponible(): Boolean
}

class UbicacionNoDisponibleException :
    Exception("No se pudo obtener la ubicación. Activa el GPS e intenta de nuevo")
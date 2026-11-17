package com.chavez.campusgps.data

import com.chavez.campusgps.model.Posicion

interface UbicacionRepository {
    suspend fun obtenerPosicion(): Result<Posicion>

    fun playServicesDisponible(): Boolean
}

class UbicacionNoDisponibleException :
    Exception("No se pudo obtener la ubicación. Activa el GPS e intenta de nuevo")
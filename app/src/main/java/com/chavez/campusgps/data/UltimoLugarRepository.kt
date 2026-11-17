package com.chavez.campusgps.data

import kotlinx.coroutines.flow.Flow

interface UltimoLugarRepository {
    val ultimoLugarId: Flow<Int?>
    suspend fun guardarUltimoLugarId(id: Int)
}
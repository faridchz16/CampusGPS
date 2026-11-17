package com.chavez.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import com.chavez.campusgps.model.Posicion
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class UbicacionRepositoryGoogle(private val context: Context) : UbicacionRepository {

    private val cliente = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> = runCatching {
        val location = cliente.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).await()
            ?: throw UbicacionNoDisponibleException()

        Posicion(location.latitude, location.longitude)
    }

    override fun playServicesDisponible(): Boolean =
        GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
}
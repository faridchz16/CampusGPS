package com.chavez.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.chavez.campusgps.model.Posicion
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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


    @SuppressLint("MissingPermission")
    override fun obtenerUbicacionContinua(): Flow<Posicion> = callbackFlow {
        val peticion = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(resultado: LocationResult) {
                resultado.lastLocation?.let { ubicacion ->
                    trySend(Posicion(ubicacion.latitude, ubicacion.longitude))
                }
            }

            override fun onLocationAvailability(disponibilidad: LocationAvailability) {
                if (!disponibilidad.isLocationAvailable) {
                    close(UbicacionNoDisponibleException())
                }
            }
        }

        cliente.requestLocationUpdates(peticion, callback, Looper.getMainLooper())
            .addOnFailureListener { error -> close(error) } // si Google rechaza la petición


        awaitClose {
            cliente.removeLocationUpdates(callback)
        }
    }

    override fun playServicesDisponible(): Boolean =
        GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
}
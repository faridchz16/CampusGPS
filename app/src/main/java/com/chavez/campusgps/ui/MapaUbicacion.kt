package com.chavez.campusgps.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.chavez.campusgps.R
import com.chavez.campusgps.model.Lugar
import com.chavez.campusgps.model.LugarConDistancia
import com.chavez.campusgps.model.UbicacionUi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

@SuppressLint("MissingPermission")
@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    masCercano: Lugar?,
    radio: Int,
    tienePermisoUbicacion: Boolean,
    modifier: Modifier = Modifier
) {
    val contexto = LocalContext.current
    val esOscuro = isSystemInDarkTheme()

    val colorRadio = MaterialTheme.colorScheme.primary

    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-12.0464, -77.0428), 12f)
    }

    val estiloMapa = remember(esOscuro) {
        if (esOscuro) {
            runCatching {
                MapStyleOptions.loadRawResourceStyle(contexto, R.raw.mapa_oscuro)
            }.getOrElse { error ->
                Log.e("MapaUbicacion", "No se pudo cargar el estilo oscuro del mapa", error)
                null
            }
        } else {
            null
        }
    }

    val propiedades = remember(tienePermisoUbicacion, estiloMapa) {
        MapProperties(
            isMyLocationEnabled = tienePermisoUbicacion,
            mapStyleOptions = estiloMapa
        )
    }

    val configuracionUi = remember(tienePermisoUbicacion) {
        MapUiSettings(myLocationButtonEnabled = tienePermisoUbicacion)
    }

    LaunchedEffect(estado) {
        if (estado is UbicacionUi.Lista) {
            val destino = LatLng(estado.posicion.latitud, estado.posicion.longitud)
            camara.animate(CameraUpdateFactory.newLatLngZoom(destino, 16f))
        }
    }

    LaunchedEffect(seleccionado) {
        val lugar = lugares.firstOrNull { it.lugar.id == seleccionado }?.lugar
        if (lugar != null) {
            camara.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(lugar.latitud, lugar.longitud), 17f)
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = camara,
        properties = propiedades,
        uiSettings = configuracionUi
    ) {
        if (estado is UbicacionUi.Lista) {
            val centro = LatLng(estado.posicion.latitud, estado.posicion.longitud)

            Circle(
                center = centro,
                radius = radio.toDouble(),
                strokeColor = colorRadio,
                strokeWidth = 3f,
                fillColor = colorRadio.copy(alpha = 0.2f)
            )

            Marker(
                state = rememberUpdatedMarkerState(position = centro),
                title = "Estás aquí"
            )
        }

        lugares.forEach { item ->
            key(item.lugar.id) {
                val estadoMarcador = rememberUpdatedMarkerState(
                    position = LatLng(item.lugar.latitud, item.lugar.longitud)
                )

                val color = if (item.lugar.id == masCercano?.id) {
                    BitmapDescriptorFactory.HUE_GREEN
                } else {
                    BitmapDescriptorFactory.HUE_AZURE
                }

                Marker(
                    state = estadoMarcador,
                    title = item.lugar.nombre,
                    snippet = item.lugar.categoria,
                    icon = BitmapDescriptorFactory.defaultMarker(color)
                )

                LaunchedEffect(seleccionado) {
                    if (item.lugar.id == seleccionado) {
                        estadoMarcador.showInfoWindow()
                    }
                }
            }
        }
    }
}
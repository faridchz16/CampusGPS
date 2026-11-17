package com.chavez.campusgps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chavez.campusgps.data.LugaresRepository
import com.chavez.campusgps.data.UbicacionNoDisponibleException
import com.chavez.campusgps.data.UbicacionRepository
import com.chavez.campusgps.data.UltimoLugarRepository
import com.chavez.campusgps.model.Lugar
import com.chavez.campusgps.model.LugarConDistancia
import com.chavez.campusgps.model.Posicion
import com.chavez.campusgps.model.TipoError
import com.chavez.campusgps.model.UbicacionUi
import com.chavez.campusgps.util.distanciaMetros
import com.chavez.campusgps.util.formatearDistancia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository,
    private val ultimoLugarRepo: UltimoLugarRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    private val _lugares = MutableStateFlow(
        lugaresRepo.obtenerLugares().map { LugarConDistancia(it, null) }
    )

    val categorias: List<String> = lugaresRepo.obtenerLugares()
        .map { it.categoria }
        .distinct()

    private val _categoria = MutableStateFlow<String?>(null)
    val categoria: StateFlow<String?> = _categoria.asStateFlow()

    val radiosDisponibles: List<Int> = listOf(100, 200, 500)

    private val _radio = MutableStateFlow(200)
    val radio: StateFlow<Int> = _radio.asStateFlow()

    val lugares: StateFlow<List<LugarConDistancia>> =
        combine(_lugares, _categoria, _radio) { lista, cat, rad ->
            val filtrada = if (cat == null) lista else lista.filter { it.lugar.categoria == cat }
            filtrada.map { item ->
                item.copy(estaEnRadio = item.metros != null && item.metros <= rad)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _lugares.value)

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    val masCercano: StateFlow<Lugar?> = lugares
        .map { lista -> lista.firstOrNull { it.metros != null }?.lugar }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val ultimoVisitado: StateFlow<Lugar?> = ultimoLugarRepo.ultimoLugarId
        .map { id ->
            if (id == null) null
            else lugaresRepo.obtenerLugares().firstOrNull { it.id == id }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun buscar() {
        // Paso 8: antes de buscar, comprobar Google Play Services (sin Context: lo pregunta al Repository)
        if (!ubicacionRepo.playServicesDisponible()) {
            _estado.value = UbicacionUi.Error(
                "Este dispositivo no tiene Google Play Services",
                TipoError.SIN_PLAY_SERVICES
            )
            return
        }

        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess {
                    _estado.value = UbicacionUi.Lista(it)
                    _seleccionado.value = null
                    actualizarDistancias(it)
                }
                .onFailure { _estado.value = errorDe(it) }
        }
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás",
            TipoError.SIN_PERMISO
        )
    }

    fun seleccionar(id: Int?) {
        _seleccionado.value = id
        if (id != null) {
            viewModelScope.launch {
                ultimoLugarRepo.guardarUltimoLugarId(id)
            }
        }
    }

    fun elegirCategoria(nueva: String?) {
        _categoria.value = if (nueva == _categoria.value) null else nueva

        val cat = _categoria.value
        val idSeleccionado = _seleccionado.value
        if (cat != null && idSeleccionado != null) {
            val lugar = _lugares.value.firstOrNull { it.lugar.id == idSeleccionado }?.lugar
            if (lugar != null && lugar.categoria != cat) {
                _seleccionado.value = null
            }
        }
    }

    fun elegirRadio(nuevoRadio: Int) {
        _radio.value = nuevoRadio
    }

    private fun actualizarDistancias(pos: Posicion) {
        val ordenados = lugaresRepo.obtenerLugares()
            .map { lugar -> LugarConDistancia(lugar, distanciaMetros(pos, lugar)) }
            .sortedBy { it.metros }

        _lugares.value = ordenados

        ordenados.forEachIndexed { i, item ->
            Log.d(
                "CampusGPS",
                "${i + 1}. ${item.lugar.nombre}: ${formatearDistancia(item.metros ?: 0f)}"
            )
        }
    }

    private fun errorDe(e: Throwable): UbicacionUi.Error = when (e) {
        is SecurityException -> UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás",
            TipoError.SIN_PERMISO
        )
        is UbicacionNoDisponibleException -> UbicacionUi.Error(
            "No se pudo obtener la ubicación.\nActiva el GPS e intenta de nuevo",
            TipoError.GPS_APAGADO
        )
        else -> UbicacionUi.Error(
            e.message ?: "No se pudo obtener la ubicación",
            TipoError.OTRO
        )
    }
}
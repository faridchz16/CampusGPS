package com.chavez.campusgps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chavez.campusgps.model.TipoError
import com.chavez.campusgps.model.UbicacionUi

@Composable
fun MensajeError(
    error: UbicacionUi.Error,
    onAbrirAjustes: () -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = error.mensaje,
            color = MaterialTheme.colorScheme.onErrorContainer,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        )

        Spacer(Modifier.height(12.dp))

        when (error.tipo) {
            TipoError.SIN_PERMISO -> OutlinedButton(onClick = onAbrirAjustes) {
                Text("Abrir ajustes")
            }
            TipoError.GPS_APAGADO, TipoError.OTRO -> OutlinedButton(onClick = onReintentar) {
                Text("Reintentar")
            }
            TipoError.SIN_PLAY_SERVICES -> {
            }
        }
    }
}
package com.chavez.campusgps.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = TealPrimario,
    onPrimary = TealSobrePrimario,
    primaryContainer = TealContenedor,
    onPrimaryContainer = TealSobreContenedor,
    secondary = AmbarSecundario,
    onSecondary = AmbarSobreSecundario,
    secondaryContainer = AmbarContenedor,
    onSecondaryContainer = AmbarSobreContenedor,
    background = FondoClaro,
    onBackground = TextoOscuro,
    surface = FondoClaro,
    onSurface = TextoOscuro,
    surfaceVariant = SuperficieVariante,
    onSurfaceVariant = TextoSecundario,
    outline = Contorno,
    error = ErrorRojo,
    errorContainer = ErrorContenedor,
    onErrorContainer = SobreErrorContenedor
)

private val EsquemaOscuro = darkColorScheme(
    primary = TealPrimarioOscuro,
    onPrimary = TealSobrePrimarioOscuro,
    primaryContainer = TealContenedorOscuro,
    onPrimaryContainer = TealSobreContenedorOscuro,
    secondary = AmbarSecundarioOscuro,
    onSecondary = AmbarSobreSecundario,
    secondaryContainer = AmbarContenedorOscuro,
    onSecondaryContainer = AmbarContenedor,
    background = FondoOscuro,
    onBackground = TextoClaro,
    surface = FondoOscuro,
    onSurface = TextoClaro,
    surfaceVariant = SuperficieVarianteOscuro,
    onSurfaceVariant = TextoClaro,
    outline = ContornoOscuro,
    error = ErrorRojoOscuro,
    errorContainer = ErrorContenedorOscuro,
    onErrorContainer = ErrorContenedor
)

@Composable
fun CampusGPSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}
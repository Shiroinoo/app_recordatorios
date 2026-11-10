package com.example.app_recordatorios.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_recordatorios.components.WalletStack
import com.example.app_recordatorios.model.Event
import com.example.app_recordatorios.ui.theme.BallDark
import com.example.app_recordatorios.ui.theme.BallLight
import com.example.app_recordatorios.ui.theme.DarkBall1
import com.example.app_recordatorios.ui.theme.DarkBall2
import com.example.app_recordatorios.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun HomeScreen(
    onCreateClick: () -> Unit,
    viewModel: EventViewModel = viewModel(),
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    var showToday by remember { mutableStateOf(false) }
    var showStack by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // El botón atrás del sistema regresa de All a la pantalla principal
    BackHandler(enabled = showStack) { showStack = false }

    // Colores de las bolas de fondo según el modo activo
    val blobGray = if (isDark) DarkBall1 else BallLight
    val blobDark = if (isDark) DarkBall2 else BallDark

    // Color de texto para los botones de colores fuertes
    val onStrong = if (isDark) Color.Black else Color.White

    val textColor = MaterialTheme.colorScheme.onBackground

    val visibleEvents: List<Event> =
        if (showToday) viewModel.todayEvents() else viewModel.events

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
        ) {
            // Círculo gris (Ellipse 1)
            Box(
                modifier = Modifier
                    .offset(x = (-54).dp, y = (-68).dp)
                    .size(307.dp, 311.dp)
                    .clip(CircleShape)
                    .background(blobGray.copy(alpha = 0.6f))
            )

            // Círculo oscuro (Ellipse 2)
            Box(
                modifier = Modifier
                    .offset(x = 99.dp, y = (-115).dp)
                    .size(296.dp, 272.dp)
                    .clip(CircleShape)
                    .background(blobDark)
            )

            if (!showStack) {
                // Pantalla principal
                ActionButton(
                    text = "Today",
                    x = 34.dp, y = 89.dp,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    textColor = Color.Black,
                    onClick = {
                        showStack = false
                        showToday = true
                    }
                )
                ActionButton(
                    text = "Scheduled",
                    x = 227.dp, y = 89.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    textColor = onStrong,
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Estamos trabajando en ello")
                        }
                    }
                )
                ActionButton(
                    text = "All",
                    x = 34.dp, y = 210.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    textColor = Color.Black,
                    onClick = {
                        showToday = false
                        showStack = true
                    }
                )
                ActionButton(
                    text = "Create",
                    x = 227.dp, y = 210.dp,
                    color = MaterialTheme.colorScheme.primary,
                    textColor = onStrong,
                    onClick = onCreateClick
                )

                Text(
                    text = "Edit",
                    color = textColor,
                    modifier = Modifier.offset(x = 340.dp, y = 331.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                )

                Card(
                    modifier = Modifier
                        .offset(x = 34.dp, y = 377.dp)
                        .size(350.dp, 318.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f)
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        if (visibleEvents.isEmpty()) {
                            item {
                                Text(
                                    text = if (showToday) "No hay tareas para hoy" else "No hay tareas",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 24.dp)
                                )
                            }
                        }
                        items(visibleEvents, key = { it.id }) { event ->
                            EventRow(
                                event = event,
                                onToggle = { viewModel.toggleCompleted(event) }
                            )
                        }
                    }
                }

                // Botón de modo claro/oscuro (no está en Figma; es para probar)
                Text(
                    text = if (isDark) "☀" else "🌙",
                    fontSize = 22.sp,
                    color = textColor,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 16.dp)
                        .clickable { onToggleTheme() }
                )
            } else {
                // Modo All: apilado a pantalla completa, sin botones
                Text(
                    text = "← Volver",
                    color = textColor,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .offset(x = 34.dp, y = 60.dp)
                        .clickable { showStack = false }
                )

                WalletStack(
                    events = viewModel.events,
                    onToggle = { viewModel.toggleCompleted(it) },
                    onDelete = { viewModel.deleteEvent(it) },
                    modifier = Modifier
                        .offset(x = 0.dp, y = 124.dp)
                        .fillMaxSize()
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    x: Dp,
    y: Dp,
    color: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .offset(x = x, y = y)
            .size(157.dp, 87.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(color)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(text = text, color = textColor, fontSize = 15.sp)
    }
}

@Composable
private fun EventRow(event: Event, onToggle: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = event.title,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (event.isCompleted) TextDecoration.LineThrough else null
            )
            Text(
                text = "${formatDate(event.dateMillis)} · ${event.priority.label}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))
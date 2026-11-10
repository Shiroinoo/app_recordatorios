package com.example.app_recordatorios.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.app_recordatorios.model.Event

// Colores exactos de Figma, en orden de apilado
private val StackColors = listOf(
    Color(0xFFF6D0D0) to Color.Black,   // Club de lectura
    Color(0xFF632525) to Color.White,   // Taller de cerámica
    Color(0xFF9F1D1D) to Color.White,   // Cita con el dentista
    Color(0xFFA58787) to Color.White    // Reunión de emergencia
)

// Medidas de Figma (animaciones(All)), en dp
private val FirstPeek = 208.dp      // franja visible de la primera tarjeta
private val Peek = 119.dp           // franja visible de cada una de las demás
private val CardHeight = 263.dp     // alto de cada tarjeta cerrada
private val ExpandedHeight = 360.dp // alto cuando se abre una tarjeta
@Composable
fun WalletStack(
    events: List<Event>,
    onToggle: (Event) -> Unit,
    onDelete: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    if (events.isEmpty()) {
        Text("No hay tareas", color = Color.Black, modifier = modifier)
        return
    }

    var expandedId by remember { mutableStateOf<Long?>(null) }
    val haptic = LocalHapticFeedback.current
    val expandedIndex = events.indexOfFirst { it.id == expandedId }

    Box(modifier = modifier.fillMaxWidth()) {
        events.forEachIndexed { index, event ->
            val isExpanded = index == expandedIndex

// Posición base de cada tarjeta según Figma
            val baseY = if (index == 0) 0.dp else FirstPeek + Peek * (index - 1)
// Las tarjetas debajo de la abierta se recorren lo que crece
            val extra = if (expandedIndex != -1 && index > expandedIndex) {
                ExpandedHeight - CardHeight
            } else 0.dp
            val targetY = baseY + extra

            val y by animateDpAsState(targetValue = targetY, label = "y$index")
            val height by animateDpAsState(
                targetValue = if (isExpanded) ExpandedHeight else CardHeight,
                label = "h$index"
            )
            val (bg, fg) = StackColors[index % StackColors.size]
            Card(
                modifier = Modifier
                    .offset(y = y)
                    .fillMaxWidth()
                    .height(height)
                    .zIndex(if (isExpanded) 100f else index.toFloat())
                    .clickable { expandedId = if (isExpanded) null else event.id },
                shape = RoundedCornerShape(topStart = 51.dp, topEnd = 51.dp),
                colors = CardDefaults.cardColors(containerColor = bg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = event.title,
                        color = fg,
                        fontSize = 15.sp
                    )

                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Chip(text = event.priority.label, color = fg)
                                Chip(
                                    text = if (event.isCompleted) "Completada" else "Pendiente",
                                    color = fg
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                ActionButton(
                                    text = if (event.isCompleted) "Desmarcar" else "Completar",
                                    color = Color(0xFF00A3FF)
                                ) { onToggle(event) }

                                ActionButton(
                                    text = "Eliminar",
                                    color = Color(0xFFE53935)
                                ) {
                                    expandedId = null
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onDelete(event)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = color, fontSize = 12.sp)
    }
}

@Composable
private fun ActionButton(text: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 14.sp)
    }
}
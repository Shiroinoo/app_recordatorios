package com.example.app_recordatorios.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_recordatorios.model.Priority
import com.example.app_recordatorios.ui.theme.BallDark
import com.example.app_recordatorios.ui.theme.BallLight
import com.example.app_recordatorios.ui.theme.CreateBtn
import com.example.app_recordatorios.ui.theme.DarkBall1
import com.example.app_recordatorios.ui.theme.DarkBall2
import com.example.app_recordatorios.ui.theme.DarkCreate
import com.example.app_recordatorios.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Horas cada 30 minutos: 00:00, 00:30, ... 23:30
private val TimeOptions: List<String> = (0 until 48).map { i ->
    "%02d:%02d".format(i / 2, if (i % 2 == 0) 0 else 30)
}

// El DatePicker devuelve medianoche UTC; aquí pasamos ese día + la hora elegida a millis locales
private fun combine(utcDayMillis: Long, time: String): Long {
    val day = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcDayMillis }
    val (h, m) = time.split(":").map { it.toInt() }
    return Calendar.getInstance().apply {
        set(day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DAY_OF_MONTH), h, m, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun formatDay(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(millis))

// Texto negro en los campos: el fondo de los recuadros es claro en ambos modos
@Composable
private fun fieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    errorIndicatorColor = Color.Transparent,
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    cursorColor = Color.Black
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    onClose: () -> Unit,
    onSaved: () -> Unit,
    viewModel: EventViewModel,
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var dayMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var time by rememberSaveable { mutableStateOf<String?>(null) }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }

    // "Tocado": el error solo aparece después de interactuar con el campo
    var titleTouched by rememberSaveable { mutableStateOf(false) }
    var dateTouched by rememberSaveable { mutableStateOf(false) }
    var timeTouched by rememberSaveable { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var timeExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    // Colores según el modo que eligió el usuario (no el del sistema)
    val blobGray = if (isDark) DarkBall1 else BallLight
    val blobDark = if (isDark) DarkBall2 else BallDark
    val checkColor = if (isDark) DarkCreate else CreateBtn
    val checkTextColor = if (isDark) Color.Black else Color.White
    val headerTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val cardColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.74f)

    // Validaciones en tiempo real
    val day = dayMillis
    val hour = time
    val titleError = if (titleTouched) viewModel.validateTitle(title) else null
    val timeError = if (timeTouched && hour == null) "Selecciona una hora" else null
    val combined: Long? = if (day != null && hour != null) combine(day, hour) else null
    val dateError = if (dateTouched) viewModel.validateDate(day?.let { combine(it, hour ?: "00:00") }) else null
    val canSave = viewModel.validateTitle(title) == null &&
            viewModel.validateDate(combined) == null &&
            combined != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Bolas de fondo, mismas posiciones que en la pantalla principal
        Box(
            modifier = Modifier
                .offset(x = (-54).dp, y = (-68).dp)
                .size(307.dp, 311.dp)
                .clip(CircleShape)
                .background(blobGray.copy(alpha = 0.6f))
        )
        Box(
            modifier = Modifier
                .offset(x = 99.dp, y = (-115).dp)
                .size(296.dp, 272.dp)
                .clip(CircleShape)
                .background(blobDark)
        )

        // Hoja (Group 2): 350 de ancho, 90% de opacidad, esquinas superiores redondeadas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp, top = 38.dp)
                .clip(RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                .padding(horizontal = 19.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado: cerrar, título y confirmar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7D7D7D))
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", color = Color.White, fontSize = 18.sp)
                }

                Text(
                    text = "Nueva tarea",
                    color = headerTextColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(checkColor)
                        .clickable {
                            // Al intentar guardar, mostramos todos los errores pendientes
                            titleTouched = true
                            dateTouched = true
                            timeTouched = true
                            if (canSave && combined != null) {
                                viewModel.addEvent(title, description, combined, priority)
                                onSaved()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = checkTextColor, fontSize = 20.sp)
                }
            }

            // Recuadro de nombre y descripción (Rectangle 15)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(25.dp))
                    .background(cardColor)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleTouched = true
                    },
                    placeholder = { Text("Nombre de la tarea") },
                    singleLine = true,
                    isError = titleError != null,
                    supportingText = { titleError?.let { Text(it) } },
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider(color = Color.Black, thickness = 1.dp)
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Descripción") },
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Recuadro de fecha y hora (Rectangle 16)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(25.dp))
                    .background(cardColor)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Fecha: campo de solo lectura; una capa encima abre el calendario
                Box {
                    TextField(
                        value = day?.let { formatDay(it) } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Fecha") },
                        isError = dateError != null,
                        supportingText = { dateError?.let { Text(it) } },
                        colors = fieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }

                // Hora: lista desplegable
                ExposedDropdownMenuBox(
                    expanded = timeExpanded,
                    onExpandedChange = { timeExpanded = !timeExpanded }
                ) {
                    TextField(
                        value = hour ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Hora") },
                        isError = timeError != null,
                        supportingText = { timeError?.let { Text(it) } },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeExpanded)
                        },
                        colors = fieldColors(),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = timeExpanded,
                        onDismissRequest = { timeExpanded = false }
                    ) {
                        TimeOptions.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    time = t
                                    timeTouched = true
                                    timeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Prioridad: menú desplegable con Baja, Media y Alta
            ExposedDropdownMenuBox(
                expanded = priorityExpanded,
                onExpandedChange = { priorityExpanded = !priorityExpanded }
            ) {
                TextField(
                    value = priority.label,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded)
                    },
                    colors = fieldColors(),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                        .background(cardColor, RoundedCornerShape(25.dp))
                )
                ExposedDropdownMenu(
                    expanded = priorityExpanded,
                    onDismissRequest = { priorityExpanded = false }
                ) {
                    Priority.entries.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.label) },
                            onClick = {
                                priority = p
                                priorityExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Botón de modo claro/oscuro (mismo que en la pantalla principal)
        Text(
            text = if (isDark) "☀" else "🌙",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 16.dp)
                .clickable { onToggleTheme() }
        )
    }

    // Diálogo del calendario
    if (showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = day)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dayMillis = pickerState.selectedDateMillis
                    dateTouched = true
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}
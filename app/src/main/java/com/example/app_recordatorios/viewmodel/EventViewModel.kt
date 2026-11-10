package com.example.app_recordatorios.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.app_recordatorios.model.Event
import com.example.app_recordatorios.model.Priority
import java.util.Calendar

class EventViewModel : ViewModel() {

    // Lista observable
    val events = mutableStateListOf<Event>()

    private var nextId = 1L

    fun addEvent(
        title: String,
        description: String,
        dateMillis: Long,
        priority: Priority
    ) {
        events.add(
            Event(
                id = nextId++,
                title = title.trim(),
                description = description.trim(),
                dateMillis = dateMillis,
                priority = priority
            )
        )
    }

    fun deleteEvent(event: Event) {
        events.remove(event)
    }

    fun toggleCompleted(event: Event) {
        val index = events.indexOfFirst { it.id == event.id }
        if (index != -1) {
            events[index] = events[index].copy(isCompleted = !events[index].isCompleted)
        }
    }

    // Filtro del botón "Today"
    fun todayEvents(): List<Event> =
        events.filter { isSameDay(it.dateMillis, System.currentTimeMillis()) }

    // Validaciones para el formulario (se llaman en cada cambio de campo)
    fun validateTitle(title: String): String? =
        if (title.isBlank()) "El título es obligatorio" else null

    fun validateDate(dateMillis: Long?): String? {
        if (dateMillis == null) return "Selecciona una fecha"
        return if (dateMillis < startOfToday()) "La fecha debe ser hoy o futura" else null
    }
}

// Utilidades de fecha
private fun isSameDay(a: Long, b: Long): Boolean {
    val calA = Calendar.getInstance().apply { timeInMillis = a }
    val calB = Calendar.getInstance().apply { timeInMillis = b }
    return calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) &&
            calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR)
}

private fun startOfToday(): Long =
    Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
package com.example.app_recordatorios.model

enum class Priority(val label: String) {
    LOW("Baja"),
    MEDIUM("Media"),
    HIGH("Alta")
}

data class Event(
    val id: Long,
    val title: String,
    val description: String = "",
    val dateMillis: Long,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false
)
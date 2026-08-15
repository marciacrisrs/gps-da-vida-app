package com.gpsdavida.app.ui.navigation

object GpsRoutes {
    const val AGORA = "agora"
    const val MEU_DIA = "meu_dia"
    const val EVENTS = "events"
    const val TASKS = "tasks"
    const val EVENT_EDITOR = "event_editor/{eventId}"
    const val TASK_EDITOR = "task_editor/{taskId}"
    const val NEW_EVENT_ID = "new"
    const val NEW_TASK_ID = "new"

    fun eventEditor(eventId: String = NEW_EVENT_ID): String = "event_editor/$eventId"
    fun taskEditor(taskId: String = NEW_TASK_ID): String = "task_editor/$taskId"
}

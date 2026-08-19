package com.gpsdavida.app.ui.agora

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpsdavida.app.domain.model.ActivityInstance
import com.gpsdavida.app.domain.model.ActivityStatus
import com.gpsdavida.app.domain.model.DailyActivity
import com.gpsdavida.app.domain.model.NextActionContext
import com.gpsdavida.app.domain.model.Priority
import com.gpsdavida.app.domain.usecase.ChooseNextActivity
import com.gpsdavida.app.domain.usecase.CompleteActivityInstance
import com.gpsdavida.app.domain.usecase.DeferActivityInstance
import com.gpsdavida.app.domain.usecase.ObserveExecutableDay
import com.gpsdavida.app.domain.usecase.SkipActivityInstance
import com.gpsdavida.app.ui.next.NextActionState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Clock
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AgoraUiState(
    val title: String = "",
    val durationMinutes: Long? = null,
    val scheduledTime: LocalTime? = null,
    val priority: Priority? = null,
    val nextTitle: String? = null,
    val state: NextActionState = NextActionState.Empty,
    val currentActivity: ActivityInstance? = null,
)

@HiltViewModel
class AgoraViewModel @Inject constructor(
    observeExecutableDay: ObserveExecutableDay,
    private val chooseNextActivity: ChooseNextActivity,
    private val completeActivity: CompleteActivityInstance,
    private val skipActivity: SkipActivityInstance,
    private val deferActivity: DeferActivityInstance,
    private val clock: Clock,
) : ViewModel() {
    private var inProgressId: String? = null

    val state: StateFlow<AgoraUiState> = observeExecutableDay()
        .map { activities -> toUiState(activities) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AgoraUiState())

    fun startCurrent() {
        inProgressId = state.value.currentActivity?.id?.value
    }

    fun completeCurrent() {
        val activity = state.value.currentActivity ?: return
        viewModelScope.launch {
            completeActivity(activity)
            inProgressId = null
        }
    }

    fun skipCurrent() {
        val activity = state.value.currentActivity ?: return
        viewModelScope.launch {
            skipActivity(activity)
            inProgressId = null
        }
    }

    fun deferCurrent() {
        val activity = state.value.currentActivity ?: return
        viewModelScope.launch {
            deferActivity(activity)
            inProgressId = null
        }
    }

    private fun toUiState(activities: List<DailyActivity>): AgoraUiState {
        val decision = chooseNextActivity(
            activities.map { it.instance },
            NextActionContext(now = clock.instant(), zoneId = clock.zone),
        )
        val recommended = decision.recommended
        if (recommended == null || recommended.status != ActivityStatus.PENDING) {
            val hasPending = activities.any { it.instance.status == ActivityStatus.PENDING }
            return AgoraUiState(
                state = if (hasPending) NextActionState.Empty else NextActionState.Completed,
            )
        }

        val current = activities.first { it.instance.id == recommended.id }
        val nextTitle = decision.next?.let { next ->
            activities.firstOrNull { it.instance.id == next.id }?.title
        }

        return AgoraUiState(
            title = current.title,
            durationMinutes = recommended.plannedDuration.toMinutes(),
            scheduledTime = recommended.planned.start.atZone(clock.zone).toLocalTime(),
            priority = recommended.priority,
            nextTitle = nextTitle,
            state = if (inProgressId == recommended.id.value) {
                NextActionState.InProgress
            } else {
                NextActionState.Ready
            },
            currentActivity = recommended,
        )
    }
}

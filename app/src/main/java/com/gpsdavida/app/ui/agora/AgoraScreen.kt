package com.gpsdavida.app.ui.agora

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gpsdavida.app.ui.next.NextActionCard
import com.gpsdavida.app.ui.next.NextActionUiModel
import com.gpsdavida.app.ui.tasks.labelRes

@Composable
fun AgoraScreen(
    viewModel: AgoraViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        NextActionCard(
            model = NextActionUiModel(
                title = state.title,
                durationMinutes = state.durationMinutes,
                scheduledTime = state.scheduledTime,
                priorityLabel = state.priority?.let { stringResource(it.labelRes()) },
                state = state.state,
            ),
            onStart = viewModel::startCurrent,
            onSnooze = viewModel::deferCurrent,
            onComplete = viewModel::completeCurrent,
            onSwap = viewModel::skipCurrent,
        )
        state.nextTitle?.let { next ->
            Text(
                text = stringResource(com.gpsdavida.app.R.string.agora_next, next),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

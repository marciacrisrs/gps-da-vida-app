package com.gpsdavida.app.ui.next

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gpsdavida.app.ui.theme.GpsDaVidaTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NextActionCardTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun readyState_showsActionAndStartsIt() {
        var started = false

        composeRule.setContent {
            GpsDaVidaTheme {
                NextActionCard(
                    model = NextActionUiModel(
                        title = "Estudar francês",
                        durationMinutes = 30,
                    ),
                    onStart = { started = true },
                )
            }
        }

        composeRule.onNodeWithText("O que faço agora?").assertIsDisplayed()
        composeRule.onNodeWithText("Estudar francês").assertIsDisplayed()
        composeRule.onNodeWithText("Começar").performClick()

        assertTrue(started)
    }

    @Test
    fun emptyState_explainsThatThereIsNoNextAction() {
        composeRule.setContent {
            GpsDaVidaTheme {
                NextActionCard(
                    model = NextActionUiModel(
                        title = "",
                        state = NextActionState.Empty,
                    ),
                )
            }
        }

        composeRule.onNodeWithText("Você está em dia.").assertIsDisplayed()
        composeRule.onNodeWithText("Quando houver uma próxima ação, ela aparecerá aqui.").assertIsDisplayed()
    }
}

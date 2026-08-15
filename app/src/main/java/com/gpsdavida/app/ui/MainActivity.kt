package com.gpsdavida.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gpsdavida.app.ui.navigation.GpsNavHost
import com.gpsdavida.app.ui.theme.GpsDaVidaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GpsDaVidaTheme {
                GpsNavHost()
            }
        }
    }
}

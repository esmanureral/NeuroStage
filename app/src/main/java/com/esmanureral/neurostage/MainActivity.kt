package com.esmanureral.neurostage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.esmanureral.neurostage.ui.AppRoot
import com.esmanureral.neurostage.ui.theme.NeuroStageTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeuroStageTheme {
                AppRoot()
            }
        }
    }
}
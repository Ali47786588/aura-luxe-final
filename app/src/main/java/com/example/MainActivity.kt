package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.AuraLuxeMainScreen
import com.example.ui.theme.AuraLuxeTheme
import com.example.ui.theme.ObsidianDark

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
          com.example.data.repository.AuraRepository.defaultContext = applicationContext
        com.example.data.repository.AuraRepository.defaultDatabase =
            com.example.data.local.AuraDatabase.getInstance(applicationContext)
        setContent {
            AuraLuxeTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ObsidianDark
                ) {
                    AuraLuxeMainScreen()
                }
            }
        }
    }
}

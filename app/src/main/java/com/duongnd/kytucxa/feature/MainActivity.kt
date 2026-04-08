package com.duongnd.kytucxa.feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.duongnd.kytucxa.core.navigations.graph.root.KyTucXaAppNavGraph
import com.duongnd.kytucxa.core.ui.theme.KyTucXaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KyTucXaTheme {
                KyTucXaApp()
            }
        }
    }
}

@Composable
fun KyTucXaApp() {
    val navController = rememberNavController()
    KyTucXaAppNavGraph(navController = navController)
}


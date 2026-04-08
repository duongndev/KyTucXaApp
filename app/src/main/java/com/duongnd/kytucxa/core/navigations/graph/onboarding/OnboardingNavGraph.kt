package com.duongnd.kytucxa.core.navigations.graph.onboarding

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.duongnd.kytucxa.core.navigations.Graphs

fun NavGraphBuilder.onboardingNavGraph(navController: NavHostController) {
    navigation(
        startDestination = "onboarding_screen",
        route = Graphs.ONBOARDING
    ) {
        // Add onboarding screens here
    }
}

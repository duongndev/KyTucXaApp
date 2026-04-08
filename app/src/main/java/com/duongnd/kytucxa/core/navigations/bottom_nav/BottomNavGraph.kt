package com.duongnd.kytucxa.core.navigations.bottom_nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.duongnd.kytucxa.core.navigations.Screen
import com.duongnd.kytucxa.feature.checkin.CheckInScreen
import com.duongnd.kytucxa.feature.home.HomeScreen
import com.duongnd.kytucxa.feature.profile.ProfileScreen
import com.duongnd.kytucxa.feature.room.RoomScreen

fun NavGraphBuilder.bottomNavGraph(navController: NavHostController) {
    composable(Screen.Home.route) {
        _root_ide_package_.com.duongnd.kytucxa.feature.home.HomeScreen(
            onNavigateToPayment = {
                navController.navigate(Screen.Payment.route)
            }
        )
    }
    composable(Screen.Room.route) {
        _root_ide_package_.com.duongnd.kytucxa.feature.room.RoomScreen(
            onRoomClick = { roomId ->
                navController.navigate(Screen.RoomDetail.createRoute(roomId))
            }
        )
    }
    composable(Screen.CheckIn.route) {
        _root_ide_package_.com.duongnd.kytucxa.feature.checkin.CheckInScreen(onBack = { navController.popBackStack() })
    }
    composable(Screen.Profile.route) {
        _root_ide_package_.com.duongnd.kytucxa.feature.profile.ProfileScreen()
    }
}

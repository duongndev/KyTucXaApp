package com.duongnd.kytucxa.core.navigations.bottom_nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.ui.graphics.vector.ImageVector
import com.duongnd.kytucxa.core.navigations.Screen

enum class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen
) {
    HOME("Trang chủ", Icons.Rounded.Home, Screen.Home),
    HISTORY("Lịch sử", Icons.Rounded.History, Screen.PaymentHistory),
    CHECKIN("Quét mã", Icons.Rounded.QrCodeScanner, Screen.CheckIn),
    BOOKING("Phòng", Icons.Rounded.Apartment, Screen.Room),
    PROFILE("Cá nhân", Icons.Rounded.Person, Screen.Profile),
}

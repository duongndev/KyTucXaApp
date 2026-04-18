package com.duongnd.kytucxa.feature.home

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Handyman
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MeetingRoom
import androidx.compose.material.icons.rounded.Nfc
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TwoWheeler
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.duongnd.kytucxa.core.ui.components.DormCard
import com.duongnd.kytucxa.core.ui.theme.PrimaryBlue
import com.duongnd.kytucxa.core.ui.theme.SuccessGreen
import com.duongnd.kytucxa.core.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToPayment: () -> Unit = {},
    onNavigateToRegistration: () -> Unit = {}
) {
    var showVirtualCard by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val primaryColor = PrimaryBlue
    val backgroundColor = Color.White
    val lazyListState = rememberLazyListState()

    // Chỉ hiện Sticky Header khi Header chính đã cuộn qua một phần
    val isScrolled by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex > 0 }
    }

    // Cập nhật màu Status Bar và Navigation Bar động
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Khi hiện Sticky Header, Status Bar tiệp màu PrimaryBlue đặc. Khi ở Top, trong suốt để thấy Gradient.
            window.statusBarColor =
                if (isScrolled) primaryColor.toArgb() else Color.Transparent.toArgb()

            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false // Luôn dùng icon trắng trên nền xanh

            window.navigationBarColor = Color.Transparent.toArgb()
            controller.isAppearanceLightNavigationBars = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Nền Gradient cố định phía sau
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.8f),
                            backgroundColor
                        )
                    )
                )
        )

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Header chính
            item {
                ModernHomeHeader()
            }

            // 2. STICKY HEADER
            stickyHeader {
                if (isScrolled) {
                    StickyHomeHeader(primaryColor)
                }
            }

            // 3. Phần nội dung (Dịch vụ, Tin tức, Hoạt động)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = backgroundColor,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                ) {
                    Column {
                        ServiceSection(
                            isTablet = false,
                            onVirtualCardClick = { showVirtualCard = true },
                            onPaymentClick = onNavigateToPayment,
                            onRegistrationClick = onNavigateToRegistration
                        )

                        PromotionSection(isTablet = false)

//                        SectionHeader(title = "Hoạt động gần đây", onSeeAll = {})
                    }
                }
            }

//            items(dummyActivities) { activity ->
//                Box(modifier = Modifier.background(backgroundColor)) {
//                    ModernActivityItem(activity, isTablet = false)
//                }
//            }

            item {
                Spacer(
                    modifier = Modifier
                        .height(110.dp)
                        .fillMaxWidth()
                        .background(backgroundColor)
                )
            }
        }

        // Bottom Sheet Thẻ KTX
        if (showVirtualCard) {
            ModalBottomSheet(
                onDismissRequest = { showVirtualCard = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Box(modifier = Modifier.padding(bottom = 32.dp)) {
                    DormCard(
                        "Trần Thị Hương Giang",
                        "SV736734",
                        "Đại học Quốc Gia Hà Nội",
                        "A-1234",
                        "31/12/2026"
                    )
                }
            }
        }
    }
}

@Composable
fun StickyHomeHeader(primaryColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = primaryColor,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, end = 12.dp, top = 8.dp, bottom = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Avatar thu nhỏ
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Trần Thị Hương Giang",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Phòng 1234 • Mỹ Đình",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            // Search Icon trong vòng tròn (giống Header chính)
            Surface(
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.15f),
                onClick = { /* TODO: Search action */ }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernHomeHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "Xin chào,", color = Color.White.copy(alpha = 0.8f), fontSize = 15.sp)
                Text(
                    text = "Trần Thị Hương Giang",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
            }
            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.Search,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White.copy(alpha = 0.12f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.LocationOn,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Khu nội trú Mỹ Đình • Phòng 1234", color = Color.White, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun ServiceSection(
    isTablet: Boolean,
    onVirtualCardClick: () -> Unit,
    onPaymentClick: () -> Unit,
    onRegistrationClick: () -> Unit
) {
    val services = listOf(
        ServiceItemData("Đổi phòng", Icons.Rounded.MeetingRoom, PrimaryBlue),
        ServiceItemData(
            "Đăng ký KTX",
            Icons.Rounded.Bolt,
            WarningOrange,
            onClick = onRegistrationClick
        ),
        ServiceItemData(
            "Thanh toán",
            Icons.Rounded.AccountBalanceWallet,
            SuccessGreen,
            onPaymentClick
        ),
        ServiceItemData(
            "Thẻ KTX",
            Icons.Rounded.Nfc,
            Color(0xFF6366F1),
            onClick = onVirtualCardClick
        ),
        ServiceItemData("Hợp đồng", Icons.Rounded.Description, Color(0xFFEC4899)),
        ServiceItemData("Sửa chữa", Icons.Rounded.Handyman, MaterialTheme.colorScheme.error),
        ServiceItemData("Gửi xe", Icons.Rounded.TwoWheeler, Color(0xFF8B5CF6)),
        ServiceItemData("Dịch vụ khác", Icons.Rounded.GridView, Color.Gray)
    )

    val columns = 4

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
        Text(
            "Dịch vụ nội trú",
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(20.dp))

        services.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { item ->
                    ModernServiceItem(item, Modifier.weight(1f))
                }
                if (rowItems.size < columns) {
                    repeat(columns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
        }
    }
}

@Composable
fun ModernServiceItem(item: ServiceItemData, modifier: Modifier) {
    Column(
        modifier = modifier.clickable { item.onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(58.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.3f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(item.icon, null, tint = item.color, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            item.label,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4A4A4A)
        )
    }
}

@Composable
fun PromotionSection(isTablet: Boolean) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        SectionHeader(title = "Tin tức & Ưu đãi", onSeeAll = {})
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PromotionCard(
                    "Ưu đãi tháng 12",
                    "Giảm 10% phí dịch vụ khi thanh toán sớm qua ứng dụng.",
                    PrimaryBlue,
                    isTablet
                )
            }
            item {
                PromotionCard(
                    "Thông báo bảo trì",
                    "Lịch bảo trì hệ thống điện nước tòa nhà A vào chủ nhật này.",
                    WarningOrange,
                    isTablet
                )
            }
        }
    }
}

@Composable
fun PromotionCard(title: String, desc: String, color: Color, isTablet: Boolean) {
    val cardWidth = if (isTablet) 400.dp else 300.dp
    Box(
        modifier = Modifier
            .width(cardWidth)
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(brush = Brush.linearGradient(listOf(color, color.copy(alpha = 0.8f))))
            .padding(20.dp)
    ) {
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(desc, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Text(
            text = "Xem tất cả",
            fontSize = 14.sp,
            color = PrimaryBlue,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onSeeAll() }
        )
    }
}

@Composable
fun ModernActivityItem(activity: ActivityData, isTablet: Boolean) {
    val horizontalPadding = if (isTablet) 40.dp else 20.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 6.dp)
            .background(Color.White, RoundedCornerShape(18.dp))
            .border(0.5.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = activity.color.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(activity.icon, null, tint = activity.color, modifier = Modifier.size(22.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(activity.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(activity.time, fontSize = 12.sp, color = Color.Gray)
        }
        Text(
            activity.amount,
            fontWeight = FontWeight.Bold,
            color = if (activity.amount.startsWith("-")) Color(0xFFE53935) else SuccessGreen,
            fontSize = 16.sp
        )
    }
}

data class ServiceItemData(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit = {}
)

data class ActivityData(
    val title: String,
    val time: String,
    val amount: String,
    val icon: ImageVector,
    val color: Color
)

val dummyActivities = listOf(
    ActivityData(
        "Thanh toán tiền điện",
        "Hôm nay, 08:30",
        "-150.000đ",
        Icons.Rounded.Bolt,
        WarningOrange
    ),
    ActivityData(
        "Nạp tiền tài khoản",
        "Hôm qua, 15:20",
        "+500.000đ",
        Icons.Rounded.CheckCircle,
        SuccessGreen
    ),
    ActivityData(
        "Tiền phòng tháng 12",
        "2 ngày trước",
        "-1.200.000đ",
        Icons.Rounded.MeetingRoom,
        PrimaryBlue
    ),
    ActivityData(
        "Thanh toán tiền điện",
        "Hôm nay, 08:30",
        "-150.000đ",
        Icons.Rounded.Bolt,
        WarningOrange
    ),
    ActivityData(
        "Nạp tiền tài khoản",
        "Hôm qua, 15:20",
        "+500.000đ",
        Icons.Rounded.CheckCircle,
        SuccessGreen
    ),
    ActivityData(
        "Tiền phòng tháng 12",
        "2 ngày trước",
        "-1.200.000đ",
        Icons.Rounded.MeetingRoom,
        PrimaryBlue
    ),
    ActivityData(
        "Thanh toán tiền điện",
        "Hôm nay, 08:30",
        "-150.000đ",
        Icons.Rounded.Bolt,
        WarningOrange
    ),
    ActivityData(
        "Nạp tiền tài khoản",
        "Hôm qua, 15:20",
        "+500.000đ",
        Icons.Rounded.CheckCircle,
        SuccessGreen
    ),
    ActivityData(
        "Tiền phòng tháng 12",
        "2 ngày trước",
        "-1.200.000đ",
        Icons.Rounded.MeetingRoom,
        PrimaryBlue
    )
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreView() {
    HomeScreen()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StickyHeaderPreview() {
    StickyHomeHeader(PrimaryBlue)
}
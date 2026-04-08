package com.duongnd.kytucxa.feature.room

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RoomScreen(onRoomClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Đăng ký phòng",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Chọn loại phòng phù hợp với nhu cầu của bạn",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(_root_ide_package_.com.duongnd.kytucxa.feature.room.roomTypes) { roomType ->
                _root_ide_package_.com.duongnd.kytucxa.feature.room.RoomTypeCard(
                    roomType,
                    onClick = { onRoomClick(roomType.id) })
            }
        }
    }
}

@Composable
fun RoomTypeCard(roomType: com.duongnd.kytucxa.feature.room.RoomType, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = roomType.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Badge(
                    containerColor = if (roomType.available > 0) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = if (roomType.available > 0) "Còn trống" else "Hết chỗ",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Số người: ${roomType.capacity} người/phòng")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bed, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Hiện còn: ${roomType.available} giường")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = roomType.price,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = roomType.available > 0
            ) {
                Text("Xem chi tiết")
            }
        }
    }
}

data class RoomType(
    val id: String,
    val name: String,
    val capacity: Int,
    val available: Int,
    val price: String
)

val roomTypes = listOf(
    _root_ide_package_.com.duongnd.kytucxa.feature.room.RoomType(
        "1",
        "Phòng Tiêu chuẩn (4 người)",
        4,
        12,
        "1.200.000đ / tháng"
    ),
    _root_ide_package_.com.duongnd.kytucxa.feature.room.RoomType(
        "2",
        "Phòng Dịch vụ (2 người)",
        2,
        5,
        "2.500.000đ / tháng"
    ),
    _root_ide_package_.com.duongnd.kytucxa.feature.room.RoomType(
        "3",
        "Phòng VIP (1 người)",
        1,
        2,
        "4.500.000đ / tháng"
    ),
    _root_ide_package_.com.duongnd.kytucxa.feature.room.RoomType(
        "4",
        "Phòng Tiêu chuẩn (8 người)",
        8,
        0,
        "800.000đ / tháng"
    )
)

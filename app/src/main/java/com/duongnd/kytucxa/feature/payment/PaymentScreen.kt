package com.duongnd.kytucxa.feature.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duongnd.kytucxa.core.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onSeeHistory: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = onSeeHistory) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Cần thanh toán",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(unpaidInvoices) { invoice ->
                    InvoiceCard(invoice)
                }
            }
        }
    }
}

@Composable
fun InvoiceCard(invoice: Invoice) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = invoice.type.getColor().copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = invoice.type.icon,
                        contentDescription = null,
                        tint = invoice.type.getColor()
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = invoice.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hạn: ${invoice.dueDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = invoice.amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                TextButton(
                    onClick = { /* TODO */ },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Thanh toán", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

enum class InvoiceType(val icon: ImageVector) {
    ELECTRIC(Icons.Default.ElectricBolt),
    WATER(Icons.Default.WaterDrop),
    ROOM(Icons.Default.Home);

    @Composable
    fun getColor(): Color = when (this) {
        ELECTRIC -> WarningOrange
        WATER -> Color(0xFF2196F3)
        ROOM -> MaterialTheme.colorScheme.primary
    }
}

data class Invoice(
    val title: String,
    val amount: String,
    val dueDate: String,
    val type: InvoiceType
)

val unpaidInvoices = listOf(
    Invoice(
        "Tiền phòng tháng 11",
        "1.200.000đ",
        "05/11/2023",
        InvoiceType.ROOM
    ),
    Invoice(
        "Tiền điện tháng 10",
        "150.000đ",
        "10/11/2023",
        InvoiceType.ELECTRIC
    ),
    Invoice(
        "Tiền nước tháng 10",
        "45.000đ",
        "10/11/2023",
        InvoiceType.WATER
    )
)

package com.duongnd.kytucxa.feature.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duongnd.kytucxa.core.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử thanh toán") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(historyInvoices) { invoice ->
                HistoryInvoiceCard(invoice)
            }
        }
    }
}

@Composable
fun HistoryInvoiceCard(invoice: HistoryInvoice) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = invoice.title, fontWeight = FontWeight.Bold)
                Text(
                    text = "Ngày thanh toán: ${invoice.paidDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = invoice.amount,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Thành công",
                    style = MaterialTheme.typography.labelSmall,
                    color = SuccessGreen
                )
            }
        }
    }
}

data class HistoryInvoice(
    val title: String,
    val amount: String,
    val paidDate: String
)

val historyInvoices = listOf(
    HistoryInvoice(
        "Tiền phòng tháng 10",
        "1.200.000đ",
        "02/10/2023"
    ),
    HistoryInvoice(
        "Tiền điện tháng 09",
        "120.000đ",
        "05/10/2023"
    ),
    HistoryInvoice(
        "Tiền nước tháng 09",
        "38.000đ",
        "05/10/2023"
    ),
    HistoryInvoice(
        "Tiền phòng tháng 09",
        "1.200.000đ",
        "01/09/2023"
    )
)

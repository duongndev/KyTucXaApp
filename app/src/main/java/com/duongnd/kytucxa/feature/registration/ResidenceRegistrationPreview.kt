package com.duongnd.kytucxa.feature.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duongnd.kytucxa.core.utils.DateUtils
import com.duongnd.kytucxa.core.utils.GenderUtils
import com.duongnd.kytucxa.domain.models.TemporaryModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ResidenceRegistrationPreview(
    data: TemporaryModel,
    onBack: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isTablet = screenWidth >= 600.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xem trước tờ khai cư trú", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Triển khai tải PDF sau */ }) {
                        Icon(Icons.Rounded.Download, contentDescription = "Download")
                    }
                }
            )
        },
        containerColor = if (isTablet) Color(0xFFF5F5F5) else MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 850.dp)
                    .fillMaxHeight()
                    .then(
                        if (isTablet) Modifier
                            .padding(24.dp)
                            .shadow(8.dp, RoundedCornerShape(8.dp))
                        else Modifier
                    ),
                shape = if (isTablet) RoundedCornerShape(8.dp) else RoundedCornerShape(0.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(if (isTablet) 48.dp else 24.dp)
                ) {
                    // Header Section
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            "Độc lập - Tự do - Hạnh phúc",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier
                            .width(160.dp)
                            .height(1.dp)
                            .background(Color.Black))
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "TỜ KHAI THAY ĐỔI THÔNG TIN CƯ TRÚ",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Form Body
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("Kính gửi:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            ResidenceDottedText(data.receiver, modifier = Modifier.weight(1f))
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("Họ, chữ đệm và tên:", fontSize = 14.sp)
                            ResidenceDottedText(data.fullName, modifier = Modifier.weight(1f))
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("Ngày, tháng, năm sinh:", fontSize = 14.sp)
                            ResidenceDottedText(
                                DateUtils.formatString(data.dateOfBirth),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("Giới tính:", fontSize = 14.sp)
                            ResidenceDottedText(
                                GenderUtils.getGenderDisplay(data.gender),
                                modifier = Modifier.weight(0.5f)
                            )
                        }


                        Column {
                            Text("Số định danh cá nhân:", fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            IdNumberGrid(data.cccd)
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("Số điện thoại liên hệ:", fontSize = 14.sp)
                            ResidenceDottedText(data.phoneNumber, modifier = Modifier.weight(1f))
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("Email:", fontSize = 14.sp)
                            ResidenceDottedText(data.email, modifier = Modifier.weight(1f))
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("Họ, chữ đệm và tên chủ hộ:", fontSize = 14.sp)
                            ResidenceDottedText(data.ownerName!!, modifier = Modifier.weight(1f))
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("Mối quan hệ với chủ hộ:", fontSize = 14.sp)
                            ResidenceDottedText(
                                data.ownerRelation!!,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Column {
                            Text("Số định danh cá nhân của chủ hộ:", fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            IdNumberGrid(data.ownerCccd!!)
                        }

                        Column {
                            Text("Nội dung đề nghị:", fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = data.requestContent.ifEmpty { "................................................................................................................................................................................................................................................................" },
                                fontSize = 14.sp,
                                lineHeight = 24.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Justify
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // Signature Section
                    }
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
fun ResidenceDottedText(value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(start = 8.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = "........................................................................................................................................................",
            fontSize = 14.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier.alpha(0.3f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@Composable
fun IdNumberGrid(number: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
        val paddedNumber = number.padEnd(12, ' ')
        paddedNumber.forEach { char ->
            Box(
                modifier = Modifier
                    .size(width = 24.dp, height = 32.dp)
                    .border(0.5.dp, Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(text = char.toString(), fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

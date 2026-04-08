package com.duongnd.kytucxa.feature.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckBox
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duongnd.kytucxa.R
import com.duongnd.kytucxa.core.utils.DateUtils
import com.duongnd.kytucxa.domain.models.FormFields
import com.duongnd.kytucxa.core.utils.PdfUtils


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ElectronicFormView(data: FormFields, onBack: () -> Unit) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isTablet = screenWidth >= 600.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xem trước đơn đăng ký", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { PdfUtils.generateRegistrationPdf(context, data) }) {
                        Icon(Icons.Rounded.Download, contentDescription = "Download PDF")
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
                        .padding(if (isTablet) 48.dp else 20.dp)
                ) {
                    // Header (Photo & Country Info)
                    AdaptiveHeader(isTablet)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "ĐƠN ĐĂNG KÝ THUÊ NHÀ Ở SINH VIÊN",
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isTablet) 20.sp else 18.sp,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Kính gửi: BAN QUẢN LÝ CÁC CÔNG TRÌNH NHÀ Ở VÀ CÔNG SỞ",
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isTablet) 15.sp else 13.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Form Content
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        
                        // Họ tên & Giới tính
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                            FormLabel("Họ và tên:")
                            DottedText(data.fullName, modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Nam", fontSize = 13.sp, color = Color.Black)
                            Icon(
                                imageVector = if (data.gender == "male") Icons.Rounded.CheckBox else Icons.Rounded.CheckBoxOutlineBlank,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp).padding(start = 2.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Nữ", fontSize = 13.sp, color = Color.Black)
                            Icon(
                                imageVector = if (data.gender == "female") Icons.Rounded.CheckBox else Icons.Rounded.CheckBoxOutlineBlank,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp).padding(start = 2.dp),
                                tint = Color.Black
                            )
                        }

                        // Ngày sinh
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                            FormLabel("Ngày sinh:")
                            DottedText(DateUtils.formatString(data.dob), modifier = Modifier.weight(1f))
                        }

                        // CMND/CCCD, Ngày cấp, Nơi cấp
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.weight(1.2f)) {
                                FormLabel("CCCD số:")
                                DottedText(data.idNumber, modifier = Modifier.weight(1f))
                            }
                            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.weight(1f)) {
                                FormLabel("Ngày cấp:")
                                DottedText(data.idIssueDate, modifier = Modifier.weight(1f))
                            }
                            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.weight(1.5f)) {
                                FormLabel("Nơi cấp:")
                                DottedText(data.idIssuePlace, modifier = Modifier.weight(1f))
                            }
                        }

                        // Hộ khẩu thường trú
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                            FormLabel("Có hộ khẩu thường trú tại:")
                            DottedText(data.permanentAddress, modifier = Modifier.weight(1f))
                        }

                        // Số điện thoại & Email
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.weight(1.2f)) {
                                FormLabel("Số điện thoại:")
                                DottedText(data.phoneNumber, modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.weight(1f)) {
                                FormLabel("Email:")
                                DottedText(data.email, modifier = Modifier.weight(1f))
                            }
                        }

                        // Khi cần liên hệ (báo tin)
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                            FormLabel("Khi cần liên hệ (báo tin):")
                            DottedText(data.emergencyContact, modifier = Modifier.weight(1f))
                        }

                        // Là sinh viên học tập tại cơ sở đào tạo
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                            FormLabel("Là sinh viên học tập tại cơ sở đào tạo:")
                            DottedText(data.schoolName, modifier = Modifier.weight(1f))
                        }

                        // Niên khóa, Lớp, Khoa
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.weight(1f)) {
                                FormLabel("Niên khóa:")
                                DottedText(data.academicYear, modifier = Modifier.weight(1f))
                            }
                            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.weight(1f)) {
                                FormLabel("(2) Lớp:")
                                DottedText(data.className, modifier = Modifier.weight(1f))
                            }
                            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.weight(1.2f)) {
                                FormLabel("Khoa:")
                                DottedText(data.department, modifier = Modifier.weight(1f))
                            }
                        }

                        // Số thẻ sinh viên
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                            FormLabel("Số thẻ sinh viên, học sinh (Nếu có):")
                            DottedText(data.studentId, modifier = Modifier.weight(1f))
                        }

                        // Đối tượng ưu tiên
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                            FormLabel("Đối tượng ưu tiên (Nếu có):")
                            DottedText(data.priorityType, modifier = Modifier.weight(1f))
                        }
                        
                        Text(
                            text = "(Giấy chứng nhận ưu tiên kèm theo)",
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "       Tôi làm đơn này đề nghị: BAN QUẢN LÝ CÁC CÔNG TRÌNH NHÀ Ở VÀ CÔNG SỞ xét duyệt cho tôi được thuê nhà ở sinh viên tại KTX",
                                fontSize = 13.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                DottedText(data.dormName, modifier = Modifier.weight(1f))
                            }
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(text = "Khu nhà ở sinh viên trong thời gian:", fontSize = 13.sp, color = Color.Black)
                                DottedText(data.duration, modifier = Modifier.weight(1f))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "       Tôi đã đọc Bản nội quy sử dụng nhà ở sinh viên và cam kết tuân thủ nội quy sử dụng nhà ở sinh viên, cam kết trả tiền thuê nhà đầy đủ, đúng thời hạn khi được thuê nhà ở.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Justify,
                            color = Color.Black,
                            lineHeight = 20.sp
                        )

                        Text(
                            text = "       Tôi cam kết những lời khai trong đơn là đúng sự thật, tôi xin chịu trách nhiệm trước pháp luật về các nội dung đã kê khai./.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Justify,
                            color = Color.Black,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Dates and Signatures
                        val datePlaceholderLeft = "Hà Nội, ngày......tháng......năm 20..."
                        val datePlaceholderRight = "Hà Nội, ngày......tháng......năm 20..."
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = datePlaceholderLeft,
                                    fontSize = 12.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                SignatureColumn(
                                    title = "Xác nhận của cơ sở đào tạo",
                                    subtitle = "(Ký ghi ngày tháng và đóng dấu)",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = datePlaceholderRight,
                                    fontSize = 12.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                SignatureColumn(
                                    title = "Người viết đơn",
                                    subtitle = "(Ký và ghi rõ họ tên)",
                                    modifier = Modifier.fillMaxWidth(),
                                    data = data
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DottedText(value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(start = 4.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = "........................................................................................................................................................",
            fontSize = 13.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier.alpha(0.6f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@Composable
fun AdaptiveHeader(isTablet: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Photo box
        Box(
            modifier = Modifier
                .size(width = 110.dp, height = 150.dp)
                .border(1.dp, Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Dán ảnh", fontSize = 12.sp, textAlign = TextAlign.Center)
                Text("3x4", fontSize = 12.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Đóng dấu", fontSize = 11.sp, textAlign = TextAlign.Center)
                Text("giáp lai", fontSize = 11.sp, textAlign = TextAlign.Center)
                Text("cơ sở đào tạo", fontSize = 11.sp, textAlign = TextAlign.Center)
                Text("(1)", fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        }

        // Country Info
        Column(
            modifier = Modifier.weight(1f).padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM",
                fontWeight = FontWeight.Bold,
                fontSize = if (isTablet) 16.sp else 14.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Độc lập – Tự do – Hạnh phúc",
                fontWeight = FontWeight.Bold,
                fontSize = if (isTablet) 15.sp else 13.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(1.dp)
                    .border(0.5.dp, Color.Black)
            )
        }
    }
}

@Composable
fun SignatureColumn(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    data: FormFields? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (data != null) {
            if (data.signatureBitmap != null) {
                Image(
                    bitmap = data.signatureBitmap.asImageBitmap(),
                    contentDescription = "Signature",
                    modifier = Modifier
                        .height(70.dp)
                        .width(120.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Spacer(modifier = Modifier.height(70.dp))
            }
            Text(
                text = data.fullName.ifEmpty { "................" },
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        } else {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun FormLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = Color.Black
    )
}


@Preview(name = "Phone", showBackground = true, showSystemUi = true)
@Composable
fun ElectronicFormViewPreview() {
    val sampleData = FormFields(
        fullName = "NGUYỄN VĂN A",
        gender = "Nam",
        dob = "10/12/2000",
        idNumber = "012345678911",
        idIssueDate = "31/10/2021",
        idIssuePlace = "Cục Cảnh sát",
        permanentAddress = "Xóm Củ Cải, xã Vườn Rau, tỉnh Nông Nghiệp",
        phoneNumber = "0123456789",
        email = "test@gmail.com",
        emergencyContact = "Mẹ - 0987654321",
        schoolName = "Đại học Bách Khoa Hà Nội",
        academicYear = "2020-2025",
        className = "CNTT-01",
        department = "Công nghệ thông tin",
        studentId = "20201234",
        priorityType = "Con thương binh",
        dormName = "A1",
        duration = "10 tháng"
    )
    MaterialTheme {
        ElectronicFormView(sampleData, onBack = {})
    }
}

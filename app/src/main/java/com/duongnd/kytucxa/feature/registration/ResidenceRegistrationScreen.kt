package com.duongnd.kytucxa.feature.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.duongnd.kytucxa.core.ui.components.KTXButton
import com.duongnd.kytucxa.core.utils.DateUtils
import com.duongnd.kytucxa.core.utils.GenderUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidenceRegistrationScreen(
    viewModel: RegistrationViewModel = hiltViewModel(),
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val savedFields by viewModel.residenceRegistrationFields.collectAsState()

    // State variables matching the form image
    var receiver by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var ownerRelation by remember { mutableStateOf("") }
    var ownerIdNumber by remember { mutableStateOf("") }
    var requestContent by remember { mutableStateOf("Đăng ký tạm trú tại ký túc xá") }

    var showPreview by remember { mutableStateOf(false) }

    // Pre-fill data from ViewModel/Session
    LaunchedEffect(currentUser, savedFields) {
        if (savedFields != null) {
            receiver = savedFields!!.receiver
            fullName = savedFields!!.fullName
            dob = savedFields!!.dob
            gender = savedFields!!.gender
            idNumber = savedFields!!.idNumber
            phoneNumber = savedFields!!.phoneNumber
            email = savedFields!!.email
            ownerName = savedFields!!.ownerName
            ownerRelation = savedFields!!.ownerRelation
            ownerIdNumber = savedFields!!.ownerIdNumber
            requestContent = savedFields!!.requestContent
        } else {
            currentUser?.let { userProfile ->
                userProfile.user?.let { u ->
                    fullName = u.fullName ?: ""
                    dob = u.dateOfBirth ?: ""
                    gender = u.gender ?: ""
                    idNumber = u.identityCard ?: ""
                    phoneNumber = u.phoneNumber ?: ""
                    email = u.email ?: ""
                }
            }
        }
    }

    val primaryColor = Color(0xFF0047BB)

    if (showPreview) {
        HtmlPreviewScreen(
            onBack = { showPreview = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Tờ khai cư trú", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showPreview = true }) {
                            Icon(Icons.Rounded.Visibility, contentDescription = "Xem trước")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        "Độc lập - Tự do - Hạnh phúc",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(
                        modifier = Modifier.width(160.dp),
                        thickness = 1.dp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "TỜ KHAI THAY ĐỔI THÔNG TIN CƯ TRÚ",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = primaryColor,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Form Fields
                OutlinedTextField(
                    value = receiver,
                    onValueChange = { receiver = it },
                    label = { Text("Kính gửi") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Họ, chữ đệm và tên") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = DateUtils.formatString(dob),
                        onValueChange = { dob = it },
                        label = { Text("Ngày sinh") },
                        modifier = Modifier
                            .weight(1.5f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = GenderUtils.getGenderDisplay(gender),
                        onValueChange = { gender = it },
                        label = { Text("Giới tính") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // CCCD Input - OTP Style
                CccdOtpInputField(
                    value = idNumber,
                    onValueChange = { if (it.length <= 12) idNumber = it },
                    label = "Số định danh cá nhân (CCCD)"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Số điện thoại") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("Tên chủ hộ") },
                        modifier = Modifier
                            .weight(1.2f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = ownerRelation,
                        onValueChange = { ownerRelation = it },
                        label = { Text("Quan hệ") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Owner CCCD Input - OTP Style
                CccdOtpInputField(
                    value = ownerIdNumber,
                    onValueChange = { if (it.length <= 12) ownerIdNumber = it },
                    label = "Số định danh cá nhân của chủ hộ"
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = requestContent,
                    onValueChange = { requestContent = it },
                    label = { Text("Nội dung đề nghị") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )


                Spacer(modifier = Modifier.height(32.dp))

                KTXButton(
                    text = "TIẾP TỤC",
                    onClick = {
                        val fields = com.duongnd.kytucxa.domain.models.ResidenceRegistrationFields(
                            receiver, fullName, dob, gender, idNumber, phoneNumber,
                            email, ownerName, ownerRelation, ownerIdNumber, requestContent
                        )
                        viewModel.updateResidenceRegistrationFields(fields)
                        onNext()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = primaryColor,
                    enabled = fullName.isNotBlank() && idNumber.length == 12
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CccdOtpInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    Column {
        Text(
            text = label,
//            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    onValueChange(it)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            decorationBox = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(12) { index ->
                        val char = when {
                            index >= value.length -> ""
                            else -> value[index].toString()
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.7f)
                                .border(
                                    width = 1.dp,
                                    color = if (index == value.length) Color(0xFF0047BB) else Color.LightGray,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .background(
                                    if (index == value.length) Color(0xFF0047BB).copy(alpha = 0.05f)
                                    else Color.Transparent,
                                    RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        )
    }
}

package com.duongnd.kytucxa.feature.registration.temporary

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.duongnd.kytucxa.core.ui.components.StepIndicator
import com.duongnd.kytucxa.core.ui.components.CccdOtpInputField
import com.duongnd.kytucxa.core.ui.components.KTXButton
import com.duongnd.kytucxa.core.ui.components.KTXDialog
import com.duongnd.kytucxa.core.ui.components.KTXTextField
import com.duongnd.kytucxa.core.utils.DateUtils
import com.duongnd.kytucxa.core.utils.GenderUtils
import com.duongnd.kytucxa.core.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemporaryFormScreen(
    viewModel: TemporaryViewModel = hiltViewModel(),
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val updateResult by viewModel.updateResult.collectAsState()
    val temporary = uiState.temporary

    // Xử lý điều hướng khi cập nhật thành công
    LaunchedEffect(updateResult) {
        if (updateResult is Resource.Success) {
            onNext()
            viewModel.resetUpdateResult()
        }
    }

    val primaryColor = Color(0xFF0047BB)

    // Handle System Back Press
    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tờ khai cư trú", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            StepIndicator(currentStep = 2)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp)
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
                KTXTextField(
                    value = temporary.receiver,
                    onValueChange = { viewModel.updateTemporary { copy(receiver = it) } },
                    placeholder = "Kính gửi",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                KTXTextField(
                    value = temporary.fullName,
                    onValueChange = { viewModel.updateTemporary { copy(fullName = it) } },
                    placeholder = "Họ, chữ đệm và tên",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    KTXTextField(
                        value = DateUtils.formatString(temporary.dateOfBirth),
                        onValueChange = { viewModel.updateTemporary { copy(dateOfBirth = it) } },
                        placeholder = "Ngày sinh",
                        modifier = Modifier
                            .weight(1.5f)
                            .padding(end = 8.dp),
                        readOnly = true
                    )
                    KTXTextField(
                        value = GenderUtils.getGenderDisplay(temporary.gender),
                        onValueChange = { viewModel.updateTemporary { copy(gender = it) } },
                        placeholder = "Giới tính",
                        modifier = Modifier.weight(1f),
                        readOnly = true
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // CCCD Input - OTP Style
                CccdOtpInputField(
                    value = temporary.cccd,
                    onValueChange = {
                        if (it.length <= 12) viewModel.updateTemporary { copy(cccd = it) }
                    },
                    label = "Số định danh cá nhân (CCCD)"
                )

                Spacer(modifier = Modifier.height(16.dp))

                KTXTextField(
                    value = temporary.phoneNumber,
                    onValueChange = { viewModel.updateTemporary { copy(phoneNumber = it) } },
                    placeholder = "Số điện thoại",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(16.dp))
                KTXTextField(
                    value = temporary.email,
                    onValueChange = { viewModel.updateTemporary { copy(email = it) } },
                    placeholder = "Email",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    KTXTextField(
                        value = temporary.ownerName ?: "",
                        onValueChange = { viewModel.updateTemporary { copy(ownerName = it) } },
                        placeholder = "Tên chủ hộ",
                        modifier = Modifier
                            .weight(1.2f)
                            .padding(end = 8.dp)
                    )
                    KTXTextField(
                        value = temporary.ownerRelation ?: "",
                        onValueChange = { viewModel.updateTemporary { copy(ownerRelation = it) } },
                        placeholder = "Quan hệ",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Owner CCCD Input - OTP Style
                CccdOtpInputField(
                    value = temporary.ownerCccd ?: "",
                    onValueChange = {
                        if (it.length <= 12) viewModel.updateTemporary {
                            copy(
                                ownerCccd = it
                            )
                        }
                    },
                    label = "Số định danh cá nhân của chủ hộ"
                )

                Spacer(modifier = Modifier.height(16.dp))

                KTXTextField(
                    value = temporary.requestContent,
                    onValueChange = { viewModel.updateTemporary { copy(requestContent = it) } },
                    placeholder = "Nội dung đề nghị",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(32.dp))

                KTXButton(
                    text = "TIẾP TỤC",
                    onClick = { viewModel.submitForm() },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = primaryColor,
                    enabled = uiState.isFormValid && temporary.cccd.length == 12 && updateResult !is Resource.Loading,
                    isLoading = updateResult is Resource.Loading
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Hiển thị thông báo lỗi nếu xảy ra
    if (updateResult is Resource.Error) {
        KTXDialog(
            onDismissRequest = { viewModel.resetUpdateResult() },
            title = "Lỗi cập nhật",
            description = {
                Text(
                    text = (updateResult as Resource.Error).message
                        ?: "Đã xảy ra lỗi không xác định. Vui lòng thử lại sau.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButtonText = "ĐÓNG",
            onConfirm = { viewModel.resetUpdateResult() }
        )
    }
}

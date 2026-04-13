package com.duongnd.kytucxa.feature.registration.residence

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.duongnd.kytucxa.core.ui.components.CccdOtpInputField
import com.duongnd.kytucxa.core.ui.components.KTXButton
import com.duongnd.kytucxa.core.ui.components.KTXTextField
import com.duongnd.kytucxa.core.utils.DateUtils
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.domain.models.FormFields
import com.duongnd.kytucxa.feature.auth.register.ElectronicFormView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidenceFormScreen(
    viewModel: ResidenceViewModel = hiltViewModel(),
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val updateResult by viewModel.updateResult.collectAsState()

    var showPreview by remember { mutableStateOf(false) }
    val primaryColor = Color(0xFF0047BB)

    // Xử lý chuyển trang khi submit thành công
    LaunchedEffect(updateResult) {
        if (updateResult is Resource.Success) {
            onNext()
        }
    }

    if (showPreview) {
        val res = uiState.residence
        ElectronicFormView(
            data = FormFields(
                fullName = res.fullName,
                gender = res.gender,
                dob = res.dateOfBirth,
                idNumber = res.cccd,
                idIssueDate = res.cccdIdIssueDate,
                idIssuePlace = res.cccdIdIssuePlace,
                permanentAddress = res.permanentAddress,
                phoneNumber = res.phoneNumber,
                email = res.email,
                emergencyContact = res.emergencyContact,
                schoolName = res.schoolName,
                academicYear = res.academicYear,
                className = res.className,
                department = res.department,
                studentId = res.studentId,
                priorityType = uiState.priorityType,
                dormName = res.dormName,
                duration = res.duration,
                signatureBitmap = uiState.signatureBitmap
            ),
            onBack = { showPreview = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Đơn đăng ký thuê nhà ở", fontWeight = FontWeight.Bold) },
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
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header: Quốc hiệu tiêu ngữ
                HeaderSection(primaryColor)

                Spacer(modifier = Modifier.height(24.dp))

                // Section I: THÔNG TIN CÁ NHÂN
                Text("I. THÔNG TIN CÁ NHÂN", fontWeight = FontWeight.Bold, color = primaryColor)
                Spacer(modifier = Modifier.height(16.dp))
                
                KTXTextField(
                    value = uiState.residence.fullName,
                    onValueChange = { newValue -> 
                        viewModel.updateResidence { copy(fullName = newValue) }
                    },
                    placeholder = "Họ và tên",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                GenderSelectionRow(
                    selectedGender = uiState.residence.gender,
                    onGenderSelected = { newValue ->
                        viewModel.updateResidence { copy(gender = newValue) }
                    }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                KTXTextField(
                    value = DateUtils.formatString(uiState.residence.dateOfBirth),
                    onValueChange = { newValue ->
                        viewModel.updateResidence { copy(dateOfBirth = newValue) }
                    },
                    placeholder = "Ngày sinh",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                CccdOtpInputField(
                    value = uiState.residence.cccd,
                    onValueChange = { newValue ->
                        viewModel.updateResidence { copy(cccd = newValue) }
                    },
                    label = "Số CCCD"
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    KTXTextField(
                        value = uiState.residence.cccdIdIssueDate,
                        onValueChange = { newValue ->
                            viewModel.updateResidence { copy(cccdIdIssueDate = newValue) }
                        },
                        placeholder = "Ngày cấp",
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    KTXTextField(
                        value = uiState.residence.cccdIdIssuePlace,
                        onValueChange = { newValue ->
                            viewModel.updateResidence { copy(cccdIdIssuePlace = newValue) }
                        },
                        placeholder = "Nơi cấp",
                        modifier = Modifier.weight(1.5f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                KTXTextField(
                    value = uiState.residence.permanentAddress,
                    onValueChange = { newValue ->
                        viewModel.updateResidence { copy(permanentAddress = newValue) }
                    },
                    placeholder = "Hộ khẩu thường trú",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                KTXTextField(
                    value = uiState.residence.phoneNumber,
                    onValueChange = { newValue ->
                        viewModel.updateResidence { copy(phoneNumber = newValue) }
                    },
                    placeholder = "Điện thoại",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                KTXTextField(
                    value = uiState.residence.email,
                    onValueChange = { newValue ->
                        viewModel.updateResidence { copy(email = newValue) }
                    },
                    placeholder = "Email",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                KTXTextField(
                    value = uiState.residence.emergencyContact,
                    onValueChange = { newValue ->
                        viewModel.updateResidence { copy(emergencyContact = newValue) }
                    },
                    placeholder = "Liên hệ báo tin",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section II: THÔNG TIN ĐÀO TẠO
                Text("II. THÔNG TIN ĐÀO TẠO", fontWeight = FontWeight.Bold, color = primaryColor)
                Spacer(modifier = Modifier.height(16.dp))
                
                KTXTextField(
                    value = uiState.residence.schoolName,
                    onValueChange = { newValue ->
                        viewModel.updateResidence { copy(schoolName = newValue) }
                    },
                    placeholder = "Cơ sở đào tạo",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    KTXTextField(
                        value = uiState.residence.academicYear,
                        onValueChange = { newValue ->
                            viewModel.updateResidence { copy(academicYear = newValue) }
                        },
                        placeholder = "Niên khóa",
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    KTXTextField(
                        value = uiState.residence.className,
                        onValueChange = { newValue ->
                            viewModel.updateResidence { copy(className = newValue) }
                        },
                        placeholder = "Lớp",
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                KTXTextField(
                    value = uiState.residence.department,
                    onValueChange = { newValue ->
                        viewModel.updateResidence { copy(department = newValue) }
                    },
                    placeholder = "Khoa",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                KTXTextField(
                    value = uiState.residence.major,
                    onValueChange = { newValue ->
                        viewModel.updateResidence { copy(major = newValue) }
                    },
                    placeholder = "Chuyên ngành",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                KTXTextField(
                    value = uiState.residence.studentId,
                    onValueChange = { newValue ->
                        viewModel.updateResidence { copy(studentId = newValue) }
                    },
                    placeholder = "Mã sinh viên",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                KTXTextField(
                    value = uiState.priorityType,
                    onValueChange = { viewModel.updatePriorityType(it) },
                    placeholder = "Đối tượng ưu tiên",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section III: NỘI DUNG ĐỀ NGHỊ
                Text("III. NỘI DUNG ĐỀ NGHỊ", fontWeight = FontWeight.Bold, color = primaryColor)
                Spacer(modifier = Modifier.height(16.dp))
                
                KTXTextField(
                    value = uiState.residence.dormName,
                    onValueChange = { newValue ->
                        viewModel.updateResidence { copy(dormName = newValue) }
                    },
                    placeholder = "Đăng ký tại KTX",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                KTXTextField(
                    value = uiState.residence.duration,
                    onValueChange = { newValue ->
                        viewModel.updateResidence { copy(duration = newValue) }
                    },
                    placeholder = "Thời gian thuê (Số tháng)",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Submit Button
                KTXButton(
                    text = "TIẾP TỤC",
                    onClick = {
                        viewModel.submitForm()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = primaryColor,
                    enabled = uiState.isFormValid && updateResult !is Resource.Loading,
                    isLoading = updateResult is Resource.Loading
                )
                
                if (updateResult is Resource.Error) {
                    Text(
                        text = (updateResult as Resource.Error).message ?: "Có lỗi xảy ra",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HeaderSection(primaryColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text("Độc lập - Tự do - Hạnh phúc", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(modifier = Modifier.width(160.dp), thickness = 1.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "ĐƠN ĐĂNG KÝ THUÊ NHÀ Ở SINH VIÊN",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = primaryColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GenderSelectionRow(selectedGender: String, onGenderSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Giới tính", fontWeight = FontWeight.Medium, modifier = Modifier.padding(8.dp).weight(1f))
        FilterChip(
            selected = selectedGender == "male",
            onClick = { onGenderSelected("male") },
            label = { Text("Nam") },
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        )
        FilterChip(
            selected = selectedGender == "female",
            onClick = { onGenderSelected("female") },
            label = { Text("Nữ") },
            modifier = Modifier.weight(1f)
        )
    }
}

package com.duongnd.kytucxa.feature.registration.residence

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.duongnd.kytucxa.core.ui.components.KTXButton
import com.duongnd.kytucxa.core.utils.DateUtils
import com.duongnd.kytucxa.data.remote.dto.auth.me.CurrentUser
import com.duongnd.kytucxa.domain.models.FormFields
import com.duongnd.kytucxa.feature.auth.register.ElectronicFormView
import com.duongnd.kytucxa.feature.registration.CccdOtpInputField
import com.duongnd.kytucxa.feature.registration.RegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidenceFormScreen(
    viewModel: RegistrationViewModel = hiltViewModel(),
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val savedFormFields by viewModel.formFields.collectAsState()

    ResidenceFormScreenContent(
        currentUser = currentUser,
        savedFormFields = savedFormFields,
        onSaveAndContinue = { fields ->
            viewModel.updateFormFields(fields)
            onNext()
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidenceFormScreenContent(
    currentUser: CurrentUser?,
    savedFormFields: FormFields?,
    onSaveAndContinue: (FormFields) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // State variables
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var idIssueDate by remember { mutableStateOf("") }
    var idIssuePlace by remember { mutableStateOf("") }
    var permanentAddress by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var schoolName by remember { mutableStateOf("") }
    var academicYear by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var priorityType by remember { mutableStateOf("") }
    var dormName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    // Pre-fill data from session OR saved state
    LaunchedEffect(currentUser, savedFormFields) {
        if (savedFormFields != null) {
            // Restore from ViewModel state
            val f = savedFormFields!!
            fullName = f.fullName
            gender = f.gender
            dob = f.dob
            idNumber = f.idNumber
            idIssueDate = f.idIssueDate
            idIssuePlace = f.idIssuePlace
            permanentAddress = f.permanentAddress
            phoneNumber = f.phoneNumber
            email = f.email
            emergencyContact = f.emergencyContact
            schoolName = f.schoolName
            academicYear = f.academicYear
            className = f.className
            department = f.department
            studentId = f.studentId
            priorityType = f.priorityType
            dormName = f.dormName
            duration = f.duration
        } else {
            // Initial fill from session
            currentUser?.let { userProfile ->
                userProfile.user?.let { u ->
                    fullName = u.fullName ?: ""
                    gender = u.gender ?: ""
                    dob = u.dateOfBirth ?: ""
                    idNumber = u.identityCard ?: ""
                    phoneNumber = u.phoneNumber ?: ""
                    email = u.email ?: ""
                }
                userProfile.student?.let { s ->
                    schoolName = s.university ?: ""
                    academicYear = s.academicYear ?: ""
                    className = s.className ?: ""
                    department = s.major ?: ""
                    studentId = s.studentId ?: ""
                }
            }
        }
    }

    var showPreview by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFF0047BB)

    if (showPreview) {
        ElectronicFormView(
            data = FormFields(
                fullName, gender, dob, idNumber, idIssueDate, idIssuePlace,
                permanentAddress, phoneNumber, email, emergencyContact,
                schoolName, academicYear, className, department, studentId,
                priorityType, dormName, duration
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

                Spacer(modifier = Modifier.height(24.dp))

                // Section I
                Text("I. THÔNG TIN CÁ NHÂN", fontWeight = FontWeight.Bold, color = primaryColor)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Giới tính",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                    )
                    FilterChip(
                        selected = gender == "male",
                        onClick = { gender = "male" },
                        label = { Text("Nam") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    FilterChip(
                        selected = gender == "female",
                        onClick = { gender = "female" },
                        label = { Text("Nữ") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = DateUtils.formatString(dob),
                    onValueChange = { dob = it },
                    label = { Text("Ngày sinh") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                // CCCD Input - OTP Style
                CccdOtpInputField(
                    value = idNumber,
                    onValueChange = { if (it.length <= 12) idNumber = it },
                    label = "Số CCCD"
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = idIssueDate,
                        onValueChange = { idIssueDate = it },
                        label = { Text("Ngày cấp") },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = idIssuePlace,
                        onValueChange = { idIssuePlace = it },
                        label = { Text("Nơi cấp") },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = permanentAddress,
                    onValueChange = { permanentAddress = it },
                    label = { Text("Hộ khẩu thường trú") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Điện thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    label = { Text("Liên hệ báo tin") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section II
                Text("II. THÔNG TIN ĐÀO TẠO", fontWeight = FontWeight.Bold, color = primaryColor)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = schoolName,
                    onValueChange = { schoolName = it },
                    label = { Text("Cơ sở đào tạo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = academicYear,
                        onValueChange = { academicYear = it },
                        label = { Text("Niên khóa") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        label = { Text("Lớp") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Khoa") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("Mã sinh viên") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = priorityType,
                    onValueChange = { priorityType = it },
                    label = { Text("Đối tượng ưu tiên") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section III
                Text("III. NỘI DUNG ĐỀ NGHỊ", fontWeight = FontWeight.Bold, color = primaryColor)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = dormName,
                    onValueChange = { dormName = it },
                    label = { Text("Đăng ký tại KTX") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Thời gian thuê (Số tháng)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Sử dụng KTXButton và điều khiển trạng thái bằng tham số 'enabled'
                KTXButton(
                    text = "TIẾP TỤC",
                    onClick = {
                        val fields = FormFields(
                            fullName, gender, dob, idNumber, idIssueDate, idIssuePlace,
                            permanentAddress, phoneNumber, email, emergencyContact,
                            schoolName, academicYear, className, department, studentId,
                            priorityType, dormName, duration
                        )
                        onSaveAndContinue(fields)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = primaryColor,
                    enabled = fullName.isNotBlank() && idNumber.length == 12 && studentId.isNotBlank()
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResidenceFormPreview(){
    ResidenceFormScreenContent(
        currentUser = null,
        savedFormFields = null,
        onSaveAndContinue = {},
        onBack = {}
    )
}

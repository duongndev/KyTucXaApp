package com.duongnd.kytucxa.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AssignmentInd
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ContactPage
import androidx.compose.material.icons.rounded.Domain
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.duongnd.kytucxa.core.ui.components.GenderOption
import com.duongnd.kytucxa.core.ui.components.KTXButton
import com.duongnd.kytucxa.core.ui.components.KTXTextField
import com.duongnd.kytucxa.core.utils.ValidateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    onBack: () -> Unit,
    onNavigateToSubmissionMethod: () -> Unit = {},
    viewModel: UpdateProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    val primaryColor = Color(0xFF0061FF)
    val secondaryColor = Color(0xFF60EFFF)
    val backgroundColor = Color(0xFFF8FAFC)

    val interactionSource = remember { MutableInteractionSource() }

    // Sử dụng hàm validate mới từ ValidateUtils
    val cccdValidation = remember(state.identityCard) {
        if (state.identityCard.isEmpty()) {
            ValidateUtils.ValidationResult(true, "")
        } else {
            ValidateUtils.validateCCCD(state.identityCard)
        }
    }

    // Kiểm tra tính hợp lệ của form
    val isFormValid by remember(state, cccdValidation) {
        derivedStateOf {
            state.fullName.isNotBlank() &&
                    state.phoneNumber.isNotBlank() &&
                    cccdValidation.isValid && state.identityCard.isNotEmpty() && // CCCD phải hợp lệ và không trống
                    state.dateOfBirth.isNotBlank() &&
                    state.gender.isNotBlank() &&
                    state.university.isNotBlank() &&
                    state.studentId.isNotBlank() &&
                    state.major.isNotBlank()
        }
    }

    // State for Dialogs
    var showMissingInfoDialog by remember { mutableStateOf(false) }
    var showUnverifiedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.currentUser) {
        state.currentUser?.let { currentUser ->
            val user = currentUser.user
            val student = currentUser.student

            val isPersonalInfoIncomplete = user == null ||
                    user.fullName.isNullOrEmpty() ||
                    user.phoneNumber.isNullOrEmpty() ||
                    user.identityCard.isNullOrEmpty() ||
                    user.dateOfBirth.isNullOrEmpty() ||
                    user.gender.isNullOrEmpty()

            val isStudentInfoIncomplete = student == null ||
                    student.studentId.isNullOrEmpty() ||
                    student.university.isNullOrEmpty() ||
                    student.major.isNullOrEmpty()

            if (isPersonalInfoIncomplete || isStudentInfoIncomplete) {
                showMissingInfoDialog = true
                showUnverifiedDialog = false
            } else {
                val isVerified = user?.isAccountVerified ?: false
                if (!isVerified) {
                    showUnverifiedDialog = true
                    showMissingInfoDialog = false
                }
            }
        }
    }

    // State for DatePicker
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = remember(state.dateOfBirth) {
            try {
                if (state.dateOfBirth.isNotEmpty()) {
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).parse(state.dateOfBirth)?.time
                } else null
            } catch (e: Exception) {
                null
            }
        }
    )

    LaunchedEffect(state.isUpdateSuccess) {
        if (state.isUpdateSuccess) {
            onBack()
            viewModel.resetUpdateSuccess()
        }
    }

    // --- DIALOGS ---
    if (showMissingInfoDialog) {
        AlertDialog(
            onDismissRequest = { showMissingInfoDialog = false },
            icon = {
                Icon(
                    Icons.Rounded.AssignmentInd,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text(text = "Bổ sung thông tin", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Hồ sơ của bạn hiện đang thiếu các thông tin cá nhân và sinh viên cần thiết. Vui lòng cập nhật đầy đủ trước khi thực hiện nộp hồ sơ đăng ký nội trú.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { showMissingInfoDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("TÔI ĐÃ HIỂU")
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    if (showUnverifiedDialog && !showMissingInfoDialog) {
        AlertDialog(
            onDismissRequest = { showUnverifiedDialog = false },
            icon = {
                Icon(
                    Icons.Rounded.VerifiedUser,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text(text = "Tài khoản chưa kích hoạt", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Thông tin của bạn đã đầy đủ. Tuy nhiên, tài khoản cần được xác thực bởi Ban quản lý. Vui lòng nộp hồ sơ đăng ký nội trú để được xét duyệt kích hoạt tài khoản.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                KTXButton(
                    text = "NỘP HỒ SƠ NGAY",
                    onClick = {
                        showUnverifiedDialog = false
                        onNavigateToSubmissionMethod()
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showUnverifiedDialog = false }) {
                    Text("ĐỂ SAU", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        viewModel.onDateOfBirthChange(formatter.format(date))
                    }
                    showDatePicker = false
                }) {
                    Text("Xác nhận", color = primaryColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Hủy", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                focusManager.clearFocus()
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor, secondaryColor.copy(alpha = 0.7f))
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Thông tin cá nhân",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White.copy(
                                    alpha = 0.2f
                                )
                            )
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Section
                    Box(
                        modifier = Modifier
                            .padding(top = 20.dp, bottom = 24.dp)
                            .size(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 8.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .background(Color(0xFFF1F5F9), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = primaryColor.copy(alpha = 0.4f)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(34.dp)
                                .clickable { /* logic đổi ảnh */ },
                            shape = CircleShape,
                            color = primaryColor,
                            shadowElevation = 4.dp
                        ) {
                            Icon(
                                Icons.Rounded.CameraAlt,
                                null,
                                modifier = Modifier.padding(8.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        ProfileInfoCard(
                            title = "Thông tin cơ bản",
                            icon = Icons.Rounded.AccountCircle
                        ) {
                            KTXTextField(
                                value = state.fullName,
                                onValueChange = viewModel::onFullNameChange,
                                placeholder = "Họ và tên",
                                leadingIcon = Icons.Rounded.Person,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )

                            KTXTextField(
                                value = state.email,
                                onValueChange = {},
                                placeholder = "Email",
                                leadingIcon = Icons.Rounded.Email,
                                enabled = false,
                                readOnly = true
                            )

                            KTXTextField(
                                value = state.phoneNumber,
                                onValueChange = viewModel::onPhoneNumberChange,
                                placeholder = "Số điện thoại",
                                leadingIcon = Icons.Rounded.Phone,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next
                                )
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                KTXTextField(
                                    value = state.identityCard,
                                    onValueChange = {
                                        if (it.length <= 12) viewModel.onIdentityCardChange(
                                            it
                                        )
                                    },
                                    placeholder = "Số CCCD/CMND",
                                    leadingIcon = Icons.Rounded.ContactPage,
                                    isError = !cccdValidation.isValid,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    )
                                )
                                if (!cccdValidation.isValid) {
                                    Text(
                                        text = cccdValidation.message,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth()) {
                                KTXTextField(
                                    value = state.dateOfBirth,
                                    onValueChange = {},
                                    placeholder = "Ngày sinh",
                                    leadingIcon = Icons.Rounded.CalendarMonth,
                                    readOnly = true,
                                    enabled = false
                                )
                                Box(modifier = Modifier
                                    .matchParentSize()
                                    .clickable {
                                        focusManager.clearFocus()
                                        showDatePicker = true
                                    })
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Giới tính",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    GenderOption(
                                        label = "Nam",
                                        icon = Icons.Rounded.Male,
                                        isSelected = state.gender == "male",
                                        onClick = {
                                            focusManager.clearFocus()
                                            viewModel.onGenderChange("male")
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    GenderOption(
                                        label = "Nữ",
                                        icon = Icons.Rounded.Female,
                                        isSelected = state.gender == "female",
                                        onClick = {
                                            focusManager.clearFocus()
                                            viewModel.onGenderChange("female")
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    GenderOption(
                                        label = "Khác",
                                        icon = Icons.Rounded.Female,
                                        isSelected = state.gender == "other",
                                        onClick = {
                                            focusManager.clearFocus()
                                            viewModel.onGenderChange("other")
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        ProfileInfoCard(
                            title = "Thông tin sinh viên",
                            icon = Icons.Rounded.School
                        ) {
                            KTXTextField(
                                value = state.university,
                                onValueChange = viewModel::onUniversityChange,
                                placeholder = "Trường đại học",
                                leadingIcon = Icons.Rounded.Domain,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )

                            KTXTextField(
                                value = state.studentId,
                                onValueChange = viewModel::onStudentIdChange,
                                placeholder = "Mã số sinh viên",
                                leadingIcon = Icons.Rounded.Badge,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                )
                            )

                            KTXTextField(
                                value = state.major,
                                onValueChange = viewModel::onMajorChange,
                                placeholder = "Ngành học",
                                leadingIcon = Icons.Rounded.AutoStories,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    KTXTextField(
                                        value = state.className,
                                        onValueChange = viewModel::onClassNameChange,
                                        placeholder = "Lớp",
                                        leadingIcon = Icons.Rounded.Groups,
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    KTXTextField(
                                        value = state.academicYear,
                                        onValueChange = viewModel::onAcademicYearChange,
                                        placeholder = "Niên khóa",
                                        leadingIcon = Icons.Rounded.History,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Done
                                        )
                                    )
                                }
                            }
                        }

                        KTXButton(
                            text = "Cập nhật ngay",
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.updateProfile()
                            },
                            isLoading = state.isLoading,
                            enabled = isFormValid,
                            containerColor = primaryColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(top = 8.dp)
                        )

                        if (state.error != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = state.error!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileInfoCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF0061FF),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )
            }
            content()
        }
    }
}

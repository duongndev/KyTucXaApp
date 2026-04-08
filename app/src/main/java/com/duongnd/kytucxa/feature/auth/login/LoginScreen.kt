package com.duongnd.kytucxa.feature.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.duongnd.kytucxa.core.ui.components.KTXButton
import com.duongnd.kytucxa.core.ui.components.KTXTextField

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToCompleteProfile: () -> Unit = {},
    onNavigateToRegistration: () -> Unit = {},
    onNavigateToVerify: (String) -> Unit = {},
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val loginUiState by loginViewModel.loginState.collectAsState()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showUpdateProfileDialog by remember { mutableStateOf(false) }
    var showRegistrationDialog by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary

    // Lắng nghe trạng thái lỗi từ ViewModel để hiển thị Dialog
    LaunchedEffect(loginUiState.errorMessage) {
        if (loginUiState.errorMessage != null) {
            showErrorDialog = true
        }
    }

    // Lắng nghe trạng thái đăng nhập thành công
    LaunchedEffect(loginUiState.isLoginSuccess) {
        if (loginUiState.isLoginSuccess) {
            val user = loginUiState.user
            val student = loginUiState.student

            if (user != null) {
                // 1. Kiểm tra thông tin cá nhân đầy đủ
                val isPersonalInfoComplete = !user.fullName.isNullOrBlank() &&
                        !user.identityCard.isNullOrBlank() &&
                        !user.phoneNumber.isNullOrBlank() &&
                        !user.gender.isNullOrBlank() &&
                        !user.dateOfBirth.isNullOrBlank()

                // 2. Kiểm tra thông tin sinh viên đầy đủ
                val isStudentInfoComplete = student != null &&
                        !student.university.isNullOrBlank() &&
                        !student.studentId.isNullOrBlank() &&
                        !student.major.isNullOrBlank() &&
                        !student.className.isNullOrBlank() &&
                        !student.academicYear.isNullOrBlank()

                // 3. Kiểm tra trạng thái xác thực tài khoản
                val isAccountVerified = user.isAccountVerified

                when {
                    !isPersonalInfoComplete || !isStudentInfoComplete -> {
                        showUpdateProfileDialog = true
                    }
                    !isAccountVerified -> {
                        showRegistrationDialog = true
                    }
                    else -> {
                        onLoginSuccess()
                        loginViewModel.clearMessages()
                    }
                }
            }
        }
    }

    // --- DIALOGS ---

    // 1. Dialog hiển thị lỗi (Thông thường hoặc Chưa xác thực OTP)
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { 
                showErrorDialog = false
                loginViewModel.clearMessages()
            },
            icon = { 
                val icon = if (!loginUiState.isEmailVerified) Icons.Rounded.VerifiedUser else Icons.Rounded.ErrorOutline
                val iconColor = if (!loginUiState.isEmailVerified) primaryColor else MaterialTheme.colorScheme.error
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(44.dp)) 
            },
            title = { 
                Text(
                    text = if (!loginUiState.isEmailVerified) "Xác thực tài khoản" else "Đăng nhập thất bại", 
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                ) 
            },
            text = { 
                Text(
                    text = loginUiState.errorMessage ?: "Đã xảy ra lỗi không xác định. Vui lòng thử lại sau.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                ) 
            },
            confirmButton = {
                if (!loginUiState.isEmailVerified) {
                    Button(
                        onClick = {
                            showErrorDialog = false
                            loginViewModel.clearMessages()
                            onNavigateToVerify(email)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("XÁC THỰC NGAY")
                    }
                } else {
                    Button(
                        onClick = {
                            showErrorDialog = false
                            loginViewModel.clearMessages()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ĐÓNG")
                    }
                }
            },
            dismissButton = if (!loginUiState.isEmailVerified) {
                {
                    TextButton(onClick = { 
                        showErrorDialog = false
                        loginViewModel.clearMessages()
                    }) {
                        Text("ĐỂ SAU", color = Color.Gray)
                    }
                }
            } else null,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // 2. Dialog yêu cầu hoàn thiện hồ sơ (Thiếu thông tin)
    if (showUpdateProfileDialog) {
        AlertDialog(
            onDismissRequest = { 
                showUpdateProfileDialog = false
                loginViewModel.clearMessages()
            },
            icon = { Icon(Icons.Rounded.Info, contentDescription = null, tint = primaryColor, modifier = Modifier.size(48.dp)) },
            title = { Text(text = "Hoàn thiện hồ sơ", fontWeight = FontWeight.ExtraBold) },
            text = { Text(text = "Thông tin cá nhân hoặc thông tin sinh viên của bạn còn thiếu. Vui lòng cập nhật để tiếp tục.") },
            confirmButton = {
                KTXButton(
                    text = "CẬP NHẬT NGAY",
                    onClick = {
                        showUpdateProfileDialog = false
                        loginViewModel.clearMessages()
                        onNavigateToCompleteProfile()
                    }
                )
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    // 3. Dialog yêu cầu nộp hồ sơ (Đã đủ thông tin nhưng chưa xác thực)
    if (showRegistrationDialog) {
        AlertDialog(
            onDismissRequest = { 
                showRegistrationDialog = false
                loginViewModel.clearMessages()
            },
            icon = { Icon(Icons.Rounded.Assignment, contentDescription = null, tint = primaryColor, modifier = Modifier.size(48.dp)) },
            title = { Text(text = "Nộp hồ sơ đăng ký", fontWeight = FontWeight.ExtraBold) },
            text = { Text(text = "Thông tin của bạn đã đầy đủ. Vui lòng hoàn tất nộp hồ sơ để Ban quản lý xét duyệt phòng.") },
            confirmButton = {
                KTXButton(
                    text = "NỘP HỒ SƠ NGAY",
                    onClick = {
                        showRegistrationDialog = false
                        loginViewModel.clearMessages()
                        onNavigateToRegistration()
                    }
                )
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    // --- MAIN UI ---
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor, MaterialTheme.colorScheme.surface)
                )
            )
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth
        val formWidth = if (screenWidth > 600.dp) 480.dp else screenWidth

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(screenHeight * 0.08f))
            
            // Header: Logo & Branding
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apartment,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "KTX SINH VIÊN",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Chào mừng bạn quay trở lại!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Login Card
            Card(
                modifier = Modifier
                    .width(formWidth)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ĐĂNG NHẬP",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = primaryColor
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    // Input Email
                    KTXTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Email hoặc MSSV",
                        leadingIcon = Icons.Rounded.Person,
                        isError = loginUiState.emailError != null,
                        enabled = !loginUiState.isLoading,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )
                    loginUiState.emailError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth().padding(start = 12.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Input Password
                    KTXTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Mật khẩu",
                        leadingIcon = Icons.Rounded.Lock,
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                loginViewModel.loginViewModel(email, password)
                            }
                        ),
                        isError = loginUiState.passwordError != null,
                        enabled = !loginUiState.isLoading
                    )
                    loginUiState.passwordError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth().padding(start = 12.dp, top = 4.dp)
                        )
                    }

                    // Forgot Password
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(onClick = { /* Action Quên mật khẩu */ }) {
                            Text(
                                text = "Quên mật khẩu?",
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Login Button
                    KTXButton(
                        text = "ĐĂNG NHẬP",
                        onClick = { 
                            focusManager.clearFocus()
                            loginViewModel.loginViewModel(email, password)
                        },
                        isLoading = loginUiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Register Footer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Bạn chưa có tài khoản? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Đăng ký ngay",
                    color = primaryColor,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}

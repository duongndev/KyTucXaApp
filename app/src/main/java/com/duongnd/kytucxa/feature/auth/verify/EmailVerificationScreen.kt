package com.duongnd.kytucxa.feature.auth.verify

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.duongnd.kytucxa.core.ui.components.KTXButton

@Composable
fun EmailVerificationScreen(
    email: String,
    onVerificationSuccess: () -> Unit,
    viewModel: VerifyEmailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary

    // Xử lý thông báo lỗi hoặc thành công
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
        if (uiState.isResendSuccess) {
            uiState.successMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }
    }

    if (uiState.isVerifySuccess) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp)) },
            title = { Text(text = "Xác thực thành công", fontWeight = FontWeight.Bold) },
            text = { Text(text = uiState.successMessage ?: "Tài khoản của bạn đã được xác thực thành công.") },
            confirmButton = {
                TextButton(onClick = onVerificationSuccess) {
                    Text("ĐĂNG NHẬP NGAY", fontWeight = FontWeight.Bold, color = primaryColor)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor, Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MarkEmailRead,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "XÁC THỰC EMAIL",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Chúng tôi đã gửi mã xác thực đến\n$email",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 450.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nhập mã xác thực",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    OtpInputField(
                        modifier = Modifier.widthIn(max = 320.dp),
                        otpText = uiState.otp,
                        onOtpTextChange = { viewModel.onOtpChange(it) },
                        isError = uiState.otpError != null
                    )

                    if (uiState.otpError != null) {
                        Text(
                            text = uiState.otpError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    KTXButton(
                        text = "XÁC NHẬN",
                        onClick = { viewModel.verifyOtp() },
                        isLoading = uiState.isLoading,
                        enabled = uiState.otp.length == 6,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Không nhận được mã? ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        if (uiState.canResend) {
                            TextButton(
                                onClick = { viewModel.resendOtp() },
                                enabled = !uiState.isLoading,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "Gửi lại",
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else {
                            Text(
                                text = "Gửi lại sau ${uiState.timerCount}s",
                                style = MaterialTheme.typography.bodySmall,
                                color = primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OtpInputField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String) -> Unit,
    isError: Boolean = false
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = otpText,
        onValueChange = {
            if (it.length <= otpCount && it.all { char -> char.isDigit() }) {
                onOtpTextChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                repeat(otpCount) { index ->
                    val char = when {
                        index >= otpText.length -> ""
                        else -> otpText[index].toString()
                    }
                    val isFocused = otpText.length == index

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = when {
                                    isError -> MaterialTheme.colorScheme.error
                                    isFocused -> MaterialTheme.colorScheme.primary
                                    else -> Color.LightGray.copy(alpha = 0.6f)
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                color = if (isFocused) Color.White else Color(0xFFF8F9FA),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = when {
                                isError -> MaterialTheme.colorScheme.error
                                isFocused -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurface
                            },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}

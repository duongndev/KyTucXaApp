package com.duongnd.kytucxa.feature.auth.register

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockReset
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.duongnd.kytucxa.R
import com.duongnd.kytucxa.core.ui.components.KTXButton
import com.duongnd.kytucxa.core.ui.components.KTXTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit = {},
    onBackToLogin: () -> Unit = {},
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.registerState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isRegisterSuccess, uiState.errorMessage, uiState.successMessage) {
        if (uiState.isRegisterSuccess) {
            Toast.makeText(
                context,
                uiState.successMessage ?: "Đăng ký thành công!",
                Toast.LENGTH_SHORT
            ).show()
            onRegisterSuccess(uiState.email)
            viewModel.clearMessages()
        }
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    RegisterContent(
        uiState = uiState,
        onTermsAcceptedChange = { viewModel.onTermsAcceptedChange(it) },
        onRegisterClick = { fullName, email, password, confirmPassword ->
            viewModel.registerViewModel(fullName, email, password, confirmPassword)
        },
        onBackToLogin = onBackToLogin
    )
}

@Composable
private fun RegisterContent(
    uiState: RegisterState,
    onTermsAcceptedChange: (Boolean) -> Unit,
    onRegisterClick: (String, String, String, String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor, MaterialTheme.colorScheme.background)
                )
            )
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        val maxWidth = maxWidth
        val isTablet = maxWidth > 600.dp
        val formWidth = if (isTablet) 550.dp else maxWidth

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.PersonAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.create_account),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimary,
                letterSpacing = 1.5.sp
            )
            Text(
                text = stringResource(id = R.string.register_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .width(formWidth)
                    .padding(horizontal = if (isTablet) 0.dp else 20.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionHeader(
                        icon = null,
                        title = "Họ và tên",
                        color = primaryColor
                    )

                    KTXTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = stringResource(id = R.string.full_name),
                        leadingIcon = Icons.Rounded.Badge,
                        isError = uiState.fullNameError != null,
                        modifier = Modifier.focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(
                                FocusDirection.Down
                            )
                        })
                    )
                    uiState.fullNameError?.let { ErrorText(it) }

                    SectionHeader(
                        icon = null,
                        title = "Email",
                        color = primaryColor
                    )
                    KTXTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = stringResource(id = R.string.email),
                        leadingIcon = Icons.Rounded.Email,
                        isError = uiState.emailError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(
                                FocusDirection.Down
                            )
                        })
                    )
                    uiState.emailError?.let { ErrorText(it) }

                    SectionHeader(
                        icon = null,
                        title = "Mật khẩu",
                        color = primaryColor
                    )
                    KTXTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = stringResource(id = R.string.password),
                        leadingIcon = Icons.Rounded.Lock,
                        trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        },
                        isError = uiState.passwordError != null,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(
                                FocusDirection.Down
                            )
                        })
                    )
                    uiState.passwordError?.let { ErrorText(it) }

                    SectionHeader(
                        icon = null,
                        title = "Nhập lại mật khẩu",
                        color = primaryColor
                    )
                    KTXTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = stringResource(id = R.string.password),
                        leadingIcon = Icons.Rounded.LockReset,
                        trailingIcon = {
                            val image =
                                if (confirmPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        },
                        isError = uiState.confirmPasswordError != null,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.clearFocus()
                        })
                    )
                    uiState.confirmPasswordError?.let { ErrorText(it) }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = uiState.isTermsAccepted,
                            onCheckedChange = {
                                focusManager.clearFocus()
                                onTermsAcceptedChange(it)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = primaryColor)
                        )
                        Text(
                            text = stringResource(id = R.string.terms_and_conditions),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                focusManager.clearFocus()
                                onTermsAcceptedChange(!uiState.isTermsAccepted)
                            }
                        )
                    }

                    KTXButton(
                        text = stringResource(id = R.string.register),
                        onClick = {
                            focusManager.clearFocus()
                            onRegisterClick(
                                fullName,
                                email,
                                password,
                                confirmPassword
                            )
                        },
                        enabled = !uiState.isLoading && uiState.isTermsAccepted,
                        isLoading = uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.already_have_account),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(id = R.string.login_now),
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { onBackToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(icon: ImageVector?, title: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}


@Composable
private fun ErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(start = 12.dp, top = 2.dp)
    )
}


@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterContent(
        uiState = RegisterState(),
        onTermsAcceptedChange = {},
        onRegisterClick = { _, _, _, _ -> },
        onBackToLogin = {}
    )
}

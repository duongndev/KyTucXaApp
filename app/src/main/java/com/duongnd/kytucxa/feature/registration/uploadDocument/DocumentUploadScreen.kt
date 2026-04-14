package com.duongnd.kytucxa.feature.registration.uploadDocument

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.duongnd.kytucxa.core.ui.components.StepIndicator
import com.duongnd.kytucxa.core.ui.components.KTXButton
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.feature.registration.RegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentUploadScreen(
    viewModel: RegistrationViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val uploadDocsResult by viewModel.uploadDocsResult.collectAsState()
    val isSubmitting = uploadDocsResult is Resource.Loading

    val idCardFront by viewModel.idCardFront.collectAsState()
    val idCardBack by viewModel.idCardBack.collectAsState()
    val studentCard by viewModel.studentCard.collectAsState()
    val priorityDoc by viewModel.priorityDoc.collectAsState()

    val canSubmit = idCardFront != null && idCardBack != null && studentCard != null

    // Handle System Back Press
    BackHandler(enabled = !isSubmitting) {
        onBack()
    }

    LaunchedEffect(uploadDocsResult) {
        if (uploadDocsResult is Resource.Success) {
            onNext()
            viewModel.resetUploadDocsResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Hồ sơ đính kèm",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isSubmitting) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(20.dp)
                        .navigationBarsPadding()
                ) {
                    KTXButton(
                        text = "HOÀN TẤT TẢI LÊN",
                        onClick = { viewModel.uploadDocuments() },
                        isLoading = isSubmitting,
                        enabled = canSubmit,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!canSubmit) {
                        Text(
                            "(*) Vui lòng cung cấp đủ các giấy tờ bắt buộc",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            StepIndicator(currentStep = 3)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.weight(1f)
            ) {
                item(span = { GridItemSpan(2) }) {
                    InfoSection()
                }

                item {
                    CompactUploadItem(
                        label = "CCCD Mặt trước",
                        isRequired = true,
                        icon = Icons.Rounded.Badge,
                        uri = idCardFront,
                        onUriSelected = { viewModel.updateIdCardFront(it) },
                        isDisabled = isSubmitting
                    )
                }
                item {
                    CompactUploadItem(
                        label = "CCCD Mặt sau",
                        isRequired = true,
                        icon = Icons.Rounded.Badge,
                        uri = idCardBack,
                        onUriSelected = { viewModel.updateIdCardBack(it) },
                        isDisabled = isSubmitting
                    )
                }
                item {
                    CompactUploadItem(
                        label = "Thẻ sinh viên",
                        isRequired = true,
                        icon = Icons.Rounded.School,
                        uri = studentCard,
                        onUriSelected = { viewModel.updateStudentCard(it) },
                        isDisabled = isSubmitting
                    )
                }
                item {
                    CompactUploadItem(
                        label = "Giấy ưu tiên",
                        isRequired = false,
                        icon = Icons.Rounded.WorkspacePremium,
                        uri = priorityDoc,
                        onUriSelected = { viewModel.updatePriorityDoc(it) },
                        isDisabled = isSubmitting
                    )
                }
                
                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun InfoSection() {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = "Yêu cầu hồ sơ",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "Ảnh chụp cần rõ nét, không bị lóa và đầy đủ 4 góc của giấy tờ.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun CompactUploadItem(
    label: String,
    isRequired: Boolean,
    icon: ImageVector,
    uri: Uri?,
    onUriSelected: (Uri?) -> Unit,
    isDisabled: Boolean
) {
    val isUploaded = uri != null
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { selectedUri: Uri? -> 
        if (selectedUri != null) onUriSelected(selectedUri) 
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = if (isUploaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            if (isRequired && !isUploaded) {
                Text(" *", color = MaterialTheme.colorScheme.error)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    if (isUploaded) Color.Transparent 
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
                .clickable(enabled = !isDisabled) { launcher.launch("image/*") }
                .then(
                    if (!isUploaded) Modifier.border(
                        1.dp, 
                        MaterialTheme.colorScheme.outlineVariant, 
                        RoundedCornerShape(28.dp)
                    ) else Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(28.dp)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isUploaded) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Overlay chỉnh sửa
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Badge thành công
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF10B981))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Tải ảnh",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

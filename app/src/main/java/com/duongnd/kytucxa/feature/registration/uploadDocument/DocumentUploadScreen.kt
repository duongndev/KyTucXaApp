package com.duongnd.kytucxa.feature.registration.uploadDocument

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.duongnd.kytucxa.core.ui.components.KTXButton
import com.duongnd.kytucxa.core.ui.components.KTXDialog
import com.duongnd.kytucxa.core.ui.components.StepIndicator
import com.duongnd.kytucxa.core.ui.theme.KyTucXaTheme
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentUploadScreen(
    viewModel: UploadDocumentViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isSubmitting = uiState.isLoading

    val idCardFront = uiState.idCardFront
    val idCardBack = uiState.idCardBack
    val studentCard = uiState.studentCard
    val priorityDoc = uiState.priorityDoc

    var showExitDialog by remember { mutableStateOf(false) }

    val hasChanges = idCardFront != null || idCardBack != null || studentCard != null || priorityDoc != null

    // Handle System Back Press
    BackHandler(enabled = !isSubmitting) {
        if (hasChanges) {
            showExitDialog = true
        } else {
            onBack()
        }
    }

    // Exit Confirmation Dialog
    if (showExitDialog) {
        KTXDialog(
            onDismissRequest = { showExitDialog = false },
            title = "Quay lại bước trước?",
            description = {
                Text(
                    "Các tệp tin đã chọn sẽ được giữ lại, bạn có chắc chắn muốn quay lại?",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButtonText = "QUAY LẠI",
            onConfirm = {
                showExitDialog = false
                viewModel.resetResult()
                onBack()
            },
            dismissButtonText = "Ở LẠI",
            onDismiss = { showExitDialog = false }
        )
    }

    DocumentUploadContent(
        idCardFront = idCardFront,
        idCardBack = idCardBack,
        studentCard = studentCard,
        priorityDoc = priorityDoc,
        isSubmitting = isSubmitting,
        onUpdateIdCardFront = { viewModel.onIdCardFrontChanged(it) },
        onUpdateIdCardBack = { viewModel.onIdCardBackChanged(it) },
        onUpdateStudentCard = { viewModel.onStudentCardChanged(it) },
        onUpdatePriorityDoc = { viewModel.onPriorityDocChanged(it) },
        onUploadDocuments = onNext,
        onBackClick = {
            if (hasChanges) {
                showExitDialog = true
            } else {
                onBack()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentUploadContent(
    idCardFront: Uri?,
    idCardBack: Uri?,
    studentCard: Uri?,
    priorityDoc: Uri?,
    isSubmitting: Boolean,
    onUpdateIdCardFront: (Uri?) -> Unit,
    onUpdateIdCardBack: (Uri?) -> Unit,
    onUpdateStudentCard: (Uri?) -> Unit,
    onUpdatePriorityDoc: (Uri?) -> Unit,
    onUploadDocuments: () -> Unit,
    onBackClick: () -> Unit
) {
    val canSubmit = idCardFront != null && idCardBack != null && studentCard != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Hồ sơ đính kèm",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        enabled = !isSubmitting
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .navigationBarsPadding()
                ) {
                    if (!canSubmit) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Vui lòng tải lên các mục bắt buộc (*)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    KTXButton(
                        text = "TIẾP TỤC",
                        onClick = onUploadDocuments,
                        isLoading = isSubmitting,
                        enabled = canSubmit,
                        modifier = Modifier.fillMaxWidth()
                    )
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

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                StepIndicator(currentStep = 3)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                item {
                    Text(
                        "Tải lên hình ảnh giấy tờ tùy thân và các hồ sơ liên quan để hoàn tất đăng ký.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    DocumentUploadItem(
                        label = "CCCD MẶT TRƯỚC",
                        isRequired = true,
                        uri = idCardFront,
                        onUriSelected = onUpdateIdCardFront,
                        isDisabled = isSubmitting,
                        aspectRatio = 1.586f
                    )
                }

                item {
                    DocumentUploadItem(
                        label = "CCCD MẶT SAU",
                        isRequired = true,
                        uri = idCardBack,
                        onUriSelected = onUpdateIdCardBack,
                        isDisabled = isSubmitting,
                        aspectRatio = 1.586f
                    )
                }

                item {
                    DocumentUploadItem(
                        label = "THẺ SINH VIÊN",
                        isRequired = true,
                        uri = studentCard,
                        onUriSelected = onUpdateStudentCard,
                        isDisabled = isSubmitting,
                        aspectRatio = 1.586f
                    )
                }

                item {
                    DocumentUploadItem(
                        label = "GIẤY TỜ ƯU TIÊN (NẾU CÓ)",
                        isRequired = false,
                        uri = priorityDoc,
                        onUriSelected = onUpdatePriorityDoc,
                        isDisabled = isSubmitting,
                        aspectRatio = 1.586f
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentUploadItem(
    label: String,
    isRequired: Boolean,
    uri: Uri?,
    onUriSelected: (Uri?) -> Unit,
    isDisabled: Boolean,
    aspectRatio: Float = 1.586f
) {
    val context = LocalContext.current
    var showOptions by remember { mutableStateOf(false) }
    var rawUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showCropDialog by remember { mutableStateOf(false) }
    var showFullScreenPreview by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { selectedUri: Uri? ->
        if (selectedUri != null) {
            rawUri = selectedUri
            showCropDialog = true
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            rawUri = tempCameraUri
            showCropDialog = true
        }
    }

    fun createImageUri(): Uri? {
        val directory = File(context.cacheDir, "Pictures").apply { if (!exists()) mkdirs() }
        val file = File.createTempFile("DOC_${System.currentTimeMillis()}", ".jpg", directory)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    if (showOptions) {
        ModalBottomSheet(
            onDismissRequest = { showOptions = false },
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    text = "Tải lên hồ sơ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                ListItem(
                    headlineContent = { Text("Chụp ảnh mới", fontWeight = FontWeight.Medium) },
                    supportingContent = { Text("Sử dụng máy ảnh để chụp trực tiếp") },
                    leadingContent = {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Rounded.PhotoCamera,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    modifier = Modifier.clickable {
                        showOptions = false
                        val newUri = createImageUri()
                        tempCameraUri = newUri
                        cameraLauncher.launch(newUri)
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                ListItem(
                    headlineContent = {
                        Text(
                            "Chọn từ thư viện",
                            fontWeight = FontWeight.Medium
                        )
                    },
                    supportingContent = { Text("Chọn ảnh đã có sẵn trong thiết bị") },
                    leadingContent = {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Rounded.Collections,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    },
                    modifier = Modifier.clickable {
                        showOptions = false
                        galleryLauncher.launch("image/*")
                    }
                )
            }
        }
    }

    if (showCropDialog && rawUri != null) {
        ImageCropDialog(
            uri = rawUri!!,
            aspectRatio = aspectRatio,
            onConfirm = { croppedUri ->
                onUriSelected(croppedUri)
                showCropDialog = false
            },
            onDismiss = { showCropDialog = false }
        )
    }

    if (showFullScreenPreview && uri != null) {
        FullImagePreviewDialog(
            uri = uri,
            onDismiss = { showFullScreenPreview = false }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (isRequired) {
                Text(
                    text = " *",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .border(
                    width = 1.dp,
                    color = if (uri != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(enabled = !isDisabled) {
                    if (uri != null) showFullScreenPreview = true else showOptions = true
                },
            contentAlignment = Alignment.Center
        ) {
            if (uri != null) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Clear and Edit buttons overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f))
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = { showOptions = true },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Rounded.Edit,
                                contentDescription = "Edit",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Surface(
                        onClick = { onUriSelected(null) },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = "Delete",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Click to view hint
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Nhấn để xem lại",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Rounded.AddAPhoto,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tải lên hình ảnh",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Chụp ảnh hoặc chọn từ thư viện",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun FullImagePreviewDialog(
    uri: Uri,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Full Preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ImageCropDialog(
    uri: Uri,
    aspectRatio: Float,
    onConfirm: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val androidBitmap = remember(uri) {
        try {
            if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    if (androidBitmap == null) {
        LaunchedEffect(Unit) { onDismiss() }
        return
    }

    val imageBitmap = remember(androidBitmap) { androidBitmap.asImageBitmap() }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val viewWidth = constraints.maxWidth.toFloat()
                val viewHeight = constraints.maxHeight.toFloat()

                val frameWidth = viewWidth * 0.9f
                val frameHeight = frameWidth / aspectRatio
                val frameLeft = (viewWidth - frameWidth) / 2
                val frameTop = (viewHeight - frameHeight) / 2

                val imgWidth = androidBitmap.width.toFloat()
                val imgHeight = androidBitmap.height.toFloat()
                val baseScale = minOf(viewWidth / imgWidth, viewHeight / imgHeight)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(0.5f, 5f)
                                offset += pan
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = null,
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = baseScale * scale,
                                scaleY = baseScale * scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    )
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(alpha = 0.99f)
                ) {
                    val rect = Rect(
                        frameLeft,
                        frameTop,
                        frameLeft + frameWidth,
                        frameTop + frameHeight
                    )
                    val cornerRadiusPx = 12.dp.toPx()

                    drawRect(color = Color.Black.copy(alpha = 0.7f))

                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        cornerRadius = CornerRadius(cornerRadiusPx),
                        blendMode = BlendMode.Clear
                    )

                    drawRoundRect(
                        color = Color.White,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        cornerRadius = CornerRadius(cornerRadiusPx),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = onDismiss,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = null, tint = Color.White)
                    }

                    Button(
                        onClick = {
                            val visualWidth = imgWidth * baseScale * scale
                            val visualHeight = imgHeight * baseScale * scale

                            val centerX = viewWidth / 2
                            val centerY = viewHeight / 2

                            val imgLeft = centerX + offset.x - visualWidth / 2
                            val imgTop = centerY + offset.y - visualHeight / 2

                            val cropX = (frameLeft - imgLeft) / (baseScale * scale)
                            val cropY = (frameTop - imgTop) / (baseScale * scale)
                            val cropW = frameWidth / (baseScale * scale)
                            val cropH = frameHeight / (baseScale * scale)

                            try {
                                val x = cropX.toInt().coerceIn(0, androidBitmap.width - 1)
                                val y = cropY.toInt().coerceIn(0, androidBitmap.height - 1)
                                val width = cropW.toInt().coerceAtMost(androidBitmap.width - x)
                                val height =
                                    cropH.toInt().coerceAtMost(androidBitmap.height - y)

                                if (width > 0 && height > 0) {
                                    val croppedBitmap =
                                        Bitmap.createBitmap(androidBitmap, x, y, width, height)
                                    val file = File(
                                        context.cacheDir,
                                        "doc_${System.currentTimeMillis()}.jpg"
                                    )
                                    FileOutputStream(file).use { out ->
                                        croppedBitmap.compress(
                                            Bitmap.CompressFormat.JPEG,
                                            95,
                                            out
                                        )
                                    }
                                    onConfirm(Uri.fromFile(file))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Xong", fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    "Căn chỉnh ảnh vào khung thẻ",
                    color = Color.White,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 64.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DocumentUploadPreview() {
    KyTucXaTheme {
        DocumentUploadContent(
            idCardFront = null,
            idCardBack = null,
            studentCard = null,
            priorityDoc = null,
            isSubmitting = false,
            onUpdateIdCardFront = {},
            onUpdateIdCardBack = {},
            onUpdateStudentCard = {},
            onUpdatePriorityDoc = {},
            onUploadDocuments = {},
            onBackClick = {}
        )
    }
}

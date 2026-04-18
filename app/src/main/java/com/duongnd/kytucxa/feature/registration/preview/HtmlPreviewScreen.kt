package com.duongnd.kytucxa.feature.registration.preview

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.duongnd.kytucxa.core.utils.Resource

@Composable
fun HtmlPreviewScreen(
    viewModel: HtmlPreviewViewModel,
    formId: String,
    type: String,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Gọi API lấy bản xem trước khi vào màn hình
    LaunchedEffect(formId, type) {
        viewModel.loadPreview(formId, type)
    }

    // Reset trạng thái khi thoát màn hình
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    HtmlPreviewContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = { viewModel.loadPreview(formId, type) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HtmlPreviewContent(
    uiState: HtmlPreviewUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF0047BB)
                )
            } else if (uiState.errorMessage != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0047BB)
                        )
                    ) {
                        Text("Thử lại")
                    }
                }
            } else if (uiState.htmlContent != null) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = WebViewClient()
                            setBackgroundColor(android.graphics.Color.WHITE)
                            setInitialScale(1)

                            settings.apply {
                                javaScriptEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                builtInZoomControls = true
                                displayZoomControls = false
                                setSupportZoom(true)
                            }
                        }
                    },
                    update = { webView ->
                        webView.loadDataWithBaseURL(null, uiState.htmlContent, "text/html", "UTF-8", null)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true, name = "Trạng thái Loading")
@Composable
fun PreviewHtmlLoading() {
    HtmlPreviewContent(
        uiState = HtmlPreviewUiState(isLoading = true),
        onBack = {},
        onRetry = {}
    )
}

@Preview(showBackground = true, name = "Trạng thái Lỗi")
@Composable
fun PreviewHtmlError() {
    HtmlPreviewContent(
        uiState = HtmlPreviewUiState(
            errorMessage = "Không thể kết nối đến máy chủ"
        ),
        onBack = {},
        onRetry = {}
    )
}

@Preview(showBackground = true, name = "Trạng thái Thành công")
@Composable
fun PreviewHtmlSuccess() {
    val mockHtml = """
        <html>
            <body style="padding: 20px;">
                <h1 style="color: #0047BB;">ĐƠN ĐĂNG KÝ NỘI TRÚ</h1>
                <p>Họ tên: <b>Nguyễn Văn A</b></p>
                <p>MSSV: <b>12345678</b></p>
                <p>Nội dung xem trước đơn đăng ký của bạn sẽ hiển thị tại đây.</p>
            </body>
        </html>
    """.trimIndent()

    HtmlPreviewContent(
        uiState = HtmlPreviewUiState(
            htmlContent = mockHtml
        ),
        onBack = {},
        onRetry = {}
    )
}

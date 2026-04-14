package com.duongnd.kytucxa.feature.registration

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.duongnd.kytucxa.core.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HtmlPreviewScreen(
    viewModel: RegistrationViewModel,
    formId: String,
    type: String,
    onBack: () -> Unit
) {
    val previewState by viewModel.previewHtml.collectAsState()

    // Gọi API lấy bản xem trước khi vào màn hình
    LaunchedEffect(formId, type) {
        if (type == "residence") {
            viewModel.getPreviewNoiTru(formId)
        } else {
            viewModel.getPreviewTamTru(formId)
        }
    }

    // Reset trạng thái khi thoát màn hình
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetPreview()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xem trước đơn", fontWeight = FontWeight.Bold) },
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
            when (val state = previewState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF0047BB)
                    )
                }

                is Resource.Success -> {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                webViewClient = WebViewClient()
                                setBackgroundColor(android.graphics.Color.WHITE)

                                // Cấu hình zoom nhỏ nhất
                                setInitialScale(1) // Ép WebView thu nhỏ hết mức để vừa chiều rộng

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
                            webView.loadDataWithBaseURL(null, state.data, "text/html", "UTF-8", null)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is Resource.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (type == "residence") {
                                    viewModel.getPreviewNoiTru(formId)
                                } else {
                                    viewModel.getPreviewTamTru(formId)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0047BB)
                            )
                        ) {
                            Text("Thử lại")
                        }
                    }
                }

                else -> {
                    // Trạng thái Idle
                }
            }
        }
    }
}

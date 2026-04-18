package com.duongnd.kytucxa.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CccdOtpInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    Column {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    onValueChange(it)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            decorationBox = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(12) { index ->
                        val char = when {
                            index >= value.length -> ""
                            else -> value[index].toString()
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.7f)
                                .border(
                                    width = 1.dp,
                                    color = if (index == value.length) Color(0xFF0047BB) else Color.LightGray,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .background(
                                    if (index == value.length) Color(0xFF0047BB).copy(alpha = 0.05f)
                                    else Color.Transparent,
                                    RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        )
    }
}
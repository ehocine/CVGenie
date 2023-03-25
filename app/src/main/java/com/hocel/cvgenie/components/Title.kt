package com.hocel.cvgenie.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hocel.cvgenie.ui.theme.TextColor

@Composable
fun Title(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier
            .padding(16.dp, 0.dp, 0.dp, 0.dp),
        color = MaterialTheme.colors.TextColor,
        style = MaterialTheme.typography.subtitle1,
        fontWeight = FontWeight.W600,
        textAlign = TextAlign.Start
    )
}
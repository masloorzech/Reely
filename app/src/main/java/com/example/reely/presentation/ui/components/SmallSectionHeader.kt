package com.example.reely.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SmallSectionHeader(
    sectionName: String
){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = sectionName,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.weight(1f))

    }
}
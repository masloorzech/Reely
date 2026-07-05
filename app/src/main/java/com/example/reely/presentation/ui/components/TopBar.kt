package com.example.reely.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TopBar(
    modifier : Modifier = Modifier,
    sectionName: String
){
    Row(
        modifier = modifier
            .fillMaxWidth()
    ){
        Text(
            text = sectionName,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
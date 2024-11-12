package com.yousuf.weatherapp.ui.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun AlertDialogScreen(
    onDismissed: () -> Unit,
    onConfirmed: () -> Unit,
    title: String,
    message: String,
    positiveButtonText: String = "Ok",
    negativeButtonText: String = "Cancel"
) {
    AlertDialog(
        onDismissRequest = {
            onDismissed()
        },
        title = {
            Text(
                text = title, //"Location Permission",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Text(
                message, //"Please grant location permission to populate location automatically",
                fontSize = 16.sp
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirmed() }) {
                Text(positiveButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissed() }) {
                Text(negativeButtonText)
            }
        },
    )
}
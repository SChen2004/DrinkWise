package com.example.cauds.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cauds.viewmodel.JournalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreateEntryScreen(navController: NavController, viewModel: JournalViewModel) {

    var entryText by remember { mutableStateOf("") }

    val todayDate = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())
    val todayDayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())

    // After a successful save we want to go back to the journal screen.
    // We also reload entries so the new one shows up immediately.
    LaunchedEffect(viewModel.saveSuccess) {
        if (viewModel.saveSuccess) {
            viewModel.onSaveHandled()   // reset the flag so it doesn't trigger again
            viewModel.loadEntries()     // refresh the list
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top section: back arrow + date header
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = todayDate, fontSize = 16.sp)
                Text(text = todayDayOfWeek, fontSize = 16.sp, color = Color.Gray)
            }

            HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
        }

        // Text input — takes up all remaining space between the header and the bottom bar
        TextField(
            value = entryText,
            onValueChange = { entryText = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            placeholder = { Text("Type something...", color = Color.LightGray) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        // Error message if save fails
        if (viewModel.saveError != null) {
            Text(
                text = viewModel.saveError!!,
                color = Color.Red,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        // Bottom bar: save button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = { viewModel.saveEntry(entryText) },
                modifier = Modifier.fillMaxWidth(),
                enabled = entryText.isNotBlank() && !viewModel.isSaving,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                if (viewModel.isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("SAVE", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}
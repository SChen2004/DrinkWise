package com.example.cauds.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cauds.viewmodel.JournalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.cauds.ui.theme.BigShouldersDisplay
import androidx.compose.ui.res.painterResource
import com.example.cauds.R
import com.example.cauds.ui.theme.Poppins
import androidx.compose.ui.graphics.RectangleShape
import com.example.cauds.ui.components.DateWheelPicker
import java.time.LocalDate

private val horizontalPad = 32.dp

@Composable
fun CreateEntryScreen(navController: NavController, viewModel: JournalViewModel) {

    var entryText by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.editingEntryText) {
        if (viewModel.editingEntryText != null) {
            entryText = viewModel.editingEntryText!!
        }
    }

    // Save success handler
    LaunchedEffect(viewModel.saveSuccess) {
        if (viewModel.saveSuccess) {
            viewModel.onSaveHandled()
            viewModel.clearEditingEntry()
            viewModel.loadEntries()
            navController.popBackStack()
        }
    }

    val displayDate = viewModel.selectedDate?.let {
        java.time.format.DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()).format(it)
    } ?: SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())

    val displayDayOfWeek = viewModel.selectedDate?.let {
        it.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())
    } ?: SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF5DC))
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 48.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp, end = 6.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    viewModel.clearEditingEntry()
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1A3720)
                    )
                }

                IconButton(onClick = {
                    val editId = viewModel.editingEntryId
                    if (editId != null) {
                        viewModel.deleteEntry(editId)
                    }
                    viewModel.clearEditingEntry()
                    navController.popBackStack()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trash),
                        contentDescription = "Delete",
                        tint = Color(0xFF1A3720),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = horizontalPad, end = horizontalPad, top = 18.dp)
                    .clickable { showDatePicker = !showDatePicker },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayDate,
                    fontSize = 24.sp,
                    fontFamily = BigShouldersDisplay,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1A3720)
                )
                Text(
                    text = displayDayOfWeek,
                    fontSize = 20.sp,
                    fontFamily = BigShouldersDisplay,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1A3720)
                )
            }

            if (showDatePicker) {
                DateWheelPicker(
                    selectedDate = viewModel.selectedDate ?: LocalDate.now(),
                    onDateSelected = { newDate ->
                        viewModel.setEntryDate(newDate)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(start = horizontalPad, end = horizontalPad, top = 12.dp)
            )
        }

        TextField(
            value = entryText,
            onValueChange = { entryText = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 16.dp, end = horizontalPad, top = 8.dp)
                .onFocusChanged { if (it.isFocused) showDatePicker = false },
            placeholder = {
                Text(
                    "Type something...",
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1A3720).copy(alpha = 0.3f)
                )
            },
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = Poppins,
                fontSize = 16.sp,
                color = Color(0xFF1A3720)
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        if (viewModel.saveError != null) {
            Text(
                text = viewModel.saveError!!,
                color = Color.Red,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        HorizontalDivider(
            thickness = 0.5.dp,
            color = Color.Gray.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // If editing, delete the old entry first
                val editId = viewModel.editingEntryId
                if (editId != null) {
                    viewModel.deleteEntry(editId)
                }
                viewModel.saveEntry(entryText) },
            enabled = entryText.isNotBlank() && !viewModel.isSaving,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF121E30),
                disabledContainerColor = Color(0xFF8397A5),
                contentColor = Color.White,
                disabledContentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(44.dp)
        ) {
            if (viewModel.isSaving) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Save",
                    fontFamily = BigShouldersDisplay,
                    fontSize = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}
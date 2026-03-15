package com.example.cauds.ui.drinklog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.cauds.R
import com.example.cauds.data.model.DrinkItem
import com.example.cauds.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageDrinksScreen(navController: NavController, viewModel: ManageDrinksViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("SAVE", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Fixed top section (Search + Add)
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search", color = Color.Gray, fontFamily = FontFamily.Monospace) },
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color.Black,
                    unfocusedContainerColor = Color(0xFFF9F9F9),
                    focusedContainerColor = Color(0xFFF9F9F9)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.AddNewDrink.route) }
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Drink", tint = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Add New Drink", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable List Section
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            } else {
                val filteredDrinks = if (uiState.searchQuery.isBlank()) {
                    uiState.allDrinks
                } else {
                    uiState.allDrinks.filter {
                        it.data.name.contains(uiState.searchQuery, ignoreCase = true)
                    }
                }

                if (filteredDrinks.isEmpty() && uiState.searchQuery.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Drink not found.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Try another search or add it as a new drink.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        // Section: Selected
                        if (uiState.searchQuery.isBlank()) {
                            item {
                                SectionHeader("Selected")
                            }

                            val selectedDrinks = uiState.allDrinks.filter { it.data.isSelected }
                            items(selectedDrinks, key = { it.id }) { drink ->
                                DrinkRowItem(
                                    drink = drink,
                                    onToggleSelection = { viewModel.toggleDrinkSelection(drink) },
                                    onDelete = { viewModel.deleteDrink(drink) }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                                SectionHeader("All", showDropdownIcon = false)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        // Categories Breakdown
                        val categories = listOf("Beer", "Fermented Drinks", "Wine", "Hard Liquor", "Mixed Drinks")

                        categories.forEach { category ->
                            val categoryDrinks = filteredDrinks.filter { it.data.category == category }
                            
                            // Only show category if it has matching drinks (relevant for search)
                            if (categoryDrinks.isNotEmpty() || uiState.searchQuery.isBlank()) {
                                item(key = category) {
                                    val isExpanded = uiState.expandedCategories.contains(category)
                                    CategoryHeader(
                                        title = category,
                                        isExpanded = isExpanded,
                                        onClick = { viewModel.toggleCategoryExpansion(category) }
                                    )
                                }

                                if (uiState.searchQuery.isNotBlank() || uiState.expandedCategories.contains(category)) {
                                    items(categoryDrinks, key = { "cat_${it.id}" }) { drink ->
                                        DrinkRowItem(
                                            drink = drink,
                                            onToggleSelection = { viewModel.toggleDrinkSelection(drink) },
                                            onDelete = { viewModel.deleteDrink(drink) }
                                        )
                                    }
                                }
                            }
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, showDropdownIcon: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
        if (showDropdownIcon) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun CategoryHeader(title: String, isExpanded: Boolean, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(true)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrinkRowItem(
    drink: DrinkItem,
    onToggleSelection: () -> Unit,
    onDelete: () -> Unit
) {
    var showDelete by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .pointerInput(drink.id) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -15) showDelete = true
                        else if (dragAmount > 15) showDelete = false
                    }
                }
                .clickable {
                    if (showDelete) showDelete = false
                    else onToggleSelection()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp).weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconRes = when (drink.data.category) {
                    "Beer" -> R.drawable.ic_beer
                    "Wine" -> R.drawable.ic_wine
                    "Mixed Drinks" -> R.drawable.ic_mixed_drinks
                    "Hard Liquor" -> R.drawable.ic_hard_liquor
                    "Fermented Drinks" -> R.drawable.ic_fermented_drinks
                    else -> R.drawable.ic_beer
                }
                
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = drink.data.category,
                    modifier = Modifier.size(12.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = drink.data.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
                // Custom Checkbox [X] vs [ ]
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(2.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (drink.data.isSelected) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Selected",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                    }
                }

                if (showDelete) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(48.dp)
                            .background(Color(0xFFFF5252))
                            .clickable {
                                showDelete = false
                                onDelete()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, "Delete", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                } else {
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
    }
}

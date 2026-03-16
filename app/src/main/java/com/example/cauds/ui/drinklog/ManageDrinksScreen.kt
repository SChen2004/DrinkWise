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
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.Roboto
import com.example.cauds.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageDrinksScreen(navController: NavController, viewModel: ManageDrinksViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val backgroundColor = Color(0xFFFEF5DC)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121E30))
                ) {
                    Text("SAVE", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = BigShouldersDisplay, letterSpacing = 2.sp)
                }
            }
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
        ) {
            // Fixed top section (Search + Add)
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search", color = Color.Gray, fontFamily = Roboto) },
                textStyle = LocalTextStyle.current.copy(fontFamily = Roboto),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                shape = RoundedCornerShape(2.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedContainerColor = Color(0xFFF2E7C9),
                    focusedContainerColor = Color(0xFFF2E7C9)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.AddNewDrink.route) }
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Drink", tint = Color.Black, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Add New Drink", fontSize = 24.sp, fontWeight = FontWeight.Normal, fontFamily = BigShouldersDisplay)
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                            fontSize = 24.sp,
                            fontFamily = BigShouldersDisplay,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Try another search or add it as a new drink.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontFamily = Roboto,
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
                            val isSelectedExpanded = uiState.expandedCategories.contains("Selected")
                            item {
                                SectionHeader(
                                    title = "Selected",
                                    isExpanded = isSelectedExpanded,
                                    onClick = { viewModel.toggleCategoryExpansion("Selected") }
                                )
                            }

                            if (isSelectedExpanded) {
                                val selectedDrinks = uiState.allDrinks.filter { it.data.isSelected }
                                item {
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .border(1.dp, Color.Black)
                                    ) {
                                        selectedDrinks.forEachIndexed { index, drink ->
                                            DrinkRowItem(
                                                drink = drink,
                                                onToggleSelection = { viewModel.toggleDrinkSelection(drink) },
                                                onDelete = { viewModel.deleteDrink(drink) }
                                            )
                                            if (index < selectedDrinks.size - 1) {
                                                HorizontalDivider(color = Color.Black, thickness = 1.dp)
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(48.dp))
                                SectionHeader("All", showDropdownIcon = false)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // Categories Breakdown
                        val categories = listOf("Beer", "Fermented", "Wine", "Spirit", "Cocktail/Mixed")

                        categories.forEachIndexed { catIndex, category ->
                            val categoryDrinks = filteredDrinks.filter { it.data.category == category }
                            
                            // Only show category if it has matching drinks (relevant for search)
                            if (categoryDrinks.isNotEmpty() || uiState.searchQuery.isBlank()) {
                                item(key = category) {
                                    val isExpanded = uiState.expandedCategories.contains(category)
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .border(1.dp, Color.Black)
                                    ) {
                                        CategoryHeader(
                                            title = category,
                                            isExpanded = isExpanded,
                                            onClick = { viewModel.toggleCategoryExpansion(category) }
                                        )
                                        
                                        if (uiState.searchQuery.isNotBlank() || isExpanded) {
                                            HorizontalDivider(color = Color.Black, thickness = 1.dp)
                                            categoryDrinks.forEachIndexed { index, drink ->
                                                DrinkRowItem(
                                                    drink = drink,
                                                    onToggleSelection = { viewModel.toggleDrinkSelection(drink) },
                                                    onDelete = { viewModel.deleteDrink(drink) }
                                                )
                                                if (index < categoryDrinks.size - 1) {
                                                    HorizontalDivider(color = Color.Black, thickness = 1.dp)
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                item {
                                    Spacer(modifier = Modifier.height(24.dp))
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
fun SectionHeader(
    title: String, 
    showDropdownIcon: Boolean = true, 
    isExpanded: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = showDropdownIcon) { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = BigShouldersDisplay
        )
        if (showDropdownIcon) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, 
                contentDescription = null, 
                tint = Color.Black
            )
        }
    }
}

@Composable
fun CategoryHeader(title: String, isExpanded: Boolean, onClick: () -> Unit) {
    val backgroundColor = Color(0xFFFEF5DC)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .clickable { onClick() }
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Normal, fontFamily = Roboto)
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Black
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
    val backgroundColor = Color(0xFFFEF5DC)

    Box(modifier = Modifier.fillMaxWidth().height(50.dp).background(backgroundColor)) {
        Row(
            modifier = Modifier
                .fillMaxSize()
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
                modifier = Modifier.padding(horizontal = 16.dp).weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconRes = when (drink.data.category) {
                    "Beer" -> R.drawable.ic_beer_o
                    "Wine" -> R.drawable.ic_wine_o
                    "Cocktail/Mixed" -> R.drawable.ic_cocktail_mixed_o
                    "Spirit" -> R.drawable.ic_spirit_o
                    "Fermented" -> R.drawable.ic_fermented_o
                    else -> R.drawable.ic_beer_o
                }
                
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = drink.data.category,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = drink.data.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Roboto
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
                // Custom Checkbox [X] vs [ ]
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(0.dp))
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (drink.data.isSelected) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Selected",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Black
                        )
                    }
                }

                if (showDelete) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(50.dp)
                            .background(Color(0xFFFF6A6A))
                            .clickable {
                                showDelete = false
                                onDelete()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, "Delete", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                } else {
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}

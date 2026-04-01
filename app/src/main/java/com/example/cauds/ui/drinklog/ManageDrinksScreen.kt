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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.cauds.ui.theme.rdp
import com.example.cauds.ui.theme.rsp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.example.cauds.R
import com.example.cauds.data.model.DrinkItem
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.ui.theme.CloverDarker
import com.example.cauds.ui.theme.BackgroundSand
import com.example.cauds.ui.navigation.Screen
import com.example.cauds.ui.theme.Tertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageDrinksScreen(navController: NavController, viewModel: ManageDrinksViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val backgroundColor = BackgroundSand

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            // Refresh page when user ar eback from add new drink
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.loadDrinks()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_left),
                            contentDescription = "Back",
                            modifier = Modifier.size(20.rdp()),
                            tint = CloverDarker
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
            ) {
                HorizontalDivider(color = Color(0xFFC0CBDB), thickness = 0.5.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.rdp())
                        .padding(top = 16.rdp(), bottom = 24.rdp())
                ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.rdp()),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121E30)),
                    contentPadding = PaddingValues(0.dp) // Removed default padding to prevent vertical clipping
                ) {
                    Text(
                        "Save", 
                        color = Color.White, 
                        fontSize = 24.rsp(),
                        style = LocalTextStyle.current.copy(
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = false)
                        ),
                        fontWeight = FontWeight.Normal, 
                        fontFamily = BigShouldersDisplay, 
                        letterSpacing = 0.rsp()
                    )
                }
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
                    .padding(horizontal = 16.rdp()),
                placeholder = { Text("Search", color = CloverDarker.copy(alpha = 0.4f), fontFamily = Poppins, fontSize = 14.rsp()) },
                textStyle = LocalTextStyle.current.copy(fontFamily = Poppins, fontSize = 14.rsp(), color = CloverDarker),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = CloverDarker, modifier = Modifier.size(16.rdp())) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = CloverDarker.copy(alpha = 0.5f))
                        }
                    }
                },
                shape = RoundedCornerShape(0.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = CloverDarker.copy(alpha = 0.5f),
                    focusedBorderColor = CloverDarker,
                    unfocusedContainerColor = Color(0xFFFFEFC6).copy(alpha = 0.5f),
                    focusedContainerColor = Color(0xFFFFEFC6).copy(alpha = 0.5f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))
 
            // Scrollable Section
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CloverDarker)
                }
            } else {
                val filteredDrinks = if (uiState.searchQuery.isBlank()) {
                    uiState.allDrinks
                } else {
                    uiState.allDrinks.filter {
                        it.data.name.contains(uiState.searchQuery, ignoreCase = true) ||
                        it.data.category.contains(uiState.searchQuery, ignoreCase = true)
                    }
                }
 
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // Search Results/Not Found Section
                    if (uiState.searchQuery.isNotBlank()) {
                        val categoryOrder = listOf("Beer", "Wine", "Spirit", "Fermented", "Cocktail/Mixed")
                        val filteredDrinks = uiState.allDrinks.filter { 
                            it.data.name.contains(uiState.searchQuery, ignoreCase = true) ||
                            it.data.category.contains(uiState.searchQuery, ignoreCase = true)
                        }.sortedWith(compareBy({ categoryOrder.indexOf(it.data.category) }, { it.data.name }))

                        if (filteredDrinks.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.rdp(), vertical = 24.rdp()),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        "Drink not found.",
                                        fontSize = 24.rsp(),
                                        fontFamily = BigShouldersDisplay,
                                        textAlign = TextAlign.Start,
                                        color = CloverDarker
                                    )
                                    Spacer(modifier = Modifier.height(8.rdp()))
                                    Text(
                                        "Try another search or add it as a new drink.",
                                        color = CloverDarker.copy(alpha = 0.6f),
                                        fontSize = 14.rsp(),
                                        fontFamily = Poppins,
                                        textAlign = TextAlign.Start
                                    )
                                }
                            }
                        } else {
                            item {
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 16.rdp(), vertical = 16.rdp())
                                        .border(0.5.dp, CloverDarker.copy(alpha = 0.5f))
                                ) {
                                    filteredDrinks.forEachIndexed { index, drink ->
                                        DrinkRowItem(
                                            drink = drink,
                                            onToggleSelection = { viewModel.toggleDrinkSelection(drink) },
                                            onDelete = { viewModel.deleteDrink(drink) }
                                        )
                                        if (index < filteredDrinks.size - 1) {
                                            HorizontalDivider(color = CloverDarker.copy(alpha = 0.5f), thickness = 0.5.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Add New Drink Button (Always below search results or at top if not searching)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate(Screen.AddNewDrink.route) }
                                .padding(vertical = 16.rdp()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add New Drink",
                                tint = CloverDarker,
                                modifier = Modifier.size(24.rdp())
                            )
                            Spacer(modifier = Modifier.height(8.rdp()))
                            Text(
                                "Add New Drink",
                                fontSize = 24.rsp(),
                                color = CloverDarker,
                                fontWeight = FontWeight.Normal,
                                fontFamily = BigShouldersDisplay
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Main List Section (Only visible when NOT searching)
                    if (uiState.searchQuery.isBlank()) {
                        val categoryOrder = listOf("Beer", "Wine", "Spirit", "Fermented", "Cocktail/Mixed")
                        val isSelectedExpanded = uiState.expandedCategories.contains("Selected")
                        item {
                            SectionHeader(
                                title = "Selected",
                                isExpanded = isSelectedExpanded,
                                onClick = { viewModel.toggleCategoryExpansion("Selected") }
                            )
                        }

                        if (isSelectedExpanded) {
                            val selectedDrinks = uiState.allDrinks
                                .filter { it.data.isSelected }
                                .sortedWith(compareBy({ categoryOrder.indexOf(it.data.category) }, { it.data.name }))
                                
                            item {
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 16.rdp())
                                        .border(0.5.dp, CloverDarker.copy(alpha = 0.5f))
                                ) {
                                    selectedDrinks.forEachIndexed { index, drink ->
                                        DrinkRowItem(
                                            drink = drink,
                                            onToggleSelection = { viewModel.toggleDrinkSelection(drink) },
                                            onDelete = { viewModel.deleteDrink(drink) }
                                        )
                                        if (index < selectedDrinks.size - 1) {
                                            HorizontalDivider(color = CloverDarker.copy(alpha = 0.5f), thickness = 0.5.dp)
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

                        // Categories Breakdown
                        categoryOrder.forEachIndexed { catIndex, category ->
                            val categoryDrinks = uiState.allDrinks
                                .filter { it.data.category == category }
                                .sortedBy { it.data.name }
                            
                            if (categoryDrinks.isNotEmpty()) {
                                item(key = category) {
                                    val isExpanded = uiState.expandedCategories.contains(category)
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 16.rdp())
                                            .border(0.5.dp, CloverDarker.copy(alpha = 0.5f))
                                    ) {
                                        CategoryHeader(
                                            title = category,
                                            isExpanded = isExpanded,
                                            onClick = { viewModel.toggleCategoryExpansion(category) }
                                        )
                                        
                                        if (isExpanded) {
                                            HorizontalDivider(color = CloverDarker.copy(alpha = 0.5f), thickness = 0.5.dp)
                                            categoryDrinks.forEachIndexed { index, drink ->
                                                DrinkRowItem(
                                                    drink = drink,
                                                    onToggleSelection = { viewModel.toggleDrinkSelection(drink) },
                                                    onDelete = { viewModel.deleteDrink(drink) }
                                                )
                                                if (index < categoryDrinks.size - 1) {
                                                    HorizontalDivider(color = CloverDarker.copy(alpha = 0.5f), thickness = 0.5.dp)
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                item {
                                    Spacer(modifier = Modifier.height(24.rdp()))
                                }
                            }
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
            .padding(horizontal = 16.rdp(), vertical = 8.rdp()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 24.rsp(),
            color = CloverDarker,
            fontWeight = FontWeight.Normal,
            fontFamily = BigShouldersDisplay
        )
        if (showDropdownIcon) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, 
                contentDescription = null, 
                tint = CloverDarker
            )
        }
    }
}

@Composable
fun CategoryHeader(title: String, isExpanded: Boolean, onClick: () -> Unit) {
    val backgroundColor = Tertiary.copy(alpha = 0.5f) 
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.rdp())
            .clickable { onClick() }
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.rdp()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 14.rsp(), color = CloverDarker, fontWeight = FontWeight.Normal, fontFamily = Poppins)
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = CloverDarker
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
    val backgroundColor = BackgroundSand

    Box(modifier = Modifier.fillMaxWidth().height(50.rdp()).background(Tertiary.copy(alpha = 0.5f))) {
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
                modifier = Modifier.padding(horizontal = 16.rdp()).weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Original category icons
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
                    modifier = Modifier.size(16.rdp())
                )
                
                Spacer(modifier = Modifier.width(16.rdp()))
                Text(
                    text = drink.data.name,
                    fontSize = 14.rsp(),
                    color = CloverDarker,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Poppins,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
                // Custom Checkbox [X] vs [ ]
                Box(
                    modifier = Modifier
                        .size(20.rdp())
                        .border(0.5.dp, CloverDarker.copy(alpha = 0.5f), RoundedCornerShape(0.dp))
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (drink.data.isSelected) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Selected",
                            modifier = Modifier.size(14.rdp()),
                            tint = CloverDarker
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.rdp()))

                if (showDelete) {
                    Spacer(modifier = Modifier.width(16.rdp()))
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(50.rdp())
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

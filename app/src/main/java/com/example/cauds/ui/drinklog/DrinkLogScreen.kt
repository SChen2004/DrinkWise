package com.example.cauds.ui.drinklog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import java.time.format.DateTimeFormatter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cauds.R
import com.example.cauds.ui.navigation.Screen
import kotlinx.coroutines.launch

/**
 * Main Composable for the Drink Log UI.
 * Connects the UI to the `DrinkLogViewModel` which holds the state data and logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkLogScreen(
    navController: NavController,
    viewModel: DrinkLogViewModel = viewModel()
) {
    // Collects the latest UI state from the ViewModel. Triggers recomposition on change.
    val uiState by viewModel.uiState.collectAsState()
    
    // Derived state to quickly check if any drinks have been logged today
    val hasLogs = uiState.addedDrinks.isNotEmpty()

    // Ensure logs are fetched every time the screen is resumed (e.g. coming back from Manage Drinks)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshLogs()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    // Scaffold provides the standard Material structural layout (topBar, bottomBar, content)
    Scaffold(
        topBar = {
            // The Top navigation bar containing the Back button and Red Dot Chips
            TopAppBar(
                title = {
                    if (uiState.editModeId == null && hasLogs) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val totalSpend = uiState.addedDrinks.sumOf { it.cost }
                            CustomRedDotChip("${uiState.addedDrinks.size} Drinks")
                            Spacer(Modifier.width(12.dp))
                            CustomRedDotChip(String.format("$%.2f Spent", totalSpend))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (uiState.editModeId != null) {
                            viewModel.cancelEdit()
                        } else {
                            navController.popBackStack() 
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, 
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            // The bottom of the screen with the "ADD" button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Fixed "ADD" or "SAVE" Button
                Button(
                    onClick = { 
                        if (uiState.editModeId != null) viewModel.saveDrink()
                        else viewModel.addDrink()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(if (uiState.editModeId != null) "SAVE" else "ADD", color = Color.White, fontSize = 16.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
                }
            }
        }
    ) { innerPadding ->
        // Root container for everything underneath the TopBar but above the BottomBar
        // The Box allows the Toast notification to overlay the scrollable content.
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Main vertically scrollable content column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            
            // 1. Date Selector (< MTH dd >) or Edit Title
            if (uiState.editModeId != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit", 
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left), 
                        contentDescription = "Prev", 
                        modifier = Modifier.size(16.dp).clickable { viewModel.previousDay() }
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        text = uiState.selectedDate,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right), 
                        contentDescription = "Next", 
                        modifier = Modifier.size(16.dp).clickable { viewModel.nextDay() }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Container Pager Carousel
            val availableContainers = uiState.availableContainers
            val pagerState = rememberPagerState(pageCount = { availableContainers.size })
            
            // Sync Pager visually when ViewModel state changes internally (e.g. changing drinks/categories)
            LaunchedEffect(uiState.selectedContainer, availableContainers) {
                val targetIndex = availableContainers.indexOf(uiState.selectedContainer).coerceAtLeast(0)
                if (pagerState.currentPage != targetIndex) {
                    pagerState.scrollToPage(targetIndex)
                }
            }

            // Sync ViewModel when Pager settles on a new swipe
            LaunchedEffect(pagerState.currentPage) {
                if (pagerState.currentPage in availableContainers.indices) {
                    viewModel.selectContainer(pagerState.currentPage)
                }
            }

            // Fixed height boundary so the scrollable Column knows exactly how much space to allocate
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                val pageWidth = 120.dp 
                val horizontalPadding = (maxWidth - pageWidth) / 2
                
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = horizontalPadding), // Centers the selected item
                    pageSpacing = 36.dp // Space between adjacent items
                ) { page ->
                    val container = availableContainers[page]
                    val isSelected = pagerState.currentPage == page
                    
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom // Aligns bottles to the bottom baseline
                    ) {
                        // Dynamic rendering rules based on Category
                        val currentDrink = uiState.availableDrinkTypes.find { it.name == uiState.selectedDrinkType }
                        val category = currentDrink?.category ?: "Beer"
                        
                        val absoluteHeight = when (container.name) {
                            "FLIGHT", "TASTING" -> 120.dp
                            "PINT", "AVERAGE", "STANDARD", "REGULAR" -> 150.dp
                            "PITCHER", "FROZEN", "LARGE" -> 180.dp
                            else -> 160.dp 
                        }
                        
                        val iconRes = when (category) {
                            "Beer" -> R.drawable.ic_beer
                            "Fermented Drinks" -> R.drawable.ic_fermented_drinks
                            "Wine" -> R.drawable.ic_wine
                            "Hard Liquor" -> R.drawable.ic_hard_liquor
                            "Mixed Drinks" -> R.drawable.ic_mixed_drinks
                            else -> R.drawable.ic_beer
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(
                                    id = iconRes
                                ),
                                contentDescription = container.name,
                                modifier = Modifier
                                    .height(absoluteHeight)
                                    .fillMaxWidth()
                                    // Ghosting transparency effect for unselected side containers
                                    .alpha(if (isSelected) 1f else 0.15f), 
                                contentScale = ContentScale.Fit
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Small blue indicator block + Text Name label below the bottle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(24.dp)
                        ) {
                            if (isSelected) {
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFF5A90DE)))
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(
                                text = container.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else Color(0xFFD9D9D9),
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.alpha(if (isSelected) 1f else 0.5f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Drink Type Wheel Picker (Scrollable text: Ale, Cider, etc.)
            DrinkTypeWheel(
                selectedType = uiState.selectedDrinkType,
                availableDrinkTypes = uiState.availableDrinkTypes,
                onDrinkSelected = { viewModel.selectDrink(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Quantity Stepper & Cost Input Text Field Forms
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Stepper Component ([-] 1 [+])
                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(2.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.fillMaxHeight().weight(1f).clickable { viewModel.updateQuantity(-1) }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = uiState.quantity.toString(),
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp
                    )
                    Box(modifier = Modifier.fillMaxHeight().weight(1f).clickable { viewModel.updateQuantity(1) }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(18.dp))
                    }
                }
                
                Spacer(modifier = Modifier.width(24.dp))

                // Numerical Cost Text Field Input ($ 0.00)
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "$", color = Color.LightGray, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                        BasicTextField(
                            value = uiState.costInput, // Raw string backing
                            onValueChange = { newText ->
                                viewModel.updateCostInput(newText)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(
                                // Pure black text vs LightGray hint text
                                color = if(uiState.costInput.isNotEmpty()) Color.Black else Color.LightGray,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.End
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    // Simulated placeholder Hint text when completely empty
                                    if (uiState.costInput.isEmpty()) {
                                        Text(text = "0.00", color = Color.LightGray, fontSize = 16.sp, fontFamily = FontFamily.Monospace, textAlign = TextAlign.End)
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            // 5. Added Drinks List (Visible if not editing and has logs)
            if (uiState.editModeId == null && hasLogs) {
                // Groups entries together based on name + container size (e.g. "Ale_PINT")
                val groupedLogs = uiState.addedDrinks.groupBy { "${it.type}_${it.drinkSize}" }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, Color(0xFFE0E0E0)) // Bounding box stroke around the list
                ) {
                    groupedLogs.values.forEach { logs ->
                        val firstLog = logs.first()
                        val groupTotal = logs.sumOf { it.cost }
                        
                        // Render Top Header for grouping (showing combined metrics)
                        BatchHeaderRow(
                            type = firstLog.type,
                            drinkSize = firstLog.drinkSize,
                            totalCost = groupTotal,
                            deletable = true,
                            onClick = { if (logs.size == 1) viewModel.enterEditMode(firstLog.id) },
                            onRemove = { viewModel.removeBatch(firstLog.type, firstLog.drinkSize) }
                        )
                        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                        
                        if (logs.size > 1) {
                            logs.forEach { log ->
                                LogItemRow(
                                    log = log,
                                    onClick = { viewModel.enterEditMode(log.id) },
                                    onRemove = { viewModel.removeDrink(log.id) }
                                )
                                HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Manage Drinks Section (scrollable with content)
            Text(
                text = "Drink not here? Add your own.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = { navController.navigate(Screen.ManageDrinks.route) },
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                 Text("Manage Drinks >", color = Color.Black, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }

            // Toast overlay
            val toastMessage = if (uiState.showLoggedToast) "Drinks logged." else if (uiState.showDeletedToast) "Drink deleted." else if (uiState.showSavedToast) "Drink saved." else null
            
            if (toastMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(toastMessage, color = Color.Black, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                    Icon(
                        Icons.Default.Close, 
                        contentDescription = "Close", 
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { viewModel.hideToast() }, 
                        tint = Color.LightGray
                    )
                }
            }
        }
    }
}

/**
 * Secondary custom composable for the small pill chips in the Top Bar.
 * Contains a tiny red dot `CircleShape` and trailing descriptive text based on Figma mockups.
 */
@Composable
fun CustomRedDotChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color(0xFFFF5252), CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, fontSize = 12.sp, color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)
        }
    }
}

/**
 * Vertical Pager Component that displays "Ale", "Cider", "Rum" etc.
 * Uses a fixed height showing approximately 3 items and lets users snap-scroll.
 */
@Composable
fun DrinkTypeWheel(selectedType: String, availableDrinkTypes: List<DrinkType>, onDrinkSelected: (String) -> Unit) {
    if (availableDrinkTypes.isEmpty()) return

    val initialPage = remember(availableDrinkTypes, selectedType) {
        availableDrinkTypes.indexOfFirst { it.name == selectedType }.coerceAtLeast(0)
    }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { availableDrinkTypes.size })
    val coroutineScope = rememberCoroutineScope()
    
    // Automatically select the drink when scrolling dynamically changes the visible page
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage in availableDrinkTypes.indices) {
            onDrinkSelected(availableDrinkTypes[pagerState.currentPage].name)
        }
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp), 
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 32.dp)
    ) { page ->
        val drink = availableDrinkTypes[page]
        val isSelected = pagerState.currentPage == page
        
        Text(
            text = drink.name,
            fontSize = if (isSelected) 20.sp else 16.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal, // Bold if center focus
            color = if (isSelected) Color.Black else Color(0xFFD9D9D9),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { 
                    coroutineScope.launch {
                        // Smoothly animates center-selection if user clicks an off-center item
                        pagerState.animateScrollToPage(page)
                    }
                }
                .padding(vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BatchHeaderRow(type: String, drinkSize: String, totalCost: Double, deletable: Boolean, onClick: () -> Unit = {}, onRemove: () -> Unit) {
    var showDelete by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(Color.White)
            .then(
                if (deletable) {
                    Modifier
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                // Swipe left to reveal, swipe right to hide
                                if (dragAmount < -15) showDelete = true
                                else if (dragAmount > 15) showDelete = false
                            }
                        }
                        .clickable { 
                            if (showDelete) showDelete = false 
                            else onClick() 
                        }
                } else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = type, fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = drinkSize.uppercase(),
                color = Color.LightGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
            Text(
                text = String.format("$%.2f", totalCost),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(end = if (showDelete) 0.dp else 16.dp)
            )
            
            if (showDelete) {
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(48.dp)
                        .background(Color(0xFFFF5252))
                        .clickable {
                            showDelete = false
                            onRemove()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Close, "Remove", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogItemRow(log: LogDataWrapper, onClick: () -> Unit, onRemove: () -> Unit) {
    var showDelete by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(Color(0xFFFAFAFA))
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount < -15) showDelete = true
                    else if (dragAmount > 15) showDelete = false
                }
            }
            .clickable { 
                if (showDelete) showDelete = false 
                else onClick() 
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(48.dp)) // Aligns under the text exactly: 32dp + 16dp
            Text(text = log.type, fontSize = 14.sp, color = Color.Black, fontFamily = FontFamily.Monospace)
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
            val costStr = if(log.cost > 0) String.format("$%.2f", log.cost) else "$0.00"
            Text(
                text = costStr,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(end = if (showDelete) 0.dp else 16.dp)
            )
            
            if (showDelete) {
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(48.dp)
                        .background(Color(0xFFFF5252))
                        .clickable {
                            showDelete = false
                            onRemove()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Close, "Remove", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}


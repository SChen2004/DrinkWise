package com.example.cauds.ui.drinklog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cauds.R
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.ui.theme.Roboto
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.BackgroundSand
import com.example.cauds.ui.theme.BowlbyOne
import com.example.cauds.ui.theme.BowlbyOne
import com.example.cauds.ui.navigation.Screen
import kotlinx.coroutines.launch

/**
 * Main Composable for the Drink Log UI.
 * Connects the UI to the `DrinkLogViewModel` which holds the state data and logic.
 */
val LightSectionBlue = Color(0xFFAFC9DC)
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

    val focusManager = LocalFocusManager.current

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
        containerColor = BackgroundSand, // Main sand background for the entire screen
        topBar = {
            // The Top navigation bar containing the Back button and Blue Dot Chips
            TopAppBar(
                title = {
                    if (uiState.editModeId == null && hasLogs) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val totalSpend = uiState.addedDrinks.sumOf { it.cost }
                            CustomBlueDotChip("${uiState.addedDrinks.size} Drinks")
                            Spacer(Modifier.width(12.dp))
                            CustomBlueDotChip(String.format("$%.2f Spent", totalSpend))
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
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
                    .background(BackgroundSand)
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121E30))
                ) {
                    Text(
                        if (uiState.editModeId != null) "Save" else "Add", 
                        color = Color.White, 
                        fontSize = 24.sp, 
                        fontFamily = BigShouldersDisplay, 
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.sp
                    )
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
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
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
                        fontWeight = FontWeight.Normal,
                        fontSize = 32.sp,
                        fontFamily = BigShouldersDisplay,
                        color = Color(0xFF1A3720)
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
                        modifier = Modifier.size(32.dp).clickable { viewModel.previousDay() },
                        tint = Color(0xFF1A3720)
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        text = uiState.selectedDate,
                        fontWeight = FontWeight.Medium,
                        fontSize = 32.sp,
                        fontFamily = BigShouldersDisplay,
                        color = Color(0xFF1A3720)
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right), 
                        contentDescription = "Next", 
                        modifier = Modifier.size(32.dp).clickable { viewModel.nextDay() },
                        tint = Color(0xFF1A3720)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // 2. Container Pager Carousel
            val availableContainers = uiState.availableContainers
            
            // Stable initial page index based on ViewModel state to prevent resets during category switches
            val initialContainerPage = remember(availableContainers, uiState.selectedContainer) {
                availableContainers.indexOf(uiState.selectedContainer).coerceAtLeast(0)
            }
            val pagerState = rememberPagerState(initialPage = initialContainerPage, pageCount = { availableContainers.size })
            
            // Sync Pager visually when ViewModel state changes internally (e.g. changing drinks/categories)
            LaunchedEffect(uiState.selectedContainer, availableContainers) {
                if (!pagerState.isScrollInProgress) {
                    val targetIndex = availableContainers.indexOf(uiState.selectedContainer).coerceAtLeast(0)
                    if (pagerState.currentPage != targetIndex) {
                        pagerState.scrollToPage(targetIndex)
                    }
                }
            }

            // Sync ViewModel when Pager settles on a new swipe
            LaunchedEffect(pagerState.settledPage) {
                if (pagerState.settledPage in availableContainers.indices) {
                    viewModel.selectContainer(pagerState.settledPage)
                }
            }

            // Fixed height boundary so the scrollable Column knows exactly how much space to allocate
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                val pageWidth = 120.dp 
                val horizontalPadding = (maxWidth - pageWidth) / 2

                Box(
                    modifier = Modifier
                        .width(pageWidth)
                        .height(53.dp)
                        .padding(bottom = 19.dp)
                        .background(Color(0xFF3583A4).copy(alpha = 0.3f))
                        .align(Alignment.BottomCenter)
                )
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = horizontalPadding), // Centers the selected item
                    pageSpacing = 48.dp // Space between adjacent items
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
                            "FLIGHT"-> 90.dp
                            "PINT", "REGULAR SHOT", "GLASS", "SINGLE", "REGULAR" -> 110.dp
                            "PITCHER", "BOTTLE", "DOUBLE SHOT", "DOUBLE" -> 130.dp
                            else -> 130.dp 
                        }

                        val absoluteWidth = when (container.name) {
                            "FLIGHT"-> 86.dp
                            "PINT", "REGULAR SHOT", "GLASS", "SINGLE", "REGULAR" -> 106.dp
                            "PITCHER", "BOTTLE", "DOUBLE SHOT", "DOUBLE" -> 125.dp
                            else -> 106.dp 
                        }
                        
                        val iconRes = when (category) {
                            "Beer" -> R.drawable.ic_beer
                            "Fermented" -> R.drawable.ic_fermented
                            "Wine" -> R.drawable.ic_wine
                            "Spirit" -> R.drawable.ic_spirit
                            "Cocktail/Mixed" -> R.drawable.ic_cocktail_mixed
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
                                alignment = Alignment.BottomCenter,
                                modifier = Modifier
                                    .size(width = absoluteWidth, height = absoluteHeight)
                                    // Ghosting transparency effect for unselected side containers
                                    .alpha(if (isSelected) 1f else 0.15f), 
                                contentScale = ContentScale.Fit
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // ext Name label below the bottle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text(
                                text = container.name,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF264168) else Color(0xFF264168).copy(alpha = 0.2f),
                                fontFamily = BowlbyOne,
                                modifier = Modifier.alpha(if (isSelected) 1f else 0.5f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Group 3 & 4 (Wheel + Inputs) into a blue-backgrounded drawer section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightSectionBlue)
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 3. Drink Type Wheel Picker (Scrollable text: Ale, Cider, etc.)
                DrinkTypeWheel(
                    selectedType = uiState.selectedDrinkType,
                    availableDrinkTypes = uiState.availableDrinkTypes,
                    onDrinkSelected = { viewModel.selectDrink(it) }
                )

                Spacer(modifier = Modifier.height(48.dp))

                // 4. Quantity Stepper & Cost Input Text Field Forms
                var isPriceFocused by remember { mutableStateOf(false) }

                Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity Stepper Component ([-] 1 [+]) - Only visible if price not focused
                    if (!isPriceFocused) {
                        Row(
                            modifier = Modifier
                            .height(48.dp)
                            .weight(1f)
                            .border(0.5.dp, Color(0xFF1A3720).copy(alpha = 0.5f), RoundedCornerShape(0.dp)),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(modifier = Modifier.fillMaxHeight().weight(1f).clickable { viewModel.updateQuantity(-1) }, contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(18.dp))
                            }
                            Text(
                                text = uiState.quantity.toString(),
                                fontWeight = FontWeight.Normal,
                                fontFamily = Roboto,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                            Box(modifier = Modifier.fillMaxHeight().weight(1f).clickable { viewModel.updateQuantity(1) }, contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(18.dp))
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(24.dp))
                    }

                    // Numerical Cost Text Field Input ($ 0.00)
                    Box(
                        modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .border(0.5.dp, Color(0xFF1A3720).copy(alpha = 0.5f), RoundedCornerShape(0.dp))
                        .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "$",  color = if(uiState.costInput.isNotEmpty()) Color.Black else Color.Black.copy(alpha = 0.3f), fontSize = 20.sp, fontFamily = Roboto, fontWeight = FontWeight.Normal)
                            BasicTextField(
                                value = uiState.costInput,
                                onValueChange = { newText ->
                                    viewModel.updateCostInput(newText)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(
                                    // Pure black text vs LightGray hint text
                                    color = if(uiState.costInput.isNotEmpty()) Color.Black else Color.Black.copy(alpha = 0.3f),
                                    fontSize = 20.sp,
                                    fontFamily = Roboto,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.End
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { isPriceFocused = it.isFocused },
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        // Simulated placeholder Hint text when completely empty
                                        if (uiState.costInput.isEmpty()) {
                                            Text(text = "0.00", color = Color.Black.copy(alpha = 0.3f), fontSize = 20.sp, fontFamily = Roboto, textAlign = TextAlign.End)
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }
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
                        .border(0.5.dp, Color(0xFF000000).copy(alpha = 0.5f)) // Bounding box stroke around the list
                ) {
                    groupedLogs.values.forEach { logs ->
                        val firstLog = logs.first()
                        val groupTotal = logs.sumOf { it.cost }
                        
                        BatchHeaderRow(
                            type = firstLog.type,
                            drinkSize = firstLog.drinkSize,
                            totalCost = groupTotal,
                            deletable = true,
                            viewModel = viewModel,
                            onClick = { if (logs.size == 1) viewModel.enterEditMode(firstLog.id) },
                            onRemove = { viewModel.removeBatch(firstLog.type, firstLog.drinkSize) },
                            onDuplicate = { viewModel.duplicateDrink(firstLog.id) }
                        )
                        HorizontalDivider(color = Color(0xFF000000).copy(alpha = 0.5f), thickness = 0.5.dp)
                        
                        if (logs.size > 1) {
                            logs.forEach { log ->
                                LogItemRow(
                                    log = log,
                                    onClick = { viewModel.enterEditMode(log.id) },
                                    onRemove = { viewModel.removeDrink(log.id) },
                                    onDuplicate = { viewModel.duplicateDrink(log.id) }
                                )
                                HorizontalDivider(color = Color(0xFF000000).copy(alpha = 0.5f), thickness = 0.5.dp)
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
                color = Color(0xFF1A3720).copy(alpha = 0.5f),
                fontFamily = Poppins,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = { navController.navigate(Screen.ManageDrinks.route) },
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF121E30), containerColor = Color(0xFFAFC9DC))
            ) {
                 Text("Manage Drinks >", color = Color(0xFF121E30), fontSize = 14.sp, fontFamily = Poppins, fontWeight = FontWeight.Normal)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }

            // Toast overlay
            val toastMessage = when {
                uiState.showLoggedToast -> if (uiState.lastToastQuantity > 1) "Drinks logged." else "Drink logged."
                uiState.showDeletedToast -> if (uiState.lastToastQuantity > 1) "Drinks deleted." else "Drink deleted."
                uiState.showSavedToast -> if (uiState.lastToastQuantity > 1) "Drinks saved." else "Drink saved."
                else -> null
            }
            
            if (toastMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .background(BackgroundSand)
                        .border(0.5.dp, Color(0xFF000000), RoundedCornerShape(2.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(toastMessage, color = Color.Black, fontFamily = Poppins, fontSize = 14.sp)
                    Icon(
                        Icons.Default.Close, 
                        contentDescription = "Close", 
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { viewModel.hideToast() }, 
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

/**
 * Secondary custom composable for the small pill chips in the Top Bar.
 * Contains a tiny blue dot `CircleShape` and trailing descriptive text based on Figma mockups.
 */
@Composable
fun CustomBlueDotChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        border = BorderStroke(0.5.dp, Color(0xFF000000).copy(alpha = 0.5f)),
        color = Color(0xFFEDF5EF).copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color(0xFF5900FF), CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, fontSize = 14.sp, color = Color.Black, fontFamily = Poppins, fontWeight = FontWeight.Normal)
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
    LaunchedEffect(pagerState.settledPage) {
        if (pagerState.settledPage in availableDrinkTypes.indices) {
            onDrinkSelected(availableDrinkTypes[pagerState.settledPage].name)
        }
    }

    // Sync pager when external selectedType changes (In Edit Mode)
    LaunchedEffect(selectedType) {
        if (!pagerState.isScrollInProgress) {
            val index = availableDrinkTypes.indexOfFirst { it.name == selectedType }
            if (index != -1 && pagerState.currentPage != index) {
                pagerState.scrollToPage(index)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp), // Fixed height to allow overlay centering
        contentAlignment = Alignment.Center
    ) {
        // Overlay rectangle behind the selected item
        Box(
            modifier = Modifier
                .width(250.dp)
                .height(44.dp)
                .background(Color(0xFF3583A4).copy(alpha = 0.3f), RoundedCornerShape(0.dp))
        )

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
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal, 
                color = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.2f),
                textAlign = TextAlign.Center,
                fontFamily = BigShouldersDisplay,
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
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BatchHeaderRow(
    type: String, 
    drinkSize: String, 
    totalCost: Double, 
    deletable: Boolean, 
    viewModel: DrinkLogViewModel,
    onClick: () -> Unit = {}, 
    onRemove: () -> Unit, 
    onDuplicate: () -> Unit = {}
) {
    // 1. Dynamic category lookup based on drink name
    val uiState by viewModel.uiState.collectAsState()
    val category = uiState.availableDrinkTypes.find { it.name == type }?.category ?: "Beer"

    // 2. Select corresponding icon (ic_{category}_o)
    val iconRes = when (category) {
        "Beer" -> R.drawable.ic_beer_o
        "Wine" -> R.drawable.ic_wine_o
        "Spirit" -> R.drawable.ic_spirit_o
        "Fermented" -> R.drawable.ic_fermented_o
        "Cocktail/Mixed" -> R.drawable.ic_cocktail_mixed_o
        else -> R.drawable.ic_beer_o
    }
    var showDelete by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(BackgroundSand)
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
                            .combinedClickable(
                                onClick = { 
                                    if (showDelete) showDelete = false 
                                    else onClick() 
                                },
                                onLongClick = { showMenu = true }
                            )
                    } else Modifier
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .background(BackgroundSand, CircleShape)
                        .padding(4.dp)
                        //.clip(CircleShape)
)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = type, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1A3720), 
                    fontFamily = Poppins,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = drinkSize.uppercase(),
                    color = Color.Black.copy(alpha = 0.3f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Poppins,
                    maxLines = 1
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
                Text(
                    text = String.format("$%.2f", totalCost),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(end = if (showDelete) 0.dp else 16.dp)
                )
                
                if (showDelete) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(50.dp)
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
        
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.background(Color.White)
        ) {
            DropdownMenuItem(
                text = { Text("Delete", fontFamily = Poppins, fontSize = 14.sp) },
                onClick = {
                    showMenu = false
                    onRemove()
                }
            )
            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            DropdownMenuItem(
                text = { Text("Duplicate", fontFamily = Poppins, fontSize = 14.sp) },
                onClick = {
                    showMenu = false
                    onDuplicate()
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogItemRow(log: LogDataWrapper, onClick: () -> Unit, onRemove: () -> Unit, onDuplicate: () -> Unit = {}) {
    var showDelete by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(Color(0xFFFFEFC6).copy(alpha = 0.5f))
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -15) showDelete = true
                        else if (dragAmount > 15) showDelete = false
                    }
                }
                .combinedClickable(
                    onClick = { 
                        if (showDelete) showDelete = false 
                        else onClick() 
                    },
                    onLongClick = { showMenu = true }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(48.dp))
                Text(
                    text = log.type, 
                    fontSize = 12.sp,
                    color = Color.Black, 
                    fontFamily = Poppins,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
                val costStr = if(log.cost > 0) String.format("$%.2f", log.cost) else "$0.00"
                Text(
                    text = costStr,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Color.Black,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(end = if (showDelete) 0.dp else 16.dp)
                )
                
                if (showDelete) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(34.dp)
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
        
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.background(Color.White)
        ) {
            DropdownMenuItem(
                text = { Text("Delete", fontFamily = Poppins, fontSize = 14.sp) },
                onClick = {
                    showMenu = false
                    onRemove()
                }
            )
            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            DropdownMenuItem(
                text = { Text("Duplicate", fontFamily = Poppins, fontSize = 14.sp) },
                onClick = {
                    showMenu = false
                    onDuplicate()
                }
            )
        }
    }
}


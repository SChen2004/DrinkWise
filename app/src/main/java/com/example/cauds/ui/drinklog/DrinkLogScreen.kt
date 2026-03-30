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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.DpOffset
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cauds.R
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.BackgroundSand
import com.example.cauds.ui.theme.CloverDarker
import com.example.cauds.ui.theme.Tertiary
import com.example.cauds.ui.theme.rdp
import com.example.cauds.ui.theme.rsp
import com.example.cauds.ui.navigation.Screen
import com.example.cauds.ui.theme.BowlbyOne
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
            // THE TOP NAVIGATION BAR (Fixed)
            // Navigation arrow is fixed, while the rest scrolls
            TopAppBar(
                title = { /* Centered chips elsewhere */ },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (uiState.editModeId != null) viewModel.cancelEdit()
                        else navController.popBackStack() 
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_left),
                            contentDescription = "Back",
                            modifier = Modifier.size(20.rdp()),
                            tint = CloverDarker
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
                        .background(BackgroundSand),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalDivider(color = Color(0xFFC0CBDB), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(16.rdp()))
                    // Fixed "ADD" or "SAVE" Button
                    Button(
                        onClick = { 
                            if (uiState.editModeId != null) viewModel.saveDrink()
                            else viewModel.addDrink()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.rdp())
                            .height(44.rdp()),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121E30)),
                        contentPadding = PaddingValues(0.dp) // Prevent clipping of descenders like 'g'
                    ) {
                        Text(
                            if (uiState.editModeId != null) "Save" else "Add", 
                            color = Color.White, 
                            fontSize = 24.rsp(), 
                            style = LocalTextStyle.current.copy(
                                platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = false)
                            ),
                            fontFamily = BigShouldersDisplay, 
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.rsp()
                        )
                    }
                    Spacer(modifier = Modifier.height(24.rdp()))
                }
        }
    ) { innerPadding ->
        // Root container for everything underneath the TopBar but above the BottomBar
        // The Box allows the Toast notification to overlay the scrollable content.
        Box(
            modifier = Modifier
                .padding(bottom = innerPadding.calculateBottomPadding()) // Allow top overlap
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
                // Scrollable Header (Centered Summary Chips)
                // Offset vertically to overlap with the TopAppBar line visually
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(innerPadding.calculateTopPadding()) // Match TopAppBar height
                        .padding(horizontal = 8.rdp()),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.editModeId == null && hasLogs) {
                        val totalSpend = uiState.addedDrinks.sumOf { it.cost }
                        CustomBlueDotChip("${uiState.addedDrinks.size} Drinks")
                        Spacer(modifier = Modifier.width(12.rdp()))
                        CustomBlueDotChip(String.format("$%.2f Spent", totalSpend))
                    }
                }
                
                Spacer(modifier = Modifier.height(8.rdp()))
            
            // 1. Date Selector (< MTH dd >) or Edit Title
            if (uiState.editModeId != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.rdp(), vertical = 16.rdp()),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit", 
                        fontWeight = FontWeight.Normal,
                        fontSize = 32.rsp(),
                        fontFamily = BigShouldersDisplay,
                        color = Color(0xFF1A3720)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.rdp(), vertical = 16.rdp()),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_left),
                        contentDescription = "Prev", 
                        modifier = Modifier.size(24.rdp()).clickable { viewModel.previousDay() },
                        tint = CloverDarker
                    )
                    Spacer(modifier = Modifier.width(32.rdp()))
                    Text(
                        text = uiState.selectedDate,
                        fontWeight = FontWeight.Medium,
                        fontSize = 32.rsp(),
                        fontFamily = BigShouldersDisplay,
                        color = CloverDarker,
                        modifier = Modifier.clickable { navController.navigate(Screen.Calendar.route) }
                    )
                    Spacer(modifier = Modifier.width(32.rdp()))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_right),
                        contentDescription = "Next", 
                        modifier = Modifier
                            .size(24.rdp())
                            .alpha(if (uiState.selectedDateObj >= java.time.LocalDate.now()) 0.2f else 1f)
                            .clickable(enabled = uiState.selectedDateObj < java.time.LocalDate.now()) { 
                                viewModel.nextDay() 
                            },
                        tint = CloverDarker
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.rdp()))

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
                    .height(250.rdp())
            ) {
                val pageWidth = 140.rdp() 
                val horizontalPadding = (maxWidth - pageWidth) / 2

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = horizontalPadding), // Centers the selected item
                    pageSpacing = 36.rdp() // Space between adjacent items
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
                            "FLIGHT"-> 90.rdp()
                            "PINT", "REGULAR SHOT", "GLASS", "SINGLE", "REGULAR" -> 110.rdp()
                            "PITCHER", "BOTTLE", "DOUBLE SHOT", "DOUBLE" -> 130.rdp()
                            else -> 130.rdp() 
                        }

                        val absoluteWidth = when (container.name) {
                            "FLIGHT"-> 86.rdp()
                            "PINT", "REGULAR SHOT", "GLASS", "SINGLE", "REGULAR" -> 106.rdp()
                            "PITCHER", "BOTTLE", "DOUBLE SHOT", "DOUBLE" -> 125.rdp()
                            else -> 106.rdp() 
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
                        
                        if (category != "Fermented") {
                            Spacer(modifier = Modifier.height(16.rdp()))

                            // Dynamic Highlight Box around size name
                            Box(
                                modifier = Modifier
                                    .wrapContentWidth(unbounded = true)
                                    .then(
                                        if (isSelected) Modifier.background(Color(0xFF3583A4).copy(alpha = 0.3f), RoundedCornerShape(0.dp))
                                        else Modifier
                                    )
                                    .padding(horizontal = 10.rdp(), vertical = 6.rdp()),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = container.name,
                                    fontSize = 14.rsp(),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF264168) else Color(0xFF264168).copy(alpha = 0.2f),
                                    fontFamily = BowlbyOne,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.alpha(if (isSelected) 1f else 0.5f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.rdp()))
                        } else {
                            // If Fermented, just add some bottom padding for the icon
                            Spacer(modifier = Modifier.height(64.rdp()))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.rdp()))

            // Group 3 & 4 (Wheel + Inputs) into a blue-backgrounded drawer section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightSectionBlue)
                    .padding(vertical = 32.rdp()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 3. Drink Type Wheel Picker (Scrollable text: Ale, Cider, etc.)
                DrinkTypeWheel(
                    selectedType = uiState.selectedDrinkType,
                    availableDrinkTypes = uiState.availableDrinkTypes,
                    onDrinkSelected = { viewModel.selectDrink(it) }
                )

                Spacer(modifier = Modifier.height(48.rdp()))

                // 4. Quantity Stepper & Cost Input Text Field Forms
                var isPriceFocused by remember { mutableStateOf(false) }

                Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.rdp()),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity Stepper Component ([-] 1 [+]) - Only visible if price not focused
                    if (!isPriceFocused) {
                        Row(
                            modifier = Modifier
                            .height(48.rdp())
                            .weight(1f)
                            .border(0.5.dp, Color(0xFF1A3720).copy(alpha = 0.5f), RoundedCornerShape(0.dp)),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(modifier = Modifier.fillMaxHeight().weight(1f).clickable { viewModel.updateQuantity(-1) }, contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(18.rdp()))
                            }
                            Text(
                                text = uiState.quantity.toString(),
                                fontWeight = FontWeight.Normal,
                                fontFamily = Poppins,
                                fontSize = 18.rsp(),
                                color = Color.Black
                            )
                            Box(modifier = Modifier.fillMaxHeight().weight(1f).clickable { viewModel.updateQuantity(1) }, contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(18.rdp()))
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(24.rdp()))
                    }

                    // Numerical Cost Text Field Input ($ 0.00)
                    Box(
                        modifier = Modifier
                        .height(48.rdp())
                        .weight(1f)
                        .border(0.5.dp, Color(0xFF1A3720).copy(alpha = 0.5f), RoundedCornerShape(0.dp))
                        .padding(horizontal = 16.rdp()),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "$",  color = if(uiState.costInput.isNotEmpty()) Color.Black else Color.Black.copy(alpha = 0.3f), fontSize = 20.rsp(), fontFamily = Poppins, fontWeight = FontWeight.Normal)
                            BasicTextField(
                                value = uiState.costInput,
                                onValueChange = { newText ->
                                    viewModel.updateCostInput(newText)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(
                                    // Pure black text vs LightGray hint text
                                    color = if(uiState.costInput.isNotEmpty()) Color.Black else Color.Black.copy(alpha = 0.3f),
                                    fontSize = 20.rsp(),
                                    fontFamily = Poppins,
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
                                            Text(text = "0.00", color = Color.Black.copy(alpha = 0.3f), fontSize = 20.rsp(), fontFamily = Poppins, textAlign = TextAlign.End)
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.rdp()))
            // 5. Added Drinks List (Visible if not editing and has logs)
            if (uiState.editModeId == null && hasLogs) {
                // Groups entries together based on name + container size (e.g. "Ale_PINT")
                val groupedLogs = uiState.addedDrinks.groupBy { "${it.type}_${it.drinkSize}" }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.rdp())
                        .background(BackgroundSand)
                        .background(Color(0xFFEDF5EF).copy(alpha = 0.5f))
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
                
                Spacer(modifier = Modifier.height(32.rdp()))
            }

            // Manage Drinks Section (scrollable with content)
            Text(
                text = "Drink not here? Add your own.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF1A3720).copy(alpha = 0.5f),
                fontFamily = Poppins,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.rdp()),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.rdp()))
            
            OutlinedButton(
                onClick = { navController.navigate(Screen.ManageDrinks.route) },
                modifier = Modifier.height(36.rdp()),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF121E30), containerColor = Color(0xFFAFC9DC)),
                contentPadding = PaddingValues(
                    start = 12.rdp(),
                    top = 6.rdp(),
                    end = 6.rdp(),
                    bottom = 6.rdp()
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.rdp()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Manage Drinks",
                        color = Color(0xFF1A3720),
                        fontSize = 14.rsp(),
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Normal
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_right),
                        contentDescription = null,
                        modifier = Modifier.size(16.rdp()),
                        tint = Color(0xFF1A3720)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.rdp()))
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
                        .padding(horizontal = 16.rdp(), vertical = 24.rdp())
                        .background(BackgroundSand)
                        .border(0.5.dp, Color(0xFF000000), RoundedCornerShape(2.dp))
                        .padding(horizontal = 16.rdp(), vertical = 12.rdp())
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(toastMessage, color = Color.Black, fontFamily = Poppins, fontSize = 14.rsp())
                    Icon(
                        Icons.Default.Close, 
                        contentDescription = "Close", 
                        modifier = Modifier
                            .size(16.rdp())
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
        shape = RoundedCornerShape(100),
        border = BorderStroke(0.5.dp, CloverDarker.copy(alpha = 0.6f)),
        color = Tertiary
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.rdp(), vertical = 4.rdp()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.rdp())
                    .background(Color(0xFF5900FF), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.rdp()))
            Text(
                text = text, 
                fontSize = 14.rsp(), 
                color = CloverDarker, 
                fontFamily = Poppins, 
                fontWeight = FontWeight.Normal
            )
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

    val containerHeight = 130.rdp()
    val itemHeight = 30.rdp()
    val verticalPadding = (containerHeight - itemHeight) / 2

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
            .height(containerHeight), // Fixed height to allow overlay centering
        contentAlignment = Alignment.Center
    ) {
        // Overlay rectangle behind the selected item
        Box(
            modifier = Modifier
                .width(250.rdp())
                .height(40.rdp())
                .background(Color(0xFF3583A4).copy(alpha = 0.3f), RoundedCornerShape(0.dp))
        )

        VerticalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(containerHeight), 
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = verticalPadding),
            pageSpacing = 12.rdp()
        ) { page ->
            val drink = availableDrinkTypes[page]
            val isSelected = pagerState.currentPage == page
            
            Text(
                text = drink.name,
                fontSize = 24.rsp(),
                fontWeight = FontWeight.Normal, 
                color = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.2f),
                textAlign = TextAlign.Center,
                fontFamily = BigShouldersDisplay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .clickable { 
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page)
                        }
                    }
                    .wrapContentHeight(Alignment.CenterVertically)
            )
        }

        // Gradient shadow overlays at top and bottom edges (to match Figma's fade effect)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        0f to LightSectionBlue,
                        0.15f to Color.Transparent,
                        0.85f to Color.Transparent,
                        1f to LightSectionBlue
                    )
                )
        )
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

    val batchBgColor = Color(0xFFF5F5E5)

    Box(modifier = Modifier.fillMaxWidth().background(batchBgColor)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.rdp())
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
                    .padding(horizontal = 16.rdp()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.rdp())
                        .padding(4.rdp())
                )
                Spacer(modifier = Modifier.width(16.rdp()))
                Text(
                    text = type, 
                    fontSize = 14.rsp(), 
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1A3720), 
                    fontFamily = Poppins,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(8.rdp()))
                Text(
                    text = drinkSize.uppercase(),
                    color = Color.Black.copy(alpha = 0.3f),
                    fontSize = 10.rsp(),
                    fontWeight = FontWeight.Normal,
                    fontFamily = Poppins,
                    maxLines = 1
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
                Text(
                    text = String.format("$%.2f", totalCost),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.rsp(),
                    color = Color.Black,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(end = if (showDelete) 0.dp else 16.rdp())
                )
                
                if (showDelete) {
                    Spacer(modifier = Modifier.width(16.rdp()))
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(50.rdp())
                            .background(Color(0xFFFF5252))
                            .clickable {
                                showDelete = false
                                onRemove()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, "Remove", tint = Color.White, modifier = Modifier.size(20.rdp()))
                    }
                }
            }
        }
        
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.rdp())
                    .size(0.dp)
            ) {
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color(0xFFF5F5E5)),
                    offset = DpOffset(x = (-130).rdp(), y = 0.rdp())
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete", fontFamily = Poppins, fontSize = 14.rsp()) },
                        onClick = {
                            showMenu = false
                            onRemove()
                        }
                    )
                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.rdp())
                    DropdownMenuItem(
                        text = { Text("Duplicate", fontFamily = Poppins, fontSize = 14.rsp()) },
                        onClick = {
                            showMenu = false
                            onDuplicate()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogItemRow(
    log: LogDataWrapper, 
    onClick: () -> Unit, 
    onRemove: () -> Unit, 
    onDuplicate: () -> Unit = {}
) {
    var showDelete by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val logBgColor = Color(0xFFF5F5E5)

    Box(modifier = Modifier.fillMaxWidth().background(logBgColor)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.rdp())
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
                    .padding(horizontal = 16.rdp()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(48.rdp()))
                Text(
                    text = log.type, 
                    fontSize = 12.rsp(),
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
                    fontSize = 12.rsp(),
                    color = Color.Black,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(end = if (showDelete) 0.dp else 16.rdp())
                )
                
                if (showDelete) {
                    Spacer(modifier = Modifier.width(16.rdp()))
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(34.rdp())
                            .background(Color(0xFFFF5252))
                            .clickable {
                                showDelete = false
                                onRemove()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, "Remove", tint = Color.White, modifier = Modifier.size(20.rdp()))
                    }
                }
            }
        }
        
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.rdp())
                    .size(0.dp)
            ) {
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color(0xFFF5F5E5)),
                    offset = DpOffset(x = (-130).rdp(), y = 0.rdp())
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete", fontFamily = Poppins, fontSize = 14.rsp()) },
                        onClick = {
                            showMenu = false
                            onRemove()
                        }
                    )
                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.rdp())
                    DropdownMenuItem(
                        text = { Text("Duplicate", fontFamily = Poppins, fontSize = 14.rsp()) },
                        onClick = {
                            showMenu = false
                            onDuplicate()
                        }
                    )
                }
            }
        }
    }
}


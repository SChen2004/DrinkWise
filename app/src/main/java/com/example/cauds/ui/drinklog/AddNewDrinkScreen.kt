package com.example.cauds.ui.drinklog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.cauds.R
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddNewDrinkScreen(navController: NavController, viewModel: ManageDrinksViewModel) {
    val coroutineScope = rememberCoroutineScope()
    
    var drinkName by remember { mutableStateOf("") }
    

    val sizes = listOf("FLIGHT", "PINT", "PITCHER")

    val categories = listOf("Select Category", "Beer", "Fermented Drinks", "Wine", "Hard Liquor", "Mixed Drinks")
    
    val categoryPagerState = rememberPagerState(pageCount = { categories.size })
    val sizePagerState = rememberPagerState(initialPage = 1, pageCount = { sizes.size })

    var toastMessage by remember { mutableStateOf<String?>(null) }

    fun showToast(msg: String) {
        toastMessage = msg
        coroutineScope.launch {
            delay(3000)
            toastMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
                    onClick = {
                        val selectedCategory = categories[categoryPagerState.currentPage]
                        val selectedSize = sizes[sizePagerState.currentPage]
                        
                        if (drinkName.isBlank()) {
                            showToast("Please input name.")
                        } else if (selectedCategory == "Select Category") {
                            showToast("Please select category.")
                        } else {
                            viewModel.addCustomDrink(drinkName, selectedCategory, selectedSize) {
                                navController.popBackStack()
                            }
                        }
                    },
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Drink Name Input (Underlined, centered)
                TextField(
                    value = drinkName,
                    onValueChange = { drinkName = it },
                    placeholder = { 
                        Text("Add drink name", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.LightGray, fontFamily = FontFamily.Monospace) 
                    },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.LightGray,
                    ),
                    trailingIcon = {
                        if (drinkName.isNotEmpty()) {
                            IconButton(onClick = { drinkName = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    val pageWidth = 120.dp 
                    val horizontalPadding = (maxWidth - pageWidth) / 2
                    
                    HorizontalPager(
                        state = sizePagerState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = horizontalPadding),
                        pageSpacing = 48.dp
                    ) { page ->
                        val sizeName = sizes[page]
                        val isSelected = sizePagerState.currentPage == page
                        
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            val absoluteHeight = when (sizeName) {
                                "FLIGHT" -> 100.dp
                                "PINT" -> 160.dp
                                "PITCHER" -> 200.dp
                                else -> 160.dp 
                            }
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = when (sizeName) {
                                            "FLIGHT" -> R.drawable.ic_flight
                                            "PINT" -> R.drawable.ic_pint
                                            "PITCHER" -> R.drawable.ic_pitcher
                                            else -> R.drawable.ic_pint
                                        }
                                    ),
                                    contentDescription = sizeName,
                                    modifier = Modifier
                                        .height(absoluteHeight)
                                        .fillMaxWidth()
                                        .alpha(if (isSelected) 1f else 0.15f), 
                                    contentScale = ContentScale.Fit,
                                    colorFilter = ColorFilter.tint(
                                        if (isSelected) Color(0xFF6595DD) else Color(0xFFD9D9D9)
                                    )
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
                                    text = sizeName,
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

                Spacer(modifier = Modifier.height(32.dp))

                // Drink Category Roller
                Box(
                    modifier = Modifier.height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    VerticalPager(
                        state = categoryPagerState,
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(vertical = 32.dp)
                    ) { page ->
                        val isCenter = categoryPagerState.currentPage == page
                        // The UI should display "Select Category", "Beer", etc inline and let them scroll together.
                        Text(
                            text = categories[page],
                            fontSize = if (isCenter) 20.sp else 16.sp,
                            fontWeight = if (isCenter) FontWeight.Medium else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace,
                            color = if (isCenter) Color.Black else Color(0xFFD9D9D9),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Toast Overlay
            if (toastMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp) // above save button
                        .padding(horizontal = 16.dp)
                        .background(Color.White, shape = RoundedCornerShape(4.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(toastMessage!!, color = Color.Black, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.Gray,
                            modifier = Modifier.clickable { toastMessage = null }
                        )
                    }
                }
            }
        }
    }
}

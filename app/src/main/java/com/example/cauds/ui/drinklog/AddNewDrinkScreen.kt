package com.example.cauds.ui.drinklog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.ui.theme.BackgroundSand
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
    

    val categories = listOf("Select Category", "Beer", "Fermented", "Wine", "Spirit", "Cocktail/Mixed")
    
    val categoryPagerState = rememberPagerState(pageCount = { categories.size })

    var toastMessage by remember { mutableStateOf<String?>(null) }

    fun showToast(msg: String) {
        toastMessage = msg
        coroutineScope.launch {
            delay(3000)
            toastMessage = null
        }
    }

    val backgroundColor = BackgroundSand

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        bottomBar = {
            val selectedCategory = categories[categoryPagerState.currentPage]
            val canSave = drinkName.isNotBlank() && selectedCategory != "Select Category"
            val saveButtonColor = if (canSave) Color(0xFF121E30) else Color(0xFF121E30).copy(alpha = 0.5f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(horizontal = 24.dp)
                    .padding(vertical = 16.dp)
            ) {
                Button(
                    onClick = {
                        if (canSave) {
                            val defaultSize = when (selectedCategory) {
                                "Beer" -> "PINT"
                                "Wine" -> "GLASS"
                                "Fermented" -> "REGULAR"
                                "Spirit" -> "SINGLE SHOT"
                                "Cocktail/Mixed" -> "SINGLE"
                                else -> "REGULAR"
                            }
                            viewModel.addCustomDrink(drinkName, selectedCategory, defaultSize) {
                                // Completion callback
                            }
                            navController.popBackStack()
                        } else {
                            if (drinkName.isBlank()) {
                                showToast("Please input name.")
                            } else {
                                showToast("Please select category.")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = saveButtonColor)
                ) {
                    Text("Save", color = Color.White, fontWeight = FontWeight.Normal, fontFamily = BigShouldersDisplay, letterSpacing = 0.sp)
                }
            }
        },
        containerColor = backgroundColor
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
                        Text("Add drink name", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.Gray.copy(alpha = 0.5f), fontFamily = BigShouldersDisplay, fontSize = 32.sp) 
                    },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Normal, fontFamily = BigShouldersDisplay, fontSize = 32.sp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Black.copy(alpha = 0.3f),
                        unfocusedIndicatorColor = Color.Black.copy(alpha = 0.3f),
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

                val currentCategory = categories[categoryPagerState.currentPage]
                val iconRes = when (currentCategory) {
                    "Beer" -> R.drawable.ic_beer
                    "Fermented" -> R.drawable.ic_fermented
                    "Wine" -> R.drawable.ic_wine
                    "Spirit" -> R.drawable.ic_spirit
                    "Cocktail/Mixed" -> R.drawable.ic_cocktail_mixed
                    else -> R.drawable.ic_beer 
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = currentCategory,
                        modifier = Modifier
                            .size(160.dp),
                        colorFilter = if (currentCategory == "Select Category") ColorFilter.tint(Color(0xFFE8E1CE)) else null,
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Drink Category Roller
                Box(
                    modifier = Modifier.height(100.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // Highlight Box for selected item
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(36.dp)
                            .background(Color(0x1A3583A4))
                    )

                    VerticalPager(
                        state = categoryPagerState,
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(vertical = 32.dp)
                    ) { page ->
                        val isCenter = categoryPagerState.currentPage == page
                        val categoryName = categories[page]
                        val displayText = if (isCenter && categoryName == "Select Category") "—  $categoryName  —" else categoryName
                        
                        Text(
                            text = displayText,
                            fontSize = if (isCenter) 20.sp else 16.sp,
                            fontWeight = if (isCenter) FontWeight.Normal else FontWeight.Normal,
                            fontFamily = BigShouldersDisplay,
                            color = if (isCenter) Color.Black else Color.Black.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Toast Overlay
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
                    Text(toastMessage!!, color = Color.Black, fontSize = 14.sp, fontFamily = Poppins)
                    Icon(
                        Icons.Default.Close, 
                        contentDescription = "Close", 
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { toastMessage = null},
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

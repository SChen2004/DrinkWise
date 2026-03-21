package com.example.cauds.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cauds.R
import com.example.cauds.ui.navigation.Screen
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.Poppins


private val CreamBackground   = Color(0xFFFEF5DC)
private val DarkGreen          = Color(0xFF1A3720)
private val DeselectedTint     = Color(0x1A000000)
private val ButtonActive       = Color(0xFF121E30)
private val ButtonDisabled     = Color(0xFFC0CBDB)


private data class DrinkOption(
    val label: String,
    val iconRes: Int,
    val selectedTint: Color,
    val rotation: Float = 0f
)

private val drinkOptions = listOf(
    DrinkOption("Wine",            R.drawable.ic_wine,            Color(0xFF33578A)),
    DrinkOption("Beer",            R.drawable.ic_beer,            Color(0xFFAFC9DC)),
    DrinkOption("Spirits",         R.drawable.ic_spirit,          Color(0xFF4A9D5B)),
    DrinkOption("Cocktail",        R.drawable.ic_cocktail_mixed,  Color(0xFFFAAAA5)),
    DrinkOption("Cider / Seltzer", R.drawable.ic_fermented,       Color(0xFFFFCB46), rotation = -45f)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteDrinksScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    var selectedOptions by remember { mutableStateOf(setOf<String>()) }

    // ── Responsive sizing ─────────────────────────────────────
    // LocalConfiguration gives us the screen height in dp.
    // 1080x2400 at typical density ≈ 800+ dp tall, so that's
    // the "normal" baseline. Below 700 dp we shrink things down
    // so nothing gets clipped or crushed on shorter screens.
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val isSmall = screenHeight < 700

    val iconSize       = if (isSmall) 70.dp else 80.dp
    val rowGap         = if (isSmall) 10.dp  else 16.dp
    val cellWidth      = if (isSmall) 120.dp else 120.dp
    val bottomPadding  = if (isSmall) 16.dp else 24.dp
    val skipFontSize   = if (isSmall) 24.sp else 24.sp

    Scaffold(
        containerColor = CreamBackground,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(top = 24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = DarkGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = bottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Almost done.",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = DarkGreen
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "What do you usually drink?",
                    fontFamily = BigShouldersDisplay,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = DarkGreen
                )

                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.weight(1f))

                drinkOptions.take(4).chunked(2).forEach { rowPair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowPair.forEach { drink ->
                            DrinkCell(
                                drink = drink,
                                isSelected = selectedOptions.contains(drink.label),
                                maxReached = selectedOptions.size >= 3,
                                iconSize = iconSize,
                                cellWidth = cellWidth,
                                onClick = {
                                    val alreadySelected = selectedOptions.contains(drink.label)
                                    if (alreadySelected) {
                                        selectedOptions = selectedOptions - drink.label
                                    } else if (selectedOptions.size < 3) {
                                        selectedOptions = selectedOptions + drink.label
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(rowGap))
                }

                val lastDrink = drinkOptions.last()
                DrinkCell(
                    drink = lastDrink,
                    isSelected = selectedOptions.contains(lastDrink.label),
                    maxReached = selectedOptions.size >= 3,
                    iconSize = iconSize,
                    cellWidth = cellWidth,
                    onClick = {
                        val alreadySelected = selectedOptions.contains(lastDrink.label)
                        if (alreadySelected) {
                            selectedOptions = selectedOptions - lastDrink.label
                        } else if (selectedOptions.size < 3) {
                            selectedOptions = selectedOptions + lastDrink.label
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = Color(0xFF121E30).copy(alpha = 0.2f)
            )

            Button(
                onClick = {
                    viewModel.saveFavouriteDrinks(selectedOptions.toList())
                    val isFromAccount =
                        navController.previousBackStackEntry?.destination?.route == Screen.Account.route
                    if (isFromAccount) {
                        navController.popBackStack()
                    } else {
                        viewModel.completeOnboarding()
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo("onboarding_name") { inclusive = true }
                        }
                    }
                },
                enabled = selectedOptions.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp)
                    .height(44.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonActive,
                    disabledContainerColor = ButtonDisabled
                )
            ) {
                Text(
                    text = "Done",
                    fontFamily = BigShouldersDisplay,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
            }

            TextButton(
                onClick = {
                    val isFromAccount =
                        navController.previousBackStackEntry?.destination?.route == Screen.Account.route
                    if (isFromAccount) {
                        navController.popBackStack()
                    } else {
                        viewModel.completeOnboarding()
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo("onboarding_name") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
            ) {
                Text(
                    text = "Skip",
                    fontFamily = BigShouldersDisplay,
                    fontSize = skipFontSize,
                    fontWeight = FontWeight.Normal,
                    color = DarkGreen
                )
            }
        }
    }
}


@Composable
private fun DrinkCell(
    drink: DrinkOption,
    isSelected: Boolean,
    maxReached: Boolean,
    iconSize: androidx.compose.ui.unit.Dp,
    cellWidth: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val tint = if (isSelected) drink.selectedTint else DeselectedTint
    val labelColor = when {
        isSelected -> DarkGreen
        maxReached -> Color(0x44000000)
        else       -> DarkGreen
    }

    Column(
        modifier = Modifier
            .width(cellWidth)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = drink.iconRes),
            contentDescription = drink.label,
            modifier = Modifier
                .size(iconSize)
                .rotate(drink.rotation),
            colorFilter = ColorFilter.tint(tint)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = drink.label.uppercase(),
            fontFamily = Poppins,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = labelColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
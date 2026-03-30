package com.example.cauds.ui.learning

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cauds.R
import com.example.cauds.data.model.LinkTile
import com.example.cauds.data.model.Subject
import com.example.cauds.ui.navigation.Screen
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.BowlbyOne
import com.example.cauds.ui.theme.Poppins

// ── Colors ──────────────────────────────────────────────────────
private val Cream = Color(0xFFFEF5DC)
private val DarkNavy = Color(0xFF121E30)
private val Clover = Color(0xFF1A3720)
private val LinkTileBackground = Color(0xFFAFC9DC)
private val LinkTileDescriptionColor = Color(0xFF33578A)

// ── Reusable tile card ──────────────────────────────────────────
// Used for both Article and Subject tiles. Accepts raw fields
// so it doesn't depend on any specific data model.
@Composable
fun LearningTileCard(
    title: String,
    description: String,
    imageRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        // Title row — text on left, arrow on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontFamily = BigShouldersDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                color = Clover,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = Clover
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = description,
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = Clover
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Image — FillWidth preserves full aspect ratio
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ── Support link card ───────────────────────────────────────────
// Opens a URL in the browser when tapped. Blue background card
// with a diagonal arrow icon.
@Composable
fun SupportLinkCard(
    linkTile: LinkTile,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LinkTileBackground)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkTile.url))
                context.startActivity(intent)
            }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = linkTile.title,
                fontFamily = BigShouldersDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = DarkNavy,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_outward),
                contentDescription = "Open link",
                tint = DarkNavy
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = linkTile.description,
            fontFamily = Poppins,
            fontSize = 12.sp,
            color = LinkTileDescriptionColor,
            lineHeight = 18.sp
        )
    }
}

// ── Learning landing page ───────────────────────────────────────
@Composable
fun LearningPageScreen(
    navController: NavController,
    viewModel: LearningViewModel
) {
    val understandingAud = Subject(
        title = "Understanding AUD",
        description = "Learn more about AUD, how it affects your health, where to get help, facts about alcohol, and how you can reduce your intake.",
        imageRes = R.drawable.aud_couch,
        route = Screen.UnderstandingAud.route
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream),
        contentPadding = PaddingValues(28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Page heading ────────────────────────────────────────
        item {
            Text(
                text = "Increase\nyour\nknowledge\nof AUD",
                fontFamily = BowlbyOne,
                fontSize = 52.sp,
                color = DarkNavy,
                lineHeight = 52.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── Understanding AUD subject tile ──────────────────────
        item {
            LearningTileCard(
                title = understandingAud.title,
                description = understandingAud.description,
                imageRes = understandingAud.imageRes,
                onClick = { navController.navigate(understandingAud.route) }
            )
        }

        // ── Support Links section ───────────────────────────────
        item {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Support Links",
                fontFamily = BigShouldersDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                color = Clover
            )
            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Clover)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(supportLinks) { link ->
            SupportLinkCard(linkTile = link)
        }
    }
}
package com.example.cauds.ui.learning

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cauds.data.model.ArticleBlock
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.BowlbyOne
import com.example.cauds.ui.theme.Poppins

private val Cream = Color(0xFFFEF5DC)
private val DarkNavy = Color(0xFF121E30)
private val Clover = Color(0xFF1A3720)
private val NoteYellow = Color(0xFFFFCB46)

// ── Single block renderer ───────────────────────────────────────
// nested = true reduces horizontal padding for blocks inside expandables,
// preventing double-indentation (expandable already has its own padding).
@Composable
fun ArticleBlockContent(block: ArticleBlock, nested: Boolean = false) {
    val hPadding = if (nested) 12.dp else 28.dp
    val listStartPadding = if (nested) 20.dp else 36.dp

    when (block.type) {
        "text" -> Text(
            text = block.content ?: "",
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = Clover,
            modifier = Modifier.padding(horizontal = hPadding, vertical = 8.dp)
        )

        "subheading" -> Text(
            text = block.content ?: "",
            fontFamily = BigShouldersDisplay,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Clover,
            modifier = Modifier.padding(horizontal = hPadding, vertical = 8.dp)
        )

        "note" -> Text(
            text = block.content ?: "",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = DarkNavy,
            lineHeight = 16.sp,
            modifier = Modifier
                .padding(horizontal = hPadding, vertical = 8.dp)
                .fillMaxWidth()
                .background(NoteYellow)
                .padding(12.dp)
        )

        "numbered_list" -> Column(
            modifier = Modifier.padding(start = listStartPadding, end = hPadding, top = 8.dp, bottom = 8.dp)
        ) {
            block.items?.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "${index + 1}. ",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Clover
                    )
                    Text(
                        text = item,
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Clover
                    )
                }
            }
        }

        "bullet_list" -> Column(
            modifier = Modifier.padding(start = listStartPadding, end = hPadding, top = 8.dp, bottom = 8.dp)
        ) {
            block.items?.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "•  ",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Clover
                    )
                    Text(
                        text = item,
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Clover
                    )
                }
            }
        }

        "image" -> {
            val context = LocalContext.current
            val resId = context.resources.getIdentifier(
                block.content ?: block.name ?: "",
                "drawable",
                context.packageName
            )
            if (resId != 0) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = block.content ?: block.name,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = hPadding, vertical = 8.dp)
                )
            }
        }

        "expandable" -> ExpandableBlock(
            title = block.name ?: "",
            content = block.content,
            blocks = block.blocks,
            modifier = Modifier.padding(horizontal = hPadding, vertical = 8.dp)
        )

        "tinyHeading" -> Text(
            text = block.content ?: "",
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Clover,
            modifier = Modifier.padding(start = hPadding, end = hPadding, top = 8.dp, bottom = 0.dp)
        )
    }
}

// ── Expandable accordion block ──────────────────────────────────
// Supports two modes:
// 1. Simple: "content" string → renders as plain text (backward compatible)
// 2. Nested: "blocks" array → renders each block recursively using ArticleBlockContent
@Composable
fun ExpandableBlock(
    title: String,
    content: String?,
    blocks: List<ArticleBlock>?,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, Clover)
            .clickable { expanded = !expanded }
    ) {
        // Title row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontFamily = BigShouldersDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = Clover,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (expanded) "−" else "+",
                fontFamily = BigShouldersDisplay,
                fontSize = 24.sp,
                color = Clover
            )
        }

        // Body — check for nested blocks first, fall back to plain content string
        AnimatedVisibility(visible = expanded) {
            Column {
                HorizontalDivider(thickness = 0.5.dp, color = Clover)

                if (!blocks.isNullOrEmpty()) {
                    // Nested blocks — rendered with nested = true so padding is reduced
                    blocks.forEach { block ->
                        ArticleBlockContent(block, nested = true)
                    }
                } else if (!content.isNullOrEmpty()) {
                    // Simple string content — backward compatible with old JSON
                    Text(
                        text = content,
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Clover,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

// ── Article reader screen ───────────────────────────────────────
@Composable
fun ArticleScreen(
    navController: NavController,
    viewModel: LearningViewModel,
) {
    val article = viewModel.currentArticle
    val blocks = viewModel.currentArticleBlocks

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        article?.let { a ->
            // Article image — full width
            item {
                Image(
                    painter = painterResource(id = a.imageRes),
                    contentDescription = a.title,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Back button — below the image
            item {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DarkNavy
                    )
                }
            }

            // Article title
            item {
                Text(
                    text = a.title,
                    fontFamily = BowlbyOne,
                    fontSize = 50.sp,
                    color = DarkNavy,
                    lineHeight = 52.sp,
                    modifier = Modifier.padding(horizontal = 28.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Article body — each block rendered by ArticleBlockContent
        // nested defaults to false, so top-level blocks get full 28dp padding
        items(blocks) { block ->
            ArticleBlockContent(block)
        }
    }
}
package lk.happypaws.app.ui.post

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lk.happypaws.app.ui.components.HappyPawsBackButton
import lk.happypaws.app.ui.post.model.CommunityPostType
import lk.happypaws.app.ui.theme.Neutral20
import lk.happypaws.app.ui.theme.Neutral60
import lk.happypaws.app.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTypeSelectionScreen(
    onNavigateBack: () -> Unit,
    onPostTypeSelected: (CommunityPostType) -> Unit,
    viewModel: CreatePostViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Post",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp, end = 8.dp)) {
                        HappyPawsBackButton(onClick = onNavigateBack)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header Section
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "What would you like to create?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Neutral20,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Select a post type below to begin. Your progress is saved as you work through the wizard.",
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral60,
                lineHeight = 20.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Post Type Cards
            CommunityPostType.entries.forEach { postType ->
                val isSelected = uiState.selectedPostType == postType
                PostTypeCard(
                    postType = postType,
                    isSelected = isSelected,
                    onClick = {
                        viewModel.selectPostType(postType)
                        onPostTypeSelected(postType)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PostTypeCard(
    postType: CommunityPostType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) postType.accentColor else Color(0xFFE5E7EB),
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Row: Combined Icon + Category Badge Widget & Chevron
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Combined Icon & Category Badge Pill Widget
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = postType.iconBackgroundColor
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = postType.icon,
                            contentDescription = null,
                            tint = postType.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = postType.categoryBadge,
                            style = MaterialTheme.typography.labelMedium,
                            color = postType.accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Arrow indicator
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(postType.iconBackgroundColor.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = "Select",
                        tint = postType.accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Post Type Title
            Text(
                text = postType.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Neutral20,
                fontSize = 17.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description (strictly left-aligned)
            Text(
                text = postType.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral60,
                lineHeight = 20.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Highlight Features
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.Start
            ) {
                postType.features.forEach { feature ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = postType.accentColor.copy(alpha = 0.85f),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodySmall,
                            color = Neutral20.copy(alpha = 0.75f),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}


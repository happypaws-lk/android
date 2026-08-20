package lk.happypaws.app.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lk.happypaws.app.ui.components.HappyPawsBackButton
import lk.happypaws.app.ui.post.model.CommunityPostType
import lk.happypaws.app.ui.theme.Neutral20
import lk.happypaws.app.ui.theme.Neutral60

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostPlaceholderScreen(
    postType: CommunityPostType,
    onNavigateBack: () -> Unit,
    viewModel: CreatePostViewModel,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = postType.screenTitle,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(postType.iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = postType.icon,
                        contentDescription = postType.title,
                        tint = postType.accentColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = postType.screenTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Neutral20
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Post creation form for ${postType.title} will appear here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral60,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CreateAdoptionListingScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreatePostViewModel,
    modifier: Modifier = Modifier
) {
    CreatePostPlaceholderScreen(
        postType = CommunityPostType.ADOPTION_LISTING,
        onNavigateBack = onNavigateBack,
        viewModel = viewModel,
        modifier = modifier
    )
}

@Composable
fun CreateRescueReportScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreatePostViewModel,
    modifier: Modifier = Modifier
) {
    CreatePostPlaceholderScreen(
        postType = CommunityPostType.RESCUE_REPORT,
        onNavigateBack = onNavigateBack,
        viewModel = viewModel,
        modifier = modifier
    )
}

@Composable
fun CreateTransportRequestScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreatePostViewModel,
    modifier: Modifier = Modifier
) {
    CreatePostPlaceholderScreen(
        postType = CommunityPostType.TRANSPORT_REQUEST,
        onNavigateBack = onNavigateBack,
        viewModel = viewModel,
        modifier = modifier
    )
}

@Composable
fun CreateCommunityStoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreatePostViewModel,
    modifier: Modifier = Modifier
) {
    CreatePostPlaceholderScreen(
        postType = CommunityPostType.COMMUNITY_STORY,
        onNavigateBack = onNavigateBack,
        viewModel = viewModel,
        modifier = modifier
    )
}

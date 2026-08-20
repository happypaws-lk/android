package lk.happypaws.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import lk.happypaws.app.ui.components.HappyPawsBackButton
import lk.happypaws.app.ui.home.CommunityPostCard
import lk.happypaws.app.ui.home.CommunityViewModel
import lk.happypaws.app.ui.navigation.AppNavKey

@OptIn(ExperimentalMaterial3Api::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Composable
fun CommunityActivityScreen(
    onNavigateBack: () -> Unit,
    onNavigateTo: (AppNavKey) -> Unit = {},
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val mySortOption by viewModel.mySortOption.collectAsStateWithLifecycle()
    val myPosts = viewModel.myPostsFlow.collectAsLazyPagingItems()
    var sortExpanded by remember { mutableStateOf(false) }
    val sortOptions = listOf("Recent", "Top - Daily", "Top - Monthly", "Top - Yearly", "Top - All Time", "Pending")

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Community Activity",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp, end = 8.dp)) {
                        HappyPawsBackButton(onClick = onNavigateBack)
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        OutlinedButton(
                            onClick = { sortExpanded = true },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(text = mySortOption, style = MaterialTheme.typography.labelMedium)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = sortExpanded,
                            onDismissRequest = { sortExpanded = false }
                        ) {
                            sortOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.updateMySortOption(option)
                                        sortExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(myPosts.itemCount) { index ->
                val post = myPosts[index]
                if (post != null) {
                    var upvotes by remember(post.id) { mutableIntStateOf(post.upvotes) }
                    var isUpvoted by remember(post.id) { mutableStateOf(post.isUpvotedByMe) }
                    
                    CommunityPostCard(
                        post = post.copy(upvotes = upvotes, isUpvotedByMe = isUpvoted),
                        onClick = { onNavigateTo(AppNavKey.PostDetail(post.id, post.type.name)) },
                        onUpvote = {
                            isUpvoted = !isUpvoted
                            upvotes += if (isUpvoted) 1 else -1
                            viewModel.toggleUpvote(post)
                        },
                        isMine = true,
                        onDelete = {
                            viewModel.deletePost(post) {
                                myPosts.refresh()
                            }
                        }
                    )
                }
            }

            myPosts.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    loadState.append is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    loadState.refresh is LoadState.Error -> {
                        item {
                            Text(
                                text = "Error loading your posts",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

package lk.happypaws.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppSkeletonScreen(
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoggedIn) {
            HomeSkeletonContent()
        } else {
            OnboardingSkeletonContent()
        }
    }
}

@Composable
fun HomeSkeletonContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header Skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shimmerEffect(CircleShape)
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(18.dp)
                            .shimmerEffect(RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(12.dp)
                            .shimmerEffect(RoundedCornerShape(4.dp))
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .shimmerEffect(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .shimmerEffect(RoundedCornerShape(23.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Chips Skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) { index ->
                val chipWidth = if (index % 2 == 0) 72.dp else 88.dp
                Box(
                    modifier = Modifier
                        .width(chipWidth)
                        .height(32.dp)
                        .shimmerEffect(RoundedCornerShape(16.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Post Card Skeletons
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(2) {
                PostCardSkeleton()
            }
        }

        // Bottom Navigation Bar Skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .shimmerEffect(CircleShape)
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shimmerEffect(CircleShape)
            )
            repeat(2) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .shimmerEffect(CircleShape)
                )
            }
        }
    }
}

@Composable
private fun PostCardSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .shimmerEffect(CircleShape)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .height(14.dp)
                            .shimmerEffect(RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(10.dp)
                            .shimmerEffect(RoundedCornerShape(4.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .shimmerEffect(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Post caption lines
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(14.dp)
                    .shimmerEffect(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .shimmerEffect(RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun OnboardingSkeletonContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.15f))

        // Hero Illustration Placeholder
        Box(
            modifier = Modifier
                .size(220.dp)
                .shimmerEffect(RoundedCornerShape(24.dp))
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Title Placeholder
        Box(
            modifier = Modifier
                .width(240.dp)
                .height(28.dp)
                .shimmerEffect(RoundedCornerShape(6.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description Placeholders
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(14.dp)
                .shimmerEffect(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(14.dp)
                .shimmerEffect(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(14.dp)
                .shimmerEffect(RoundedCornerShape(4.dp))
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Page Indicator Dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 24.dp, height = 8.dp)
                    .shimmerEffect(RoundedCornerShape(4.dp))
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .shimmerEffect(CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .shimmerEffect(CircleShape)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Button Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shimmerEffect(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom link skeleton
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(16.dp)
                .shimmerEffect(RoundedCornerShape(4.dp))
        )
    }
}

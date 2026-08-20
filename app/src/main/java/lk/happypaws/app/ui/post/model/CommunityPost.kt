package lk.happypaws.app.ui.post.model

import java.time.LocalDateTime

data class CommunityPost(
    val id: String,
    val type: CommunityPostType,
    val title: String,
    val content: String,
    val authorName: String,
    val authorReputation: Int,
    val authorAvatarUrl: String?,
    val upvotes: Int,
    val isUpvotedByMe: Boolean,
    val createdAt: LocalDateTime,
    val photoUrl: String? = null,
    val location: String? = null,
    val isPending: Boolean = false
)

package lk.happypaws.app.ui.home

import lk.happypaws.app.ui.post.model.CommunityPost
import retrofit2.http.GET
import retrofit2.http.Query

interface CommunityApi {
    @GET("api/v1/community")
    suspend fun getCommunityPosts(
        @Query("sort") sort: String,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): PagedResponse<CommunityPostDto>

    @GET("api/v1/community/me")
    suspend fun getMyCommunityPosts(
        @Query("sort") sort: String,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): PagedResponse<CommunityPostDto>
}

data class PagedResponse<T>(
    val items: List<T>,
    val totalCount: Int,
    val pageIndex: Int,
    val pageSize: Int
)

data class CommunityPostDto(
    val id: String,
    val type: String,
    val title: String,
    val content: String,
    val authorName: String,
    val authorReputation: Int,
    val upvotes: Int,
    val isUpvotedByMe: Boolean,
    val createdAt: String,
    val isPending: Boolean
)

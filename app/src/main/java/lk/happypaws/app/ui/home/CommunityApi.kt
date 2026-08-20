package lk.happypaws.app.ui.home

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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

    @POST("api/v1/community/{targetType}/{id}/upvote")
    suspend fun toggleUpvote(
        @Path("targetType") targetType: String,
        @Path("id") id: String
    ): UpvoteResponse

    @DELETE("api/v1/community/{targetType}/{id}")
    suspend fun deletePost(
        @Path("targetType") targetType: String,
        @Path("id") id: String
    )

    @GET("api/v1/community/{targetType}/{id}")
    suspend fun getPostById(
        @Path("targetType") targetType: String,
        @Path("id") id: String
    ): CommunityPostDto
}

data class UpvoteResponse(
    val upvotes: Int,
    val isUpvotedByMe: Boolean
)

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
    val imageUrl: String?,
    val isPending: Boolean
)

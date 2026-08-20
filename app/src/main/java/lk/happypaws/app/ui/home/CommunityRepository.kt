package lk.happypaws.app.ui.home

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow
import lk.happypaws.app.ui.post.model.CommunityPost
import lk.happypaws.app.ui.post.model.CommunityPostType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepository @Inject constructor(
    private val api: CommunityApi
) {

    fun getCommunityPosts(sortOption: String): Flow<PagingData<CommunityPost>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { CommunityPagingSource(api, sortOption, false) }
        ).flow
    }

    fun getMyCommunityPosts(sortOption: String): Flow<PagingData<CommunityPost>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { CommunityPagingSource(api, sortOption, true) }
        ).flow
    }

    suspend fun toggleUpvote(type: String, id: String): UpvoteResponse {
        return api.toggleUpvote(type, id)
    }

    suspend fun deletePost(type: String, id: String) {
        api.deletePost(type, id)
    }

    suspend fun getPostById(type: String, id: String): CommunityPost {
        val dto = api.getPostById(type, id)
        return CommunityPost(
            id = dto.id,
            type = try { CommunityPostType.valueOf(dto.type) } catch (e: Exception) { CommunityPostType.COMMUNITY_STORY },
            title = dto.title,
            content = dto.content,
            authorName = dto.authorName,
            authorReputation = dto.authorReputation,
            authorAvatarUrl = null,
            upvotes = dto.upvotes,
            isUpvotedByMe = dto.isUpvotedByMe,
            createdAt = LocalDateTime.parse(dto.createdAt, DateTimeFormatter.ISO_DATE_TIME),
            photoUrl = dto.imageUrl,
            isPending = dto.isPending
        )
    }
}

class CommunityPagingSource(
    private val api: CommunityApi,
    private val sortOption: String,
    private val isMyPosts: Boolean
) : PagingSource<Int, CommunityPost>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommunityPost> {
        return try {
            val page = params.key ?: 1
            
            val response = if (isMyPosts) {
                api.getMyCommunityPosts(sortOption, page, params.loadSize)
            } else {
                api.getCommunityPosts(sortOption, page, params.loadSize)
            }
            
            val posts = response.items.map { dto ->
                CommunityPost(
                    id = dto.id,
                    type = try { CommunityPostType.valueOf(dto.type) } catch (e: Exception) { CommunityPostType.COMMUNITY_STORY },
                    title = dto.title,
                    content = dto.content,
                    authorName = dto.authorName,
                    authorReputation = dto.authorReputation,
                    authorAvatarUrl = null,
                    upvotes = dto.upvotes,
                    isUpvotedByMe = dto.isUpvotedByMe,
                    createdAt = LocalDateTime.parse(dto.createdAt, DateTimeFormatter.ISO_DATE_TIME),
                    photoUrl = dto.imageUrl,
                    isPending = dto.isPending
                )
            }

            LoadResult.Page(
                data = posts,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (posts.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CommunityPost>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

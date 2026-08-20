package lk.happypaws.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import lk.happypaws.app.ui.post.model.CommunityPost
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val repository: CommunityRepository
) : ViewModel() {

    private val _sortOption = MutableStateFlow("Recent")
    val sortOption: StateFlow<String> = _sortOption.asStateFlow()

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val postsFlow = _sortOption.flatMapLatest { sort ->
        repository.getCommunityPosts(sort)
    }.cachedIn(viewModelScope)

    private val _mySortOption = MutableStateFlow("Recent")
    val mySortOption: StateFlow<String> = _mySortOption.asStateFlow()

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val myPostsFlow = _mySortOption.flatMapLatest { sort ->
        repository.getMyCommunityPosts(sort)
    }.cachedIn(viewModelScope)

    fun updateMySortOption(newSort: String) {
        _mySortOption.value = newSort
    }

    fun updateSortOption(newSort: String) {
        _sortOption.value = newSort
    }
}

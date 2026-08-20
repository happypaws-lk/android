package lk.happypaws.app.ui.post

import lk.happypaws.app.ui.post.model.CommunityPostType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CreatePostViewModelTest {

    private lateinit var viewModel: CreatePostViewModel

    @Before
    fun setUp() {
        viewModel = CreatePostViewModel()
    }

    @Test
    fun initialState_hasNoSelectedPostType() {
        val state = viewModel.uiState.value
        assertNull(state.selectedPostType)
        assertEquals("", state.title)
        assertEquals("", state.description)
    }

    @Test
    fun selectPostType_updatesStateCorrectly() {
        viewModel.selectPostType(CommunityPostType.ADOPTION_LISTING)
        assertEquals(CommunityPostType.ADOPTION_LISTING, viewModel.uiState.value.selectedPostType)

        viewModel.selectPostType(CommunityPostType.RESCUE_REPORT)
        assertEquals(CommunityPostType.RESCUE_REPORT, viewModel.uiState.value.selectedPostType)

        viewModel.selectPostType(CommunityPostType.COMMUNITY_STORY)
        assertEquals(CommunityPostType.COMMUNITY_STORY, viewModel.uiState.value.selectedPostType)
    }

    @Test
    fun updateDraft_persistsDraftAcrossWizard() {
        viewModel.selectPostType(CommunityPostType.ADOPTION_LISTING)
        viewModel.updateTitle("Golden Retriever Puppy")
        viewModel.updateDescription("Vaccinated and friendly puppy looking for home.")
        viewModel.updateLocation("Colombo, Sri Lanka")

        val state = viewModel.uiState.value
        assertEquals(CommunityPostType.ADOPTION_LISTING, state.selectedPostType)
        assertEquals("Golden Retriever Puppy", state.title)
        assertEquals("Vaccinated and friendly puppy looking for home.", state.description)
        assertEquals("Colombo, Sri Lanka", state.locationName)
    }

    @Test
    fun reset_clearsWizardState() {
        viewModel.selectPostType(CommunityPostType.RESCUE_REPORT)
        viewModel.updateTitle("Injured Stray")
        viewModel.reset()

        val state = viewModel.uiState.value
        assertNull(state.selectedPostType)
        assertEquals("", state.title)
        assertEquals("", state.description)
    }
}

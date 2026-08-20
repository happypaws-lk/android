package lk.happypaws.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.happypaws.app.data.remote.model.ActivityLevel
import lk.happypaws.app.data.remote.model.HomeSize
import lk.happypaws.app.data.remote.model.LifestyleProfileRequest
import lk.happypaws.app.data.remote.model.LifestyleProfileResponse
import lk.happypaws.app.data.remote.model.MeProfileResponse
import lk.happypaws.app.domain.repository.UserRepository
import java.util.Locale
import javax.inject.Inject

data class LifestyleProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isVerified: Boolean = true,
    val meProfile: MeProfileResponse? = null,
    val homeSize: HomeSize = HomeSize.House,
    val activityLevel: ActivityLevel = ActivityLevel.Moderate,
    val hasChildren: Boolean = false,
    val hasYard: Boolean = false,
    val existingPetTypes: List<String> = emptyList(),
    val customPetInput: String = "",
    val updatedAt: String? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class LifestyleProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LifestyleProfileUiState())
    val uiState: StateFlow<LifestyleProfileUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 1. Fetch Me profile to get isVerified status and user details
            val meResult = userRepository.fetchMeProfile()
            val meProfile = meResult.getOrNull() ?: userRepository.getMeProfileStream().value
            val isVerified = meProfile?.isVerified ?: true

            // 2. Fetch Lifestyle Profile
            val lifestyleResult = userRepository.getLifestyleProfile()
            lifestyleResult.fold(
                onSuccess = { profile ->
                    if (profile != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isVerified = isVerified,
                                meProfile = meProfile,
                                homeSize = profile.homeSize ?: HomeSize.House,
                                activityLevel = profile.activityLevel ?: ActivityLevel.Moderate,
                                hasChildren = profile.hasChildren,
                                hasYard = profile.hasYard,
                                existingPetTypes = profile.existingPetTypes ?: emptyList(),
                                updatedAt = profile.updatedAt,
                                errorMessage = null
                            )
                        }
                    } else {
                        // User has no lifestyle profile yet; default values are ready
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isVerified = isVerified,
                                meProfile = meProfile,
                                errorMessage = null
                            )
                        }
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isVerified = isVerified,
                            meProfile = meProfile,
                            errorMessage = error.message ?: "Failed to load lifestyle profile"
                        )
                    }
                }
            )
        }
    }

    fun onHomeSizeSelected(homeSize: HomeSize) {
        _uiState.update { it.copy(homeSize = homeSize, errorMessage = null) }
    }

    fun onActivityLevelSelected(activityLevel: ActivityLevel) {
        _uiState.update { it.copy(activityLevel = activityLevel, errorMessage = null) }
    }

    fun onHasChildrenChanged(hasChildren: Boolean) {
        _uiState.update { it.copy(hasChildren = hasChildren, errorMessage = null) }
    }

    fun onHasYardChanged(hasYard: Boolean) {
        _uiState.update { it.copy(hasYard = hasYard, errorMessage = null) }
    }

    fun onCustomPetInputChange(input: String) {
        _uiState.update { it.copy(customPetInput = input, errorMessage = null) }
    }

    fun addPetType(petType: String) {
        val trimmed = petType.trim()
        if (trimmed.isBlank()) return

        val capitalized = trimmed.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        val currentList = _uiState.value.existingPetTypes
        if (currentList.size >= 10) {
            _uiState.update { it.copy(errorMessage = "You can add up to 10 existing pets") }
            return
        }

        if (currentList.any { it.equals(capitalized, ignoreCase = true) }) {
            _uiState.update { it.copy(customPetInput = "") }
            return
        }

        val updated = currentList + capitalized
        _uiState.update {
            it.copy(
                existingPetTypes = updated,
                customPetInput = "",
                errorMessage = null
            )
        }
    }

    fun removePetType(petType: String) {
        val updated = _uiState.value.existingPetTypes.filterNot { it.equals(petType, ignoreCase = true) }
        _uiState.update { it.copy(existingPetTypes = updated, errorMessage = null) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val state = _uiState.value

        val request = LifestyleProfileRequest(
            homeSize = state.homeSize,
            activityLevel = state.activityLevel,
            existingPetTypes = if (state.existingPetTypes.isEmpty()) null else state.existingPetTypes,
            hasChildren = state.hasChildren,
            hasYard = state.hasYard
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            userRepository.upsertLifestyleProfile(request).fold(
                onSuccess = { response ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isSuccess = true,
                            homeSize = response.homeSize ?: it.homeSize,
                            activityLevel = response.activityLevel ?: it.activityLevel,
                            hasChildren = response.hasChildren,
                            hasYard = response.hasYard,
                            existingPetTypes = response.existingPetTypes ?: emptyList(),
                            updatedAt = response.updatedAt,
                            errorMessage = null
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Failed to save lifestyle profile"
                        )
                    }
                }
            )
        }
    }
}

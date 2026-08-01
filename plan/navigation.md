# Navigation Graph & Flow Maps

## 1. Routing Strategy
The HappyPaws Android app uses Jetpack Navigation Compose with `kotlinx.serialization` for type-safe route definitions. 

## 2. Route Definitions (Type-Safe)

```kotlin
sealed interface Route {
    // Auth & Onboarding Flow
    @Serializable data object Splash : Route
    @Serializable data object Login : Route
    @Serializable data object RegisterRoleSelection : Route
    @Serializable data class RegisterDetails(val roleId: String) : Route
    @Serializable data class ForgotPasswordEmail(val initialEmail: String? = null) : Route
    @Serializable data class ForgotPasswordOtp(val email: String) : Route
    @Serializable data class ForgotPasswordReset(val email: String, val otpToken: String) : Route
    @Serializable data object ForgotPasswordSuccess : Route
    @Serializable data object KycUpload : Route
    
    // Main App (Bottom Navigation Graph)
    @Serializable data object HomeMap : Route
    @Serializable data object AdoptionCatalog : Route
    @Serializable data object CreateListing : Route
    @Serializable data object ChatList : Route
    @Serializable data object Profile : Route
    
    // Sub-flows
    @Serializable data object ReportStrayLocation : Route
    @Serializable data class ReportStrayTriage(val lat: Double, val lng: Double, val photoUri: String) : Route
    @Serializable data class RescueCaseDetail(val rescueId: String) : Route
    
    @Serializable data class PetDetail(val listingId: String) : Route
    @Serializable data object MatchingQuiz : Route
    @Serializable data class AdoptionApplication(val listingId: String) : Route
    
    @Serializable data class ChatConversation(val conversationId: String) : Route
    @Serializable data object Notifications : Route
}
```

## 3. Role-Based Access Guards

When navigating to restricted routes, a `NavigationInterceptor` (or standard `LaunchedEffect` in the ViewModel) checks the user's decoded JWT role and Verification Status (which is fetched from DataStore).

- **Unverified Users**: Blocked from `ReportStrayLocation`, `CreateListing`, `AdoptionApplication`, `ChatConversation`. Prompted with an Alert Dialog suggesting they complete their KYC.
- **Transporters**: Navigating to a `RescueCaseDetail` shows the "Claim Transport" FAB.
- **Fosters**: Navigating to a `RescueCaseDetail` shows the "Accept Placement" FAB.
- **Vets**: Show AI Triage override buttons on `ReportStrayTriage` and `RescueCaseDetail`.

## 4. Bottom Sheet Navigation
Certain flows will use `ModalBottomSheetLayout` rather than full-screen transitions to maintain context:
- `MatchingQuiz` modal overlaying the `AdoptionCatalog`.
- Sorting/Filtering bottom sheet on `AdoptionCatalog`.

import os

SCREENS = [
    {
        "filename": "forgot_password_step3_reset.md",
        "title": "Forgot Password Flow - Step 3: Set New Password",
        "id": "ForgotPasswordResetScreen",
        "flow": "Step 3 of the Forgot Password flow.",
        "route": "Route.ForgotPasswordReset(val email: String, val otpToken: String)",
        "access": "Public.",
        "api": "POST /api/v1/auth/reset-password",
        "prompt": "UI Design of a Set New Password screen for a dark-mode app. Background charcoal (#1E1E24). Bold white text 'Set a new password'. Two input fields 'Password' and 'Confirm Password' filled with slate grey (#2B2B36). Cyan button 'Update Password' at the bottom."
    },
    {
        "filename": "forgot_password_step4_success.md",
        "title": "Forgot Password Flow - Step 4: Success",
        "id": "ForgotPasswordSuccessScreen",
        "flow": "Final Step of Forgot Password.",
        "route": "Route.ForgotPasswordSuccess",
        "access": "Public.",
        "api": "None",
        "prompt": "UI Design of a Success screen for password reset. Dark charcoal background. A large cyan circular checkmark icon in the center. White text 'Successful'. Grey subtext 'Your password has been changed. Click continue to login'. Cyan button 'Back to Login'."
    },
    {
        "filename": "register_step1_role_selection.md",
        "title": "Registration Flow - Step 1: Role Selection",
        "id": "RegisterRoleSelectionScreen",
        "flow": "Step 1 of Registration.",
        "route": "Route.RegisterRoleSelection",
        "access": "Public.",
        "api": "None",
        "prompt": "UI Design of a Role Selection screen for a pet rescue app. Dark mode. 'Choose your role' header. A vertical list of large cards for Adopter, Foster, Transporter, Veterinarian. Each card has an icon and is slate grey, but the selected one has a cyan border."
    },
    {
        "filename": "register_step2_email_password.md",
        "title": "Registration Flow - Step 2: Details",
        "id": "RegisterDetailsScreen",
        "flow": "Step 2 of Registration.",
        "route": "Route.RegisterDetails(val roleId: String)",
        "access": "Public.",
        "api": "POST /api/v1/auth/register",
        "prompt": "UI Design of a Registration form. Dark mode. Inputs for Full Name, Email, Password. Cyan 'Sign Up' button."
    },
    {
        "filename": "kyc_upload_screen.md",
        "title": "KYC Identity Verification Screen",
        "id": "KycUploadScreen",
        "flow": "Required after registration before user can perform actions.",
        "route": "Route.KycUpload",
        "access": "Authenticated users (Unverified status)",
        "api": "PUT /api/v1/users/me/kyc (MinIO file upload)",
        "prompt": "UI Design of a KYC Document Upload screen. Dark mode. 'Verify your identity' header. A large dashed-border drop zone for taking a photo of National ID. Cyan 'Submit for Review' button."
    },
    {
        "filename": "home_feed_screen.md",
        "title": "Home Feed & Rescue Radar",
        "id": "HomeMapScreen",
        "flow": "Main landing screen after login.",
        "route": "Route.HomeMap",
        "access": "Authenticated users.",
        "api": "GET /api/v1/rescues (with geo-bounds)",
        "prompt": "UI Design of a Home screen map for a pet rescue app. A dark themed map dominates the screen. Several pins are on the map: most are cyan, but one is a pulsing neon pink 'Critical' pin. A bottom sheet peeks up showing a summary of the selected rescue."
    },
    {
        "filename": "report_stray_step1_location.md",
        "title": "Report Stray - Step 1: Location & Photo",
        "id": "ReportStrayLocationScreen",
        "flow": "Step 1 of reporting an injured animal.",
        "route": "Route.ReportStrayLocation",
        "access": "Verified users only.",
        "api": "None",
        "prompt": "UI Design for reporting a stray animal. Dark mode. A camera viewfinder area takes up the top half. Below is a map snippet showing current GPS location. Cyan 'Next' button."
    },
    {
        "filename": "report_stray_step2_triage.md",
        "title": "Report Stray - Step 2: AI Triage",
        "id": "ReportStrayTriageScreen",
        "flow": "Step 2 of reporting.",
        "route": "Route.ReportStrayTriage(val lat: Double, val lng: Double, val photoUri: String)",
        "access": "Verified users only.",
        "api": "POST /api/v1/rescues",
        "prompt": "UI Design of an AI Triage result screen. Dark mode. The uploaded photo is shown with a neon pink 'CRITICAL URGENCY' badge overlaid. Text explains the Gemini AI assessment. A pink 'Broadcast Emergency' button at the bottom."
    },
    {
        "filename": "rescue_case_detail_screen.md",
        "title": "Rescue Case Detail Screen",
        "id": "RescueCaseDetailScreen",
        "flow": "Viewing a specific rescue case.",
        "route": "Route.RescueCaseDetail(val rescueId: String)",
        "access": "Verified users.",
        "api": "GET /api/v1/rescues/{id}",
        "prompt": "UI Design of a Rescue Case Detail screen. Dark mode. Large animal photo at top. Status timeline below it (Reported -> Accepted -> In Transit). Cyan FAB floating at bottom right for Fosters to 'Accept Placement'."
    },
    {
        "filename": "adoption_catalog_screen.md",
        "title": "Adoption Hub Catalog",
        "id": "AdoptionCatalogScreen",
        "flow": "Main tab for browsing adoptable pets.",
        "route": "Route.AdoptionCatalog",
        "access": "Authenticated users.",
        "api": "GET /api/v1/listings",
        "prompt": "UI Design of an Adoption Catalog screen. Dark mode. A masonry grid of pet photos. Each photo card has the pet's name, breed, and a small cyan badge if it's a 'High Match'. A search/filter bar at the top."
    },
    {
        "filename": "pet_detail_screen.md",
        "title": "Pet Listing Detail Screen",
        "id": "PetDetailScreen",
        "flow": "Viewing a specific pet listing.",
        "route": "Route.PetDetail(val listingId: String)",
        "access": "Authenticated users.",
        "api": "GET /api/v1/listings/{id}",
        "prompt": "UI Design of a Pet Detail screen. Dark mode. Image carousel of a dog at the top. Below, details on age, breed, and temperament. A wide cyan 'Apply to Adopt' button at the bottom."
    },
    {
        "filename": "matching_quiz_screen.md",
        "title": "Lifestyle Matching Quiz",
        "id": "MatchingQuizScreen",
        "flow": "Wizard to build adopter profile.",
        "route": "Route.MatchingQuiz",
        "access": "Authenticated Adopters.",
        "api": "PUT /api/v1/users/me/profile",
        "prompt": "UI Design of a Lifestyle Matching quiz. Dark mode. Question: 'What is your home size?'. Three large selectable cards: 'Apartment', 'House without yard', 'House with yard'. A cyan progress bar at the top."
    },
    {
        "filename": "adoption_application_screen.md",
        "title": "Adoption Application Screen",
        "id": "AdoptionApplicationScreen",
        "flow": "Applying for a pet.",
        "route": "Route.AdoptionApplication(val listingId: String)",
        "access": "Verified Adopters.",
        "api": "POST /api/v1/applications",
        "prompt": "UI Design of an Adoption Application form. Dark mode. Input fields for describing experience with pets and why they want this specific dog. Cyan 'Submit Application' button."
    },
    {
        "filename": "create_listing_screen.md",
        "title": "Create Adoption Listing",
        "id": "CreateListingScreen",
        "flow": "Owners/Fosters listing a pet.",
        "route": "Route.CreateListing",
        "access": "Verified Fosters / Owners.",
        "api": "POST /api/v1/listings",
        "prompt": "UI Design of a Create Listing screen. Dark mode. Photo upload grid at the top. Form fields for pet name, breed, age, and a bio text area. Cyan 'Publish Listing' button."
    },
    {
        "filename": "chat_list_screen.md",
        "title": "Conversations List",
        "id": "ChatListScreen",
        "flow": "Main tab for inbox.",
        "route": "Route.ChatList",
        "access": "Verified users.",
        "api": "GET /api/v1/conversations",
        "prompt": "UI Design of an Inbox/Chat List screen. Dark mode. A vertical list of chat threads. Each row shows an avatar, name, and preview of the last message. Unread threads have a small cyan dot."
    },
    {
        "filename": "chat_detail_screen.md",
        "title": "Chat Conversation Screen",
        "id": "ChatConversationScreen",
        "flow": "Private messaging.",
        "route": "Route.ChatConversation(val conversationId: String)",
        "access": "Verified users.",
        "api": "GET /api/v1/conversations/{id}/messages",
        "prompt": "UI Design of a Chat Conversation screen. Dark mode. Chat bubbles; sender messages are cyan (#4CE5E5) with black text, received messages are slate grey (#2B2B36) with white text. Message input field at bottom."
    },
    {
        "filename": "profile_screen.md",
        "title": "User Profile & Reputation",
        "id": "ProfileScreen",
        "flow": "Main tab for user profile.",
        "route": "Route.Profile",
        "access": "Authenticated users.",
        "api": "GET /api/v1/users/me",
        "prompt": "UI Design of a User Profile screen. Dark mode. Avatar at top with a cyan 'VERIFIED FOSTER' pill badge below it. A section showing 'Reputation Points: 450' in large text. A list of unlocked trust badges (e.g. '10 Rescues Completed')."
    },
    {
        "filename": "sponsorship_case_list.md",
        "title": "Sponsorship / Pledge Dashboard",
        "id": "SponsorshipCaseListScreen",
        "flow": "For sponsors to track pledges.",
        "route": "Route.SponsorshipCases",
        "access": "Verified Sponsors.",
        "api": "GET /api/v1/pledge/api/v1/pledges/me",
        "prompt": "UI Design of a Sponsorship Tracking dashboard. Dark mode. List of rescue cases the user has pledged to. Each card shows the animal's photo, the amount pledged, and the current recovery status timeline."
    },
    {
        "filename": "notifications_screen.md",
        "title": "Notifications List",
        "id": "NotificationsScreen",
        "flow": "Accessed from bell icon on Home.",
        "route": "Route.Notifications",
        "access": "Authenticated users.",
        "api": "GET /api/v1/notification/api/v1/notifications",
        "prompt": "UI Design of a Notifications screen. Dark mode. A list of notification items: 'Your application was accepted!', 'Emergency stray reported nearby'. Unread items have a cyan background tint."
    }
]

TEMPLATE = """# {title}

## 1. Screen Context & Flow Position
- **Screen Name & ID**: `{id}`
- **Flow Position**: {flow}
- **Route & Arguments**: `{route}`
- **Access & Permissions**: {access}

## 2. UI/UX & Layout Blueprint
- **Layout & Structure**: `Scaffold` container. Uses `EdgeToEdge` padding.
- **Component Placement & Spacing**: Standard 16dp horizontal padding. Elements spaced vertically by 8dp and 16dp.
- **Colors & Styling**: Deep Charcoal (`#1E1E24`) background. Surface cards in Slate Grey (`#2B2B36`). Primary actions in Vibrant Cyan (`#4CE5E5`).
- **Light & Dark Mode Adaptations**: Dark mode strictly enforced per Design System.
- **Micro-Interactions**: Hover/press animations on interactive elements.

## 3. API Integration & Backend Contract
- **Target Endpoint(s)**: `{api}`
- **Request/Response**: Utilizes standard DTO mapping.
- **Error Handling**: Displays `Snackbar` on network failure. Shimmer effect during data fetch.

## 4. ViewModel & Logic Code (Kotlin)
```kotlin
data class {id}UiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface {id}UiEvent {{
    object LoadData : {id}UiEvent
}}

@HiltViewModel
class {id}ViewModel @Inject constructor(
    private val repository: DefaultRepository
) : ViewModel() {{
    private val _state = MutableStateFlow({id}UiState())
    val state = _state.asStateFlow()
    
    // Logic handles data loading, validation, and API execution.
}}
```

## 5. Jetpack Compose UI Code (Kotlin)
```kotlin
@Composable
fun {id}(
    viewModel: {id}ViewModel = hiltViewModel(),
    onNavigate: (Route) -> Unit
) {{
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        containerColor = Color(0xFF1E1E24)
    ) {{ padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)
        ) {{
            // Screen content rendered based on state
            if (state.isLoading) {{
                CircularProgressIndicator(color = Color(0xFF4CE5E5))
            }}
        }}
    }}
}}
```

## 6. AI UI Design Generation Prompt
> {prompt}
"""

def generate_screens():
    base_dir = r"c:\Users\Nethmina\happypaws-lk\android\plan\screens"
    for screen in SCREENS:
        filepath = os.path.join(base_dir, screen["filename"])
        content = TEMPLATE.format(
            title=screen["title"],
            id=screen["id"],
            flow=screen["flow"],
            route=screen["route"],
            access=screen["access"],
            api=screen["api"],
            prompt=screen["prompt"]
        )
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"Created: {{screen['filename']}}")

if __name__ == "__main__":
    generate_screens()

package lk.happypaws.app.ui.post.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Emergency
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
enum class CommunityPostType(
    val title: String,
    val categoryBadge: String,
    val description: String,
    val screenTitle: String,
    val features: List<String>
) {
    ADOPTION_LISTING(
        title = "Adoption Listing",
        categoryBadge = "Rehome & Adopt",
        description = "Find a verified and loving permanent home for a rescue pet or animal in your care.",
        screenTitle = "Create Adoption Listing",
        features = listOf(
            "Verified adopter matchmaking",
            "Lifestyle compatibility scoring",
            "Secure in-app adopter messaging"
        )
    ),
    RESCUE_REPORT(
        title = "Emergency Rescue",
        categoryBadge = "Urgent Alert",
        description = "Report an injured, trapped, or endangered stray animal for immediate field response.",
        screenTitle = "Report Emergency Rescue",
        features = listOf(
            "AI-powered photo urgency triage",
            "Automatic GPS location capture",
            "Real-time alerts to nearby responders"
        )
    ),
    TRANSPORT_REQUEST(
        title = "Transport Request",
        categoryBadge = "Rescue Logistics",
        description = "Coordinate transport for rescued animals to foster homes, vet clinics, or adopters.",
        screenTitle = "Create Transport Request",
        features = listOf(
            "Pickup & destination route planning",
            "Broadcast to verified volunteer drivers",
            "Live transfer updates & status tracking"
        )
    ),
    COMMUNITY_STORY(
        title = "Community Story",
        categoryBadge = "Updates & Advice",
        description = "Share a heartwarming rescue update, celebrate an adoption, or offer community guidance.",
        screenTitle = "Create Community Story",
        features = listOf(
            "Photo & video story updates",
            "Builds community trust & reputation",
            "Engagement & community feedback"
        )
    );

    val icon: ImageVector
        get() = when (this) {
            ADOPTION_LISTING -> Icons.Rounded.Pets
            RESCUE_REPORT -> Icons.Rounded.Emergency
            TRANSPORT_REQUEST -> Icons.Rounded.LocalShipping
            COMMUNITY_STORY -> Icons.Rounded.AutoAwesome
        }

    val accentColor: Color
        get() = when (this) {
            ADOPTION_LISTING -> Color(0xFF008585) // Primary Brand Teal
            RESCUE_REPORT -> Color(0xFFE11D48)   // Vibrant Crimson/Rose Alert
            TRANSPORT_REQUEST -> Color(0xFFD97706) // Warm Amber / Logistics Gold
            COMMUNITY_STORY -> Color(0xFF7C3AED) // Deep Violet
        }

    val containerColor: Color
        get() = when (this) {
            ADOPTION_LISTING -> Color(0xFFF0FDF4).copy(alpha = 0.85f)
            RESCUE_REPORT -> Color(0xFFFFF1F2).copy(alpha = 0.85f)
            TRANSPORT_REQUEST -> Color(0xFFFFFBEB).copy(alpha = 0.85f)
            COMMUNITY_STORY -> Color(0xFFF5F3FF).copy(alpha = 0.85f)
        }

    val iconBackgroundColor: Color
        get() = when (this) {
            ADOPTION_LISTING -> Color(0xFFE6F4F4)
            RESCUE_REPORT -> Color(0xFFFFE4E6)
            TRANSPORT_REQUEST -> Color(0xFFFEF3C7)
            COMMUNITY_STORY -> Color(0xFFEDE9FE)
        }
}


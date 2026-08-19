package lk.happypaws.app.ui.navigation

import kotlinx.serialization.Serializable

sealed interface HomeNavKey {
    @Serializable
    data object CommunityTab : HomeNavKey

    @Serializable
    data object NearbyTab : HomeNavKey

    @Serializable
    data object ChatsTab : HomeNavKey

    @Serializable
    data object ProfileTab : HomeNavKey
}

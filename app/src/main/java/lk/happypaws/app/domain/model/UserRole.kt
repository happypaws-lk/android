package lk.happypaws.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole(val value: Int) {
    ADOPTER(0),
    FOSTER(1),
    TRANSPORTER(2),
    SPONSOR(3),
    VETERINARIAN(4),
    ADMIN(5)
}

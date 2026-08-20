package lk.happypaws.app.domain.model

enum class RoleRequestStatus(val apiValue: String) {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    companion object {
        fun from(value: String): RoleRequestStatus =
            entries.firstOrNull { it.apiValue == value } ?: PENDING
    }
}

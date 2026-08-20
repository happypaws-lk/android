package lk.happypaws.app.domain.model

enum class DocumentType(val apiValue: String, val displayName: String) {
    NIC("Nic", "National Identity Card (NIC)"),
    PASSPORT("Passport", "Passport"),
    LICENSE("License", "Driving License"),
    CLINIC_REG("ClinicReg", "Veterinary / Clinic Registration");

    companion object {
        fun validFor(role: UserRole): List<DocumentType> = when (role) {
            UserRole.FOSTER       -> listOf(NIC, PASSPORT)
            UserRole.TRANSPORTER  -> listOf(LICENSE)
            UserRole.SPONSOR      -> listOf(NIC, PASSPORT)
            UserRole.VETERINARIAN -> listOf(CLINIC_REG)
            else                  -> listOf(NIC, PASSPORT)
        }

        fun fromApiValue(value: String): DocumentType =
            entries.firstOrNull { it.apiValue == value } ?: NIC
    }
}

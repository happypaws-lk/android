package lk.happypaws.app.data.remote.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.lang.reflect.Type

enum class HomeSize(val displayName: String, val description: String) {
    @SerializedName("Apartment")
    Apartment("Apartment / Flat", "Compact indoor living space, ideal for low to moderate energy pets"),

    @SerializedName("House")
    House("Single House", "Standard detached home with dedicated living areas and rooms"),

    @SerializedName("Estate")
    Estate("Estate / Acreage", "Large property with extensive indoor space and surrounding grounds");

    companion object {
        fun fromStringOrOrdinal(value: Any?): HomeSize {
            if (value == null) return House
            if (value is Number) {
                return when (value.toInt()) {
                    0 -> Apartment
                    1 -> House
                    2 -> Estate
                    else -> House
                }
            }
            val str = value.toString().trim()
            return when {
                str.equals("Apartment", ignoreCase = true) || str == "0" -> Apartment
                str.equals("House", ignoreCase = true) || str == "1" -> House
                str.equals("Estate", ignoreCase = true) || str == "2" -> Estate
                else -> House
            }
        }
    }
}

class HomeSizeAdapter : JsonDeserializer<HomeSize>, JsonSerializer<HomeSize> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): HomeSize {
        if (json == null || json.isJsonNull) return HomeSize.House
        return try {
            if (json.isJsonPrimitive && json.asJsonPrimitive.isNumber) {
                HomeSize.fromStringOrOrdinal(json.asInt)
            } else {
                HomeSize.fromStringOrOrdinal(json.asString)
            }
        } catch (_: Exception) {
            HomeSize.House
        }
    }

    override fun serialize(src: HomeSize?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.name ?: HomeSize.House.name)
    }
}

enum class ActivityLevel(val displayName: String, val description: String) {
    @SerializedName("Low")
    Low("Relaxed & Calm", "Gentle indoor lifestyle, quiet routines, and occasional leisurely strolls"),

    @SerializedName("Moderate")
    Moderate("Moderately Active", "Regular daily walks, interactive playtime, and a balanced household tempo"),

    @SerializedName("High")
    High("High Energy", "Frequent outdoor runs, energetic play, hiking adventures, and high stamina routines");

    companion object {
        fun fromStringOrOrdinal(value: Any?): ActivityLevel {
            if (value == null) return Moderate
            if (value is Number) {
                return when (value.toInt()) {
                    0 -> Low
                    1 -> Moderate
                    2 -> High
                    else -> Moderate
                }
            }
            val str = value.toString().trim()
            return when {
                str.equals("Low", ignoreCase = true) || str == "0" -> Low
                str.equals("Moderate", ignoreCase = true) || str == "1" -> Moderate
                str.equals("High", ignoreCase = true) || str == "2" -> High
                else -> Moderate
            }
        }
    }
}

class ActivityLevelAdapter : JsonDeserializer<ActivityLevel>, JsonSerializer<ActivityLevel> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ActivityLevel {
        if (json == null || json.isJsonNull) return ActivityLevel.Moderate
        return try {
            if (json.isJsonPrimitive && json.asJsonPrimitive.isNumber) {
                ActivityLevel.fromStringOrOrdinal(json.asInt)
            } else {
                ActivityLevel.fromStringOrOrdinal(json.asString)
            }
        } catch (_: Exception) {
            ActivityLevel.Moderate
        }
    }

    override fun serialize(src: ActivityLevel?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.name ?: ActivityLevel.Moderate.name)
    }
}

@Serializable
data class LifestyleProfileRequest(
    @SerializedName("homeSize")
    val homeSize: HomeSize,

    @SerializedName("activityLevel")
    val activityLevel: ActivityLevel,

    @SerializedName("existingPetTypes")
    val existingPetTypes: List<String>?,

    @SerializedName("hasChildren")
    val hasChildren: Boolean,

    @SerializedName("hasYard")
    val hasYard: Boolean
)

@Serializable
data class LifestyleProfileResponse(
    @SerializedName("homeSize")
    val homeSize: HomeSize? = HomeSize.House,

    @SerializedName("activityLevel")
    val activityLevel: ActivityLevel? = ActivityLevel.Moderate,

    @SerializedName("existingPetTypes")
    val existingPetTypes: List<String>? = emptyList(),

    @SerializedName("hasChildren")
    val hasChildren: Boolean = false,

    @SerializedName("hasYard")
    val hasYard: Boolean = false,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

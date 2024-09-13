package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Categories(
    @SerialName("categories") val types: List<Tag>,
    @SerialName("preparations") val preparations: List<Tag>,
    @SerialName("diets") val diets: List<Tag>,
)

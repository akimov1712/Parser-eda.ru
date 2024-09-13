package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("icon") val icon: String,
)

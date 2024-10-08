package org.example.model

import kotlinx.serialization.Serializable

@Serializable
data class StepRecipe(
    val text: String,
    val image: String?
)
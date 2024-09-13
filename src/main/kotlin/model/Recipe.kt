package model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.h2.result.Row
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object RecipeDbo: IntIdTable("recipe"){

    val createdAt = date(KEY.createdAt).nullable()
    val updatedAt = date(KEY.updatedAt).nullable()
    val title = varchar(KEY.title, 255)
    val descr = varchar(KEY.description, 255).nullable()
    val image = varchar(KEY.image, 255)
    val video = varchar(KEY.video, 255).nullable()
    val calories = integer(KEY.calories).nullable()
    val cookingTime = integer(KEY.cookingTime).nullable()
    val difficulty = varchar(KEY.difficulty, 64).nullable()
    val userId = varchar(KEY.userId, 255).nullable()
    val protein = integer(KEY.protein)
    val fat = integer(KEY.fat)
    val carbs = integer(KEY.carbs)
    val categoryId = integer(KEY.categoryId).nullable()
    val dietsTypeId = integer(KEY.dietsTypeId).nullable()
    val preparationId = integer(KEY.preparationId).nullable()


    fun insert(recipe: Recipe) = transaction{
        RecipeDbo.insert {
            it[createdAt] = recipe.createdAt
            it[updatedAt] = recipe.updatedAt
            it[title] = recipe.title
            it[descr] = recipe.descr
            it[image] = recipe.image
            it[video] = recipe.video
            it[calories] = recipe.calories
            it[cookingTime] = recipe.cookingTime
            it[difficulty] = recipe.difficulty.toString()
            it[userId] = recipe.userId
            it[protein] = recipe.protein
            it[fat] = recipe.fat
            it[carbs] = recipe.carbs
            it[categoryId] = recipe.categoryId
            it[dietsTypeId] = recipe.dietsTypeId
            it[preparationId] = recipe.preparationId
        }
    }

    fun selectRecipes() = transaction { selectAll().map { it.toRecipe() } }

    private fun ResultRow.toRecipe() = Recipe(
        id = this[id].value,
        createdAt = this[createdAt],
        updatedAt = this[updatedAt],
        title = this[title],
        descr = this[descr],
        image = this[image],
        video = this[video],
        calories = this[calories],
        cookingTime = this[cookingTime],
        difficulty = this[difficulty].toDifficulty(),
        userId = this[userId],
        protein = this[protein],
        fat = this[fat],
        carbs = this[carbs],
        categoryId = this[categoryId],
        dietsTypeId = this[dietsTypeId],
        preparationId = this[preparationId],
    )

}

@Serializable
data class Recipe(
    val id: Int,
    val createdAt: LocalDate?,
    val updatedAt: LocalDate?,
    val title: String,
    val descr: String?,
    val image: String,
    val video: String?,
    val calories: Int?,
    val cookingTime: Int?,
    val difficulty: Difficulty?,
    val userId: String?,
    val protein: Int,
    val fat: Int,
    val carbs: Int,
    val categoryId: Int?,
    val dietsTypeId: Int?,
    val preparationId: Int?,
)

private object KEY{
    const val createdAt = "key_createdAt"
    const val updatedAt = "key_updatedAt"
    const val title = "key_title"
    const val description = "key_description"
    const val image = "key_image"
    const val video = "key_video"
    const val calories = "key_calories"
    const val cookingTime = "key_cookingTime"
    const val difficulty = "key_difficulty"
    const val userId = "key_userId"
    const val protein = "key_protein"
    const val fat = "key_fat"
    const val carbs = "key_carbs"
    const val categoryId = "key_categoryId"
    const val dietsTypeId = "key_dietsTypeId"
    const val preparationId = "key_preparationId"
}
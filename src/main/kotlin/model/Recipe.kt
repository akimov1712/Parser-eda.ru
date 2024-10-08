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

    val title = text(KEY.title,)
    val descr = text(KEY.description).nullable()
    val image = text(KEY.image)
    val video = text(KEY.video).nullable()
    val calories = integer(KEY.calories).nullable()
    val cookingTime = integer(KEY.cookingTime).nullable()
    val difficulty = text(KEY.difficulty).nullable()
    val protein = integer(KEY.protein)
    val fat = integer(KEY.fat)
    val carbs = integer(KEY.carbs)
    val categoryId = integer(KEY.categoryId).nullable()
    val dietsTypeId = integer(KEY.dietsTypeId).nullable()
    val preparationId = integer(KEY.preparationId).nullable()


    fun insert(recipe: Recipe) = transaction{
        RecipeDbo.insert {
            it[title] = recipe.title
            it[descr] = recipe.descr
            it[image] = recipe.image
            it[video] = recipe.video
            it[calories] = recipe.calories
            it[cookingTime] = recipe.cookingTime
            it[difficulty] = recipe.difficulty.toString()
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
        title = this[title],
        descr = this[descr],
        image = this[image],
        video = this[video],
        calories = this[calories],
        cookingTime = this[cookingTime],
        difficulty = this[difficulty].toDifficulty(),
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
    val title: String,
    val descr: String?,
    val image: String,
    val video: String?,
    val calories: Int?,
    val cookingTime: Int?,
    val difficulty: Difficulty?,
    val protein: Int,
    val fat: Int,
    val carbs: Int,
    val categoryId: Int?,
    val dietsTypeId: Int?,
    val preparationId: Int?,
)

private object KEY{
    const val title = "key_title"
    const val description = "key_description"
    const val image = "key_image"
    const val video = "key_video"
    const val calories = "key_calories"
    const val cookingTime = "key_cookingTime"
    const val difficulty = "key_difficulty"
    const val protein = "key_protein"
    const val fat = "key_fat"
    const val carbs = "key_carbs"
    const val categoryId = "key_categoryId"
    const val dietsTypeId = "key_dietsTypeId"
    const val preparationId = "key_preparationId"
}
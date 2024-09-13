package org.example.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object RecipeLinkDbo: IntIdTable("recipeLink") {

    val link = varchar("recipeLink", 255)
    val categories = varchar("categories", 255)

    fun insertAll(links: List<RecipeLink>) = transaction{
        links.map { link ->
            RecipeLinkDbo.insert {
                it[RecipeLinkDbo.link] = link.link
                it[RecipeLinkDbo.categories] = link.categories.joinToString(" ,")
            }
        }
    }

    fun selectLinks() = transaction { RecipeLinkDbo.selectAll().map { RecipeLink(it[link], it[categories].split(" ,")) } }

}

data class RecipeLink(
    val link: String,
    val categories: List<String>
)
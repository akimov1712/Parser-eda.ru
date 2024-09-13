package org.example

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Recipe
import model.RecipeDbo
import org.example.model.RecipeLinkDbo
import org.example.scanners.RecipeScanner
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.FileWriter

fun main(): Unit = runBlocking {
    initialDatabase()
    transaction { RecipeDbo.deleteAll() }
    RecipeScanner().use {
        load()
    }
}

private fun initialDatabase() {
    Database.connect("jdbc:h2:./adminBot", driver = "org.h2.Driver", user = "root", password = "")
    transaction {
        SchemaUtils.create(RecipeDbo, RecipeLinkDbo)
    }
}
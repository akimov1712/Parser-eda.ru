package org.example.scanners

import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Difficulty
import model.Recipe
import model.RecipeDbo
import org.example.Dependency
import org.example.model.*
import org.example.utills.Log
import org.example.utills.formatCookingTime
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class RecipeScanner(
    private val linkScanner: RecipeLinkScanner = Dependency.RecipeLinkScanner
): Scanner() {

    override suspend fun load() {
        var links = RecipeLinkDbo.selectLinks()
        if (links.isEmpty()){
            Log.w("Загрузка ссылок...")
            linkScanner.load().also { links = RecipeLinkDbo.selectLinks() }
        }
        loadRecipes(links)
    }

    private suspend fun loadRecipes(links: List<RecipeLink>){
        val recipes = links.mapIndexed { index, link ->
            async(SupervisorJob()) {
                val doc = Jsoup.connect(link.link).get()
                Recipe(
                    title = doc.getTitle(),
                    descr = doc.getDescr(),
                    image = doc.getImage(),
                    video = null,
                    calories = doc.getCalories(),
                    cookingTime = doc.getTimeCooking(),
                    difficulty = doc.getDifficulty(doc.getTimeCooking()),
                    protein = doc.getProtein(),
                    fat = doc.getFat(),
                    carbs = doc.getCarbs(),
                    categoryId = link.categories.getCategories(),
                    dietsTypeId = link.categories.getDiets(),
                    preparationId = link.categories.getPreparations(),
                )
            }
        }
        recipes.mapIndexed { index, i ->
            try {
                Log.i("Загрузка рецепта: ${index + 1}/${recipes.size}")
                val recipe = i.await()
                RecipeDbo.insert(recipe)
            } catch (e: Exception){
                Log.e("Ошибка ${index + 1} рецепта\n${e.message ?: ""}")
            }
        }
    }

    private fun Document.getTitle() = this.select("h1").text()
    private fun Document.getDescr() = this.getElementsByAttributeValue("itemprop", "recipeInstructions").map {
        val text = it.getElementsByAttributeValue("itemprop", "text").text() ?: ""
        val image = it.select("picture.emotion-0").select("img").attr("src") ?: ""
        StepRecipe(text,image)
    }.run { Json.encodeToString(this) }
    private fun Document.getImage() = this.select(".emotion-gxbcya").attr("src")
    private fun Document.getCalories() = this.select(".emotion-1bpeio7").select(".emotion-16si75h")[0].text().toInt()
    private fun Document.getProtein() = this.select(".emotion-1bpeio7").select(".emotion-16si75h")[1].text().toInt()
    private fun Document.getFat() = this.select(".emotion-1bpeio7").select(".emotion-16si75h")[2].text().toInt()
    private fun Document.getCarbs() = this.select(".emotion-1bpeio7").select(".emotion-16si75h")[3].text().toInt()
    private fun Document.getTimeCooking() = this.select(".emotion-my9yfq").text().formatCookingTime()
    private fun List<String>.getCategories() = getValueFromMap(this, categories)
    private fun List<String>.getDiets() = getValueFromMap(this, diets)
    private fun List<String>.getPreparations() = getValueFromMap(this, preparations)

    private fun Document.getDifficulty(time: Int):Difficulty{
        val countIngr = this.select(".emotion-1oyy8lz").size
        return when{
            countIngr < 6 || time < 50 -> Difficulty.Easy
            countIngr < 10 || time < 90 -> Difficulty.Normal
            else -> Difficulty.Hard
        }
    }

    private fun getValueFromMap(values:List<String>, map: Map<String, Int>): Int? {
        values.forEach {
            if (map.containsKey(it)) return map[it]
        }
        return null
    }

}
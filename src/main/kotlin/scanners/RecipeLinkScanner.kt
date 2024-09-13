package org.example.scanners

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import org.example.model.RecipeLink
import org.example.model.RecipeLinkDbo
import org.example.utills.Log
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class RecipeLinkScanner: Scanner() {
    private var startLoading = true


    override suspend fun load() {
        RecipeLinkDbo.insertAll(parseLinks())
    }

    private suspend fun parseLinks(): List<RecipeLink>{
        val result = mutableListOf<RecipeLink>()
        val deferredList = mutableListOf<Deferred<List<RecipeLink>>>()
        var page = 1
        val exceptionHandler = CoroutineExceptionHandler{_, throwable -> ; Log.e("Page $page ended an error\n${throwable.message}"); page++ }
        while (startLoading && page < 800){
            val cachePage = page
            val result = async(exceptionHandler + SupervisorJob()) {                val doc = Jsoup.connect("${LINK}?page=$cachePage").get()
                if (doc.checkEmptyPage()){ startLoading = false; throw RuntimeException("page $cachePage is empty") }
                doc.select(".emotion-1f6ych6 ").map {
                    val link = it.select("a").select(".emotion-18hxz5k").attr("href").resolveSite()
                    val categories = it.select(".emotion-8kmwsh").select("a").map { it.text() }
                    RecipeLink(link, categories)
                }
            }
            deferredList.add(result)
            page++
        }
        deferredList.mapIndexed { index, deferred ->
            try {
                result.addAll(deferred.await()).also {
                    Log.i("Загружено: ${index + 1}/${deferredList.size}")
                }
            } catch (e: HttpStatusException){
                Log.e(e.message ?: "")
                return@mapIndexed
            } catch (e: Exception){
                Log.e(e.message ?: "")
            }
        }
        return result
    }

    private fun Document.checkEmptyPage() = this.hasClass(".emotion-7ufl01")

    private fun String.resolveSite() = "https://eda.ru$this"

    companion object{
        private const val LINK = "https://eda.ru/recepty"
    }

}
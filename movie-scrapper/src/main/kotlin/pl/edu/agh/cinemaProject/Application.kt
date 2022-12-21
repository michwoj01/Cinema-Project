package pl.edu.agh.cinemaProject

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import java.io.File
import kotlin.random.Random

@Serializable
data class OmdbSearchResult(
    val Search: ArrayList<Search> = arrayListOf(), val totalResults: String? = null, val Response: String? = null
)

@Serializable
data class Search(
    val Title: String? = null,
    val Year: String? = null,
    val imdbID: String? = null,
    val Type: String? = null,
    val Poster: String? = null
)

data class DbMovie(val name: String, val duration: Int, val cover_url: String, val description: String) {
    fun toSqlValues(): String {
        return "('${name.replace("'", "''")}', $duration, '$cover_url', '$description')"
    }
}

fun requestBaseWithPageAndSearch(apiKey: String, search: String, page: Int): String {
    return "http://www.omdbapi.com/?s=$search&apikey=${apiKey}&page=$page"
}

suspend fun main() {
    val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    val apiKey = "????"

    fun mkReq(apiKey: String, search: String, page: Int = 1): List<OmdbSearchResult> {
        return runBlocking {
            println("now get $search with page $page")
            val response = client.get(requestBaseWithPageAndSearch(apiKey, search,page)).body<OmdbSearchResult>()
            if ((response.totalResults?.toIntOrNull() ?: (page * 10)) / 10 < page || page ==50) {
                return@runBlocking listOf(response)
            } else {
                return@runBlocking listOf(response).plus(mkReq(apiKey, search, page + 1))
            }
        }
    }

    fun Int.toDesc(): String {
        return "This movie was made in $this"
    }

    val joinedInserts =
        listOf<String>("cow", "man", "woman", "police").flatMap {
            println("now getting $it")
            mkReq(apiKey, it)
        }.flatMap { it.Search }
            .filter {
                it.Poster != "N/A" && it.Type != "series"
            }
            .map {
                print(it.Type)
                it
            }
            .joinToString(", \n", "INSERT INTO MOVIE(NAME, DURATION, COVER_URL, DESCRIPTION) VALUES\n", ";") { search ->
                DbMovie(
                    search.Title!!,
                    Random.nextInt(80, 150),
                    search.Poster!!,
                    search.Year?.toIntOrNull()?.toDesc() ?: "Movie created at idk"
                ).toSqlValues()
            }


    File("movies_insert.sql").writeText(joinedInserts)


}
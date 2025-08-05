package com.example.cardtracker.service

import com.example.Card_Tracker.dto.CardDataDTO
import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate

@Service
class PokemonTcgService(
    private val restTemplate: RestTemplate
) {
    companion object {
        private const val MAX_PAGE_SIZE = 250
    }

    private val logger = LoggerFactory.getLogger(PokemonTcgService::class.java)
    private val cardBaseUrl = "https://api.pokemontcg.io/v2/cards"
    private val setBaseUrl = "https://api.pokemontcg.io/v2/sets"

    @Value("\${POKEMON_TCG_API_KEY}")
    private lateinit var apiKey: String

    fun getCardsFromSet(setId: String): List<CardDataDTO> {
        val allCards = mutableListOf<CardDataDTO>()
        var currentPage = 1
        var hasMorePages = true

        while (hasMorePages) {
            val url = "$cardBaseUrl?q=set.id:$setId&page=$currentPage&pageSize=$MAX_PAGE_SIZE"
            try {
                val response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createRequestEntity(),
                    PokemonApiResponse::class.java
                ).body ?: break

                response.data?.let { allCards.addAll(it) }

                // Check if we need to fetch more pages
                hasMorePages = response.data?.size == MAX_PAGE_SIZE
                currentPage++

                logger.debug("Fetched page $currentPage for set $setId, total cards: ${allCards.size}")

            } catch (e: HttpClientErrorException) {
                logger.error("Client error fetching cards for set $setId (page $currentPage): ${e.statusCode} - ${e.responseBodyAsString}")
                break
            } catch (e: HttpServerErrorException) {
                logger.error("Server error fetching cards for set $setId (page $currentPage): ${e.statusCode} - ${e.responseBodyAsString}")
                break
            } catch (e: Exception) {
                logger.error("Unexpected error fetching cards for set $setId (page $currentPage)", e)
                break
            }
        }

        return allCards
    }

    fun getSets(): List<CardDataDTO.CardSetDTO> {
        return try {
            val response = restTemplate.exchange(
                "$setBaseUrl?pageSize=$MAX_PAGE_SIZE",
                HttpMethod.GET,
                createRequestEntity(),
                PokemonSetApiResponse::class.java
            )
            response.body?.data ?: emptyList()
        } catch (e: HttpClientErrorException) {
            logger.error("Client error fetching sets: ${e.statusCode} - ${e.responseBodyAsString}")
            emptyList()
        } catch (e: HttpServerErrorException) {
            logger.error("Server error fetching sets: ${e.statusCode} - ${e.responseBodyAsString}")
            emptyList()
        } catch (e: Exception) {
            logger.error("Unexpected error fetching sets", e)
            emptyList()
        }
    }

    private fun createRequestEntity(): HttpEntity<String> {
        val headers = HttpHeaders().apply {
            set("X-Api-Key", apiKey)
            set("Accept", "application/json")
        }
        return HttpEntity<String>(headers)
    }

    private data class PokemonApiResponse(
        @JsonProperty("data") val data: List<CardDataDTO> = emptyList(),
        @JsonProperty("count") val count: Int = 0,
        @JsonProperty("totalCount") val totalCount: Int = 0,
        @JsonProperty("page") val page: Int = 1,
        @JsonProperty("pageSize") val pageSize: Int = MAX_PAGE_SIZE
    )

    private data class PokemonSetApiResponse(
        @JsonProperty("data") val data: List<CardDataDTO.CardSetDTO> = emptyList()
    )
}
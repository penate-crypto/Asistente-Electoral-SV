package com.example.data.search.normalization

import java.text.Normalizer

object ElectoralNormalizer {

    private val STOP_WORDS = setOf(
        "el", "la", "los", "las", "un", "una", "unos", "unas", "de", "del", "al", "a", "en", "por",
        "para", "con", "sin", "sobre", "tras", "durante", "mediante", "y", "o", "u", "e", "que",
        "se", "su", "sus", "mi", "mis", "tu", "tus", "me", "te", "le", "nos", "les", "lo", "como",
        "cuando", "donde", "quien", "cual", "cuales", "si", "no", "mas", "pero", "aunque", "hago",
        "debo", "hacer", "hacerlo", "pasa", "sucede", "ocurre", "iba", "iba a", "estaba", "fui"
    )

    private val TYPO_MAP = mapOf(
        "presi" to "presidente",
        "precidente" to "presidente",
        "presidete" to "presidente",
        "precidete" to "presidente",
        "secre" to "secretario",
        "cecretario" to "secretario",
        "secretaro" to "secretario",
        "vocal" to "vocal",
        "bocal" to "vocal",
        "vocales" to "vocal",
        "bocales" to "vocal",
        "policia" to "pnc",
        "policias" to "pnc",
        "chota" to "pnc",
        "cuete" to "pistola",
        "coete" to "pistola",
        "arma" to "arma",
        "armas" to "arma",
        "pistolas" to "pistola",
        "revolveres" to "revolver",
        "papeletas" to "papeleta",
        "boletas" to "papeleta",
        "boleta" to "papeleta",
        "cedula" to "dui",
        "duis" to "dui",
        "urnas" to "urna",
        "padrones" to "padron",
        "actas" to "acta",
        "conteo" to "escrutinio",
        "vigilantes" to "vigilante",
        "fiscales" to "vigilante",
        "fiscal" to "vigilante",
        "votantes" to "votante",
        "electores" to "votante",
        "elector" to "votante",
        "ciudadanos" to "votante",
        "ciudadano" to "votante"
    )

    /**
     * Remove accents/diacritics and convert to ASCII
     */
    fun stripAccents(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }

    /**
     * Normalize query text:
     * - Lowercase
     * - Remove diacritics
     * - Remove punctuation
     * - Replace typos and plural forms
     * - Filter out noise stopwords
     */
    fun normalize(rawQuery: String): NormalizedQuery {
        val clean = stripAccents(rawQuery.lowercase())
            .replace("[^a-z0-9\\s]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()

        val tokens = clean.split(" ").filter { it.isNotBlank() }

        val correctedTokens = tokens.map { token ->
            TYPO_MAP[token] ?: singularize(token)
        }

        val meaningfulKeywords = correctedTokens.filter { token ->
            token !in STOP_WORDS && token.length > 2
        }.distinct()

        return NormalizedQuery(
            raw = rawQuery.trim(),
            cleanedText = clean,
            tokens = correctedTokens,
            keywords = meaningfulKeywords
        )
    }

    /**
     * Basic singularization for Spanish electoral terms
     */
    private fun singularize(word: String): String {
        return when {
            word.endsWith("ces") -> word.dropLast(3) + "z"
            word.endsWith("es") && (word.endsWith("nes") || word.endsWith("res") || word.endsWith("les")) -> word.dropLast(2)
            word.endsWith("s") && !word.endsWith("is") && !word.endsWith("us") && word.length > 3 -> word.dropLast(1)
            else -> word
        }
    }
}

data class NormalizedQuery(
    val raw: String,
    val cleanedText: String,
    val tokens: List<String>,
    val keywords: List<String>
)

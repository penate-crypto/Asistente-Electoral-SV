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
        "q" to "que",
        "k" to "que",
        "c" to "se",
        "d" to "de",
        "x" to "por",
        "xq" to "porque",
        "pq" to "porque",
        "xk" to "porque",
        "tmb" to "tambien",
        "tmbn" to "tambien",
        "istala" to "instala",
        "intala" to "instala",
        "istalar" to "instala",
        "intalar" to "instala",
        "istalacion" to "instalacion",
        "intalacion" to "instalacion",
        "nesecita" to "necesita",
        "nesecitan" to "necesitan",
        "necesitan" to "necesita",
        "ocupa" to "necesita",
        "requiere" to "necesita",
        "aparesco" to "aparece",
        "aparese" to "aparece",
        "aparesca" to "aparece",
        "foto" to "fotografia",
        "fotos" to "fotografia",
        "retrato" to "fotografia",
        "parece" to "parece",
        "rasgos" to "rasgos",
        "fisonomia" to "rasgos",
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
        "ciudadano" to "votante",
        "escrutinioo" to "escrutinio",
        "escrutineo" to "escrutinio",
        "sufragar" to "votar",
        "sufragio" to "voto",
        "sufrago" to "voto",
        "urnas" to "urna",
        "cajas" to "urna",
        "papeletotas" to "papeleta",
        "boletin" to "papeleta",
        "atril" to "atril",
        "atriles" to "atril",
        "mampara" to "atril",
        "mamparas" to "atril",
        "cabina" to "atril",
        "cabinas" to "atril"
    )

    private val PHRASE_REPLACEMENTS = listOf(
        Regex("\\balas\\s+5\\b", RegexOption.IGNORE_CASE) to "a las 5 de la tarde",
        Regex("\\balas\\b", RegexOption.IGNORE_CASE) to "a las",
        Regex("\\bcierre\\s+de\\s+votos\\b", RegexOption.IGNORE_CASE) to "cierre de votacion",
        Regex("\\bcerrar\\s+los\\s+votos\\b", RegexOption.IGNORE_CASE) to "cierre de votacion",
        Regex("\\bcerrar\\s+la\\s+mesa\\b", RegexOption.IGNORE_CASE) to "cierre de votacion",
        Regex("\\bcontar\\s+votos\\b", RegexOption.IGNORE_CASE) to "escrutinio conteo de votos",
        Regex("\\bconteo\\s+de\\s+votos\\b", RegexOption.IGNORE_CASE) to "escrutinio conteo de votos",
        Regex("\\bpersona\\s+que\\s+fue\\s+a\\s+votar\\b", RegexOption.IGNORE_CASE) to "votante",
        Regex("\\bpersona\\s+que\\s+va\\s+a\\s+votar\\b", RegexOption.IGNORE_CASE) to "votante",
        Regex("\\bpersona\\s+que\\s+quiere\\s+sufragar\\b", RegexOption.IGNORE_CASE) to "votante",
        Regex("\\bpersona\\s+que\\s+sufrago\\b", RegexOption.IGNORE_CASE) to "votante",
        Regex("\\bciudadano\\s+que\\s+va\\s+a\\s+emitir\\s+su\\s+voto\\b", RegexOption.IGNORE_CASE) to "votante",
        Regex("\\bcinco\\s+de\\s+la\\s+tarde\\b", RegexOption.IGNORE_CASE) to "5 pm cierre de votacion",
        Regex("\\b5\\s*p\\.?m\\.?\\b", RegexOption.IGNORE_CASE) to "5 pm",
        Regex("\\b7\\s*a\\.?m\\.?\\b", RegexOption.IGNORE_CASE) to "7 am",
        Regex("\\b6\\s*a\\.?m\\.?\\b", RegexOption.IGNORE_CASE) to "6 am",
        Regex("\\b(?:se\\s+)?arma(?:r)?\\s+(?:la\\s+)?(mesa|jrv)\\b", RegexOption.IGNORE_CASE) to "instalar la $1",
        Regex("\\bcomo\\s+se\\s+arma\\b", RegexOption.IGNORE_CASE) to "como se instala"
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
     * - Idiom / colloquial phrase replacement
     * - Remove diacritics
     * - Remove punctuation
     * - Replace typos and plural forms
     * - Filter out noise stopwords
     */
    fun normalize(rawQuery: String): NormalizedQuery {
        var preprocessed = rawQuery.lowercase()
        for ((pattern, replacement) in PHRASE_REPLACEMENTS) {
            preprocessed = pattern.replace(preprocessed, replacement)
        }

        val clean = stripAccents(preprocessed)
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
     * Checks if [text] (already lowercase and accent-stripped) contains [term] at word boundaries.
     */
    fun containsWholeWord(text: String, term: String): Boolean {
        val cleanTerm = stripAccents(term.lowercase()).trim()
        if (cleanTerm.isBlank()) return false
        val escaped = Regex.escape(cleanTerm)
        return Regex("(?<![a-z0-9])$escaped(?![a-z0-9])").containsMatchIn(text)
    }

    /**
     * Checks if [text] contains a word starting with [prefix] (for intentional verb stems like amenaz*, ingres*, romp*).
     */
    fun containsWordPrefix(text: String, prefix: String): Boolean {
        val cleanPrefix = stripAccents(prefix.lowercase()).trim()
        if (cleanPrefix.isBlank()) return false
        val escaped = Regex.escape(cleanPrefix)
        return Regex("(?<![a-z0-9])$escaped").containsMatchIn(text)
    }

    /**
     * Tokenizes clean lowercase ASCII text and singularizes tokens.
     */
    fun tokenizeAndSingularize(cleanText: String): List<String> {
        return cleanText.replace("[^a-z0-9\\s]".toRegex(), " ")
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            .map { singularize(it) }
    }

    /**
     * Basic singularization for Spanish electoral terms
     */
    fun singularize(word: String): String {
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

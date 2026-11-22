package com.example.data.search.codigo

import android.content.Context

/** Un libro que se puede consultar con el botón «Buscar en libros». */
data class LawBook(
    val id: String,
    val title: String,
    val bank: LawBank,
    val search: LawSearch,
    /** id del documento en la Biblioteca (ElectoralLibraryData). */
    val pdfDocId: String
)

/**
 * Catálogo de los libros con banco de artículos. Cada banco se carga SOLO la primera vez que se usa
 * (no al abrir la app), así que [search] debe llamarse fuera del hilo principal.
 * El Instructivo JRV y el Ciclo Electoral no tienen banco aquí: se buscan desde su propio lector PDF.
 */
object LawLibrary {

    private fun book(id: String, title: String, asset: String, pdfDocId: String, bodyWeight: Double = 3.0): LawBook {
        val bank = LawBank(asset)
        return LawBook(id, title, bank, LawSearch(bank, { -1 }, 0, bodyWeight = bodyWeight), pdfDocId)
    }

    val books: List<LawBook> = listOf(
        LawBook("codigo_electoral", "Código Electoral", ElectoralCodeBank, ElectoralCodeSearch, "codigo_electoral_decreto_413"),
        LawBook("constitucion", "Constitución de la República", ElectoralConstitutionBank, ElectoralConstitutionSearch, "constitucion_republica_1983"),
        book("ley_partidos", "Ley de Partidos Políticos", "leyes/ley_partidos.json", "ley_de_partidos_politicos"),
        book("ley_acceso_informacion", "Ley de Acceso a la Información Pública", "leyes/ley_acceso_informacion.json", "ley_acceso_informacion_publica"),
        book("ley_sufragio_exterior", "Ley de Sufragio en el Exterior", "leyes/ley_sufragio_exterior.json", "ley_sufragio_extranjero_542"),
        book("ley_reestructuracion_municipal", "Reestructuración Municipal", "leyes/ley_reestructuracion_municipal.json", "ley_reestructuracion_municipal_763"),
        book("reglamento_observacion", "Reglamento de Observación Electoral", "leyes/reglamento_observacion.json", "reglamento_observacion_electoral"),
        book("candidaturas_no_partidarias", "Candidaturas No Partidarias", "leyes/candidaturas_no_partidarias.json", "disposiciones_candidaturas_no_partidarias"),
        book("acuerdo_legislativo", "Acuerdo Legislativo (segunda elección)", "leyes/acuerdo_legislativo.json", "acuerdo_legislativo_reforma_electoral"),
        book("codigo_penal_electoral", "Código Penal (delitos electorales)", "leyes/codigo_penal_electoral.json", "codigo_penal_delitos_electorales")
    )

    fun byId(id: String): LawBook? = books.firstOrNull { it.id == id }

    fun byPdfDocId(docId: String): LawBook? = books.firstOrNull { it.pdfDocId == docId }

    /** Busca en un libro. Devuelve null si no hay resultados. Llamar fuera del hilo principal. */
    fun search(context: Context, bookId: String, query: String): CodeSearchResult? {
        val b = byId(bookId) ?: return null
        b.bank.ensureLoaded(context.applicationContext)
        return b.search.searchDirect(query)
    }
}

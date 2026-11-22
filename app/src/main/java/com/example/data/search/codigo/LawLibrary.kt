package com.example.data.search.codigo

import android.content.Context

/** Un libro que se puede consultar con el botón «Buscar en libros». */
data class LawBook(
    val id: String,
    val title: String,
    val bank: LawBank,
    val search: LawSearch
)

/**
 * Catálogo de los libros con banco de artículos. Cada banco se carga SOLO la primera vez que se usa
 * (no al abrir la app), así que [search] debe llamarse fuera del hilo principal.
 * El Instructivo JRV y el Ciclo Electoral no tienen banco aquí: se buscan desde su propio lector PDF.
 */
object LawLibrary {

    private fun book(id: String, title: String, asset: String, bodyWeight: Double = 3.0): LawBook {
        val bank = LawBank(asset)
        return LawBook(id, title, bank, LawSearch(bank, { -1 }, 0, bodyWeight = bodyWeight))
    }

    val books: List<LawBook> = listOf(
        LawBook("codigo_electoral", "Código Electoral", ElectoralCodeBank, ElectoralCodeSearch),
        LawBook("constitucion", "Constitución de la República", ElectoralConstitutionBank, ElectoralConstitutionSearch),
        book("ley_partidos", "Ley de Partidos Políticos", "leyes/ley_partidos.json"),
        book("ley_acceso_informacion", "Ley de Acceso a la Información Pública", "leyes/ley_acceso_informacion.json"),
        book("ley_sufragio_exterior", "Ley de Sufragio en el Exterior", "leyes/ley_sufragio_exterior.json"),
        book("ley_reestructuracion_municipal", "Reestructuración Municipal", "leyes/ley_reestructuracion_municipal.json"),
        book("reglamento_observacion", "Reglamento de Observación Electoral", "leyes/reglamento_observacion.json"),
        book("candidaturas_no_partidarias", "Candidaturas No Partidarias", "leyes/candidaturas_no_partidarias.json"),
        book("acuerdo_legislativo", "Acuerdo Legislativo (segunda elección)", "leyes/acuerdo_legislativo.json"),
        book("codigo_penal_electoral", "Código Penal (delitos electorales)", "leyes/codigo_penal_electoral.json")
    )

    fun byId(id: String): LawBook? = books.firstOrNull { it.id == id }

    /** Busca en un libro. Devuelve null si no hay resultados. Llamar fuera del hilo principal. */
    fun search(context: Context, bookId: String, query: String): CodeSearchResult? {
        val b = byId(bookId) ?: return null
        b.bank.ensureLoaded(context.applicationContext)
        return b.search.searchDirect(query)
    }
}

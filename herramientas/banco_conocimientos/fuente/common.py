# Helpers compartidos para construir el banco de conocimientos.
LEYES = {
    "CE": ("Código Electoral de El Salvador", "Codigo-Electoral.pdf", "Ley"),
    "CN": ("Constitución de la República de El Salvador", "constitucion.pdf", "Constitución"),
    "CP": ("Código Penal de El Salvador", "Código Penal.pdf", "Ley"),
    "INS": ("Instructivo para Integrantes de Juntas Receptoras de Votos (TSE)", "instructivo-JRV.pdf", "Instructivo TSE"),
    "LEX": ("Ley Especial para el Ejercicio del Sufragio en el Extranjero", "LEY ESPECIAL PARA EL EJERCICIO DEL SUFRAGIO EN EL EXTRANJERO.pdf", "Ley Especial"),
    "ROE": ("Reglamento General para la Observación Electoral Nacional e Internacional", "REGLAMENTO-GENERAL-PARA-LA-OBSERVACION-ELECTORAL-NACIONAL-E-INTERNACIONAL-EN-EL-SALVADOR.pdf", "Reglamento"),
    "LRM": ("Ley Especial para la Reestructuración Municipal", "LEY ESPECIAL PARA LA REESTRUCTURACIÓN MUNICIPAL.pdf", "Ley Especial"),
    "RCN": ("Acuerdo de Reforma Constitucional N° 3 (D.O. N° 143, 31 de julio de 2025)", "ACUERDO LEGISLATIVO - REFORMA - SUPRECIÓN SEGUNDA ELECCIÓN.pdf", "Reforma Constitucional"),
    "CNP": ("Disposiciones Especiales para Candidaturas No Partidarias", "Disposciones-Especiales-Candidaturas-no-partidarias.pdf", "Disposiciones Especiales"),
    "CIC": ("Ciclo Electoral Salvadoreño 2025-2027 (TSE)", "CICLO ELECTORAL SALVADOREÑO.pdf", "Documento TSE"),
}

DOC_IDS = {
    "CE": "codigo_electoral_decreto_413", "CN": "constitucion_republica_1983",
    "CP": "codigo_penal_delitos_electorales", "INS": "instructivo_jrv_tse",
    "LEX": "ley_sufragio_extranjero_542", "ROE": "reglamento_observacion_electoral",
    "LRM": "ley_reestructuracion_municipal_763", "RCN": "acuerdo_legislativo_reforma_electoral",
    "CNP": "disposiciones_candidaturas_no_partidarias", "CIC": "ciclo_electoral_salvadoreno",
}

ENTRIES = []


def E(id, titulo, categoria, subcategoria, descripcion, actores, objetos, acciones,
      contextos, intenciones, palabras_clave, sinonimos, situacion, que_hacer,
      que_no_hacer, quien_interviene, fundamento, prioridad="alta",
      advertencia=None, quien_puede=None, quien_no_puede=None, condiciones=None,
      instructivo=None, relacionadas=None, docs=None):
    """fundamento: lista de (codigo_ley, [(numero, descripcion), ...]).
    instructivo: lista de (frase_para_ubicar_pagina, seccion) del instructivo JRV."""
    ENTRIES.append(dict(
        id=id, titulo=titulo, categoria=categoria, subcategoria=subcategoria,
        descripcion=descripcion, actores=actores, objetos=objetos, acciones=acciones,
        contextos=contextos, intenciones=intenciones, palabras_clave=palabras_clave,
        sinonimos=sinonimos, situacion=situacion, que_hacer=que_hacer,
        que_no_hacer=que_no_hacer, quien_interviene=quien_interviene,
        fundamento=fundamento, prioridad=prioridad, advertencia=advertencia,
        quien_puede=quien_puede, quien_no_puede=quien_no_puede,
        condiciones=condiciones, instructivo=instructivo or [],
        relacionadas=relacionadas or [], docs=docs or []))

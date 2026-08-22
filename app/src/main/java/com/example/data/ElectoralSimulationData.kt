package com.example.data

import androidx.compose.ui.graphics.vector.ImageVector

// --- Casos y Soluciones Data Classes ---

data class EntityInvolved(
    val entityName: String,
    val specificRole: String,
    val whatShouldDo: String = ""
)

data class CaseSolution(
    val id: String,
    val title: String,
    val stage: String, // "Instalación", "Votación", "Escrutinio", "Seguridad"
    val situationDescription: String,
    val contextDescription: String = "",
    val entitiesInvolved: List<EntityInvolved>,
    val whatShouldBeDone: String,
    val stepByStepProcedure: List<String>,
    val actionsNotToDo: List<String>,
    val correctSolution: String,
    val whySolutionIsCorrect: String,
    val legalNormativeRef: String,
    val libraryDocumentRef: String = "Código Electoral de El Salvador"
)

// --- Examen Electoral Data Classes ---

enum class QuestionType {
    MULTIPLE_CHOICE,
    BALLOT_VALIDITY // ¿El voto es válido o nulo?
}

enum class BallotMarkType {
    VALID_SINGLE_PARTY_CROSS,
    VALID_CANDIDATE_CROSS,
    VALID_CHECKMARK_OVER_FLAG,
    VALID_COALITION_MARK,
    NULL_TWO_RIVAL_PARTIES,
    NULL_OBSCENE_INSULT_TEXT,
    NULL_MUTILATED_BALLOT,
    NULL_CANDIDATE_PLUS_INDEPENDENT_RIVAL,
    NULL_BLANK_BALLOT_WRITTEN,
    VALID_SLIGHT_OVERFLOW_CROSS,
    NULL_MARKED_ALL_PARTIES,
    VALID_PREFERENTIAL_CROSS_CANDIDATES
}

data class ExamQuestion(
    val id: String,
    val category: String, // JRV, JEM, DOE, PNC, FGR, Procedimientos, Seguridad, etc.
    val questionText: String,
    val situationContext: String? = null,
    val type: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val ballotMarkType: BallotMarkType? = null,
    val ballotVisualDescription: String? = null, // Descripción de lo que se observa en la papeleta
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val normativeReference: String,
    val sourceDocument: String,
    val sourceArticle: String = "",
    val sourcePage: Int? = null,
    val sourceDocumentId: String = "codigo_electoral_decreto_413"
)

data class ExamResult(
    val totalQuestions: Int = 25,
    val correctAnswersCount: Int,
    val incorrectAnswersCount: Int,
    val scorePercentage: Float,
    val isApproved: Boolean, // >= 70% (18/25)
    val questionReviews: List<ExamQuestionReview>
)

data class ExamQuestionReview(
    val question: ExamQuestion,
    val selectedOptionIndex: Int,
    val isCorrect: Boolean
)

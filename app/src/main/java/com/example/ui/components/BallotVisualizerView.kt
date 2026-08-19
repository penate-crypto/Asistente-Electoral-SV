package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BallotMarkType

@Composable
fun BallotVisualizerView(
    markType: BallotMarkType,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .testTag("ballot_canvas_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFBF7)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD7CCC8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ballot Header simulation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REPÚBLICA DE EL SALVADOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0D47A1)
                )
                Text(
                    text = "PAPELETA OFICIAL",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D4037)
                )
            }

            Text(
                text = "TRIBUNAL SUPREMO ELECTORAL • ELECCIÓN NACIONAL",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF78909C),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Vector Canvas drawing of the Ballot
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F0))
                    .border(1.dp, Color(0xFFBCAAA4), RoundedCornerShape(8.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawBallotGraphics(markType)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Watermark stamp footer
            Text(
                text = "Simulación visual didáctica para calificación de votos",
                fontSize = 9.sp,
                color = Color(0xFF8D6E63),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun DrawScope.drawBallotGraphics(markType: BallotMarkType) {
    val totalWidth = size.width
    val totalHeight = size.height

    // Draw 4 Party slots (2x2 grid or horizontal layout)
    val slotWidth = (totalWidth - 30f) / 2f
    val slotHeight = (totalHeight - 30f) / 2f

    val slot1 = Offset(10f, 10f)
    val slot2 = Offset(slotWidth + 20f, 10f)
    val slot3 = Offset(10f, slotHeight + 20f)
    val slot4 = Offset(slotWidth + 20f, slotHeight + 20f)

    // Party 1: Blue / White Flag
    drawRect(
        color = Color(0xFFE3F2FD),
        topLeft = slot1,
        size = Size(slotWidth, slotHeight)
    )
    drawRect(
        color = Color(0xFF1976D2),
        topLeft = slot1,
        size = Size(slotWidth, slotHeight),
        style = Stroke(width = 2f)
    )
    // Flag banner inside slot 1
    drawRect(
        color = Color(0xFF1565C0),
        topLeft = Offset(slot1.x + 8f, slot1.y + 8f),
        size = Size(slotWidth - 16f, slotHeight * 0.45f)
    )

    // Party 2: Red Flag
    drawRect(
        color = Color(0xFFFFEBEE),
        topLeft = slot2,
        size = Size(slotWidth, slotHeight)
    )
    drawRect(
        color = Color(0xFFD32F2F),
        topLeft = slot2,
        size = Size(slotWidth, slotHeight),
        style = Stroke(width = 2f)
    )
    drawRect(
        color = Color(0xFFC62828),
        topLeft = Offset(slot2.x + 8f, slot2.y + 8f),
        size = Size(slotWidth - 16f, slotHeight * 0.45f)
    )

    // Party 3: Green Flag
    drawRect(
        color = Color(0xFFE8F5E9),
        topLeft = slot3,
        size = Size(slotWidth, slotHeight)
    )
    drawRect(
        color = Color(0xFF388E3C),
        topLeft = slot3,
        size = Size(slotWidth, slotHeight),
        style = Stroke(width = 2f)
    )
    drawRect(
        color = Color(0xFF2E7D32),
        topLeft = Offset(slot3.x + 8f, slot3.y + 8f),
        size = Size(slotWidth - 16f, slotHeight * 0.45f)
    )

    // Party 4: Orange Flag / Independent
    drawRect(
        color = Color(0xFFFFF3E0),
        topLeft = slot4,
        size = Size(slotWidth, slotHeight)
    )
    drawRect(
        color = Color(0xFFF57C00),
        topLeft = slot4,
        size = Size(slotWidth, slotHeight),
        style = Stroke(width = 2f)
    )
    drawRect(
        color = Color(0xFFEF6C00),
        topLeft = Offset(slot4.x + 8f, slot4.y + 8f),
        size = Size(slotWidth - 16f, slotHeight * 0.45f)
    )

    // Render simulated voter marks (crayon style, dark charcoal/black/red strokes)
    val crayonColor = Color(0xFF212121)
    val markStroke = 7f

    when (markType) {
        BallotMarkType.VALID_SINGLE_PARTY_CROSS -> {
            // A clean, solid cross over Party 1
            drawLine(
                color = crayonColor,
                start = Offset(slot1.x + 20f, slot1.y + 15f),
                end = Offset(slot1.x + slotWidth - 20f, slot1.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = crayonColor,
                start = Offset(slot1.x + slotWidth - 20f, slot1.y + 15f),
                end = Offset(slot1.x + 20f, slot1.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
        }

        BallotMarkType.NULL_TWO_RIVAL_PARTIES -> {
            // Cross on Party 1 AND Cross on Party 2 (Non-coalition)
            drawLine(
                color = crayonColor,
                start = Offset(slot1.x + 20f, slot1.y + 15f),
                end = Offset(slot1.x + slotWidth - 20f, slot1.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = crayonColor,
                start = Offset(slot1.x + slotWidth - 20f, slot1.y + 15f),
                end = Offset(slot1.x + 20f, slot1.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )

            drawLine(
                color = crayonColor,
                start = Offset(slot2.x + 20f, slot2.y + 15f),
                end = Offset(slot2.x + slotWidth - 20f, slot2.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = crayonColor,
                start = Offset(slot2.x + slotWidth - 20f, slot2.y + 15f),
                end = Offset(slot2.x + 20f, slot2.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
        }

        BallotMarkType.VALID_CHECKMARK_OVER_FLAG -> {
            // Checkmark '✓' over Party 3 (Green)
            val path = Path().apply {
                moveTo(slot3.x + 25f, slot3.y + slotHeight * 0.5f)
                lineTo(slot3.x + slotWidth * 0.45f, slot3.y + slotHeight - 20f)
                lineTo(slot3.x + slotWidth - 25f, slot3.y + 18f)
            }
            drawPath(
                path = path,
                color = crayonColor,
                style = Stroke(width = markStroke + 2f, cap = StrokeCap.Round)
            )
        }

        BallotMarkType.VALID_COALITION_MARK -> {
            // Mark on Party 1 and Party 3 (Coalition alliance)
            drawLine(
                color = crayonColor,
                start = Offset(slot1.x + 20f, slot1.y + 15f),
                end = Offset(slot1.x + slotWidth - 20f, slot1.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = crayonColor,
                start = Offset(slot1.x + slotWidth - 20f, slot1.y + 15f),
                end = Offset(slot1.x + 20f, slot1.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )

            drawLine(
                color = crayonColor,
                start = Offset(slot3.x + 20f, slot3.y + 15f),
                end = Offset(slot3.x + slotWidth - 20f, slot3.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = crayonColor,
                start = Offset(slot3.x + slotWidth - 20f, slot3.y + 15f),
                end = Offset(slot3.x + 20f, slot3.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
        }

        BallotMarkType.NULL_OBSCENE_INSULT_TEXT -> {
            // Scribbles and aggressive crossing out across the ballot
            drawLine(
                color = Color(0xFFD32F2F),
                start = Offset(10f, 10f),
                end = Offset(totalWidth - 10f, totalHeight - 10f),
                strokeWidth = markStroke + 3f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFFD32F2F),
                start = Offset(totalWidth - 10f, 10f),
                end = Offset(10f, totalHeight - 10f),
                strokeWidth = markStroke + 3f,
                cap = StrokeCap.Round
            )
            // Simulated aggressive scratch lines
            for (i in 1..4) {
                drawLine(
                    color = Color(0xFFD32F2F),
                    start = Offset(20f * i, 30f + i * 25f),
                    end = Offset(totalWidth - 20f * i, 40f + i * 25f),
                    strokeWidth = 3f
                )
            }
        }

        BallotMarkType.NULL_MUTILATED_BALLOT -> {
            // Drawn with a ripped/torn corner effect
            val ripPath = Path().apply {
                moveTo(totalWidth * 0.5f, 0f)
                lineTo(totalWidth * 0.55f, totalHeight * 0.3f)
                lineTo(totalWidth * 0.45f, totalHeight * 0.6f)
                lineTo(totalWidth * 0.52f, totalHeight)
                lineTo(totalWidth, totalHeight)
                lineTo(totalWidth, 0f)
                close()
            }
            drawPath(
                path = ripPath,
                color = Color(0xFFE0E0E0).copy(alpha = 0.85f)
            )
            drawPath(
                path = ripPath,
                color = Color(0xFFB71C1C),
                style = Stroke(width = 3f)
            )
        }

        BallotMarkType.VALID_PREFERENTIAL_CROSS_CANDIDATES -> {
            // Mark on Party 2 flag + marks on two sub-candidate boxes
            drawLine(
                color = crayonColor,
                start = Offset(slot2.x + 20f, slot2.y + 10f),
                end = Offset(slot2.x + slotWidth - 20f, slot2.y + slotHeight * 0.5f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = crayonColor,
                start = Offset(slot2.x + slotWidth - 20f, slot2.y + 10f),
                end = Offset(slot2.x + 20f, slot2.y + slotHeight * 0.5f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
            // Candidate preference 1
            drawCircle(
                color = crayonColor,
                center = Offset(slot2.x + slotWidth * 0.3f, slot2.y + slotHeight * 0.75f),
                radius = 10f,
                style = Stroke(width = 4f)
            )
            // Candidate preference 2
            drawCircle(
                color = crayonColor,
                center = Offset(slot2.x + slotWidth * 0.7f, slot2.y + slotHeight * 0.75f),
                radius = 10f,
                style = Stroke(width = 4f)
            )
        }

        BallotMarkType.NULL_CANDIDATE_PLUS_INDEPENDENT_RIVAL -> {
            // Cross on Party 1 and cross on Independent Candidate Slot 4
            drawLine(
                color = crayonColor,
                start = Offset(slot1.x + 20f, slot1.y + 15f),
                end = Offset(slot1.x + slotWidth - 20f, slot1.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = crayonColor,
                start = Offset(slot1.x + slotWidth - 20f, slot1.y + 15f),
                end = Offset(slot1.x + 20f, slot1.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )

            drawLine(
                color = crayonColor,
                start = Offset(slot4.x + 20f, slot4.y + 15f),
                end = Offset(slot4.x + slotWidth - 20f, slot4.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = crayonColor,
                start = Offset(slot4.x + slotWidth - 20f, slot4.y + 15f),
                end = Offset(slot4.x + 20f, slot4.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
        }

        BallotMarkType.VALID_SLIGHT_OVERFLOW_CROSS -> {
            // Cross on Party 3 extending slightly past border
            drawLine(
                color = crayonColor,
                start = Offset(slot3.x + 10f, slot3.y + 8f),
                end = Offset(slot3.x + slotWidth + 12f, slot3.y + slotHeight + 8f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = crayonColor,
                start = Offset(slot3.x + slotWidth + 10f, slot3.y + 8f),
                end = Offset(slot3.x + 10f, slot3.y + slotHeight + 8f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
        }

        else -> {
            // General cross
            drawLine(
                color = crayonColor,
                start = Offset(slot1.x + 20f, slot1.y + 15f),
                end = Offset(slot1.x + slotWidth - 20f, slot1.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = crayonColor,
                start = Offset(slot1.x + slotWidth - 20f, slot1.y + 15f),
                end = Offset(slot1.x + 20f, slot1.y + slotHeight - 15f),
                strokeWidth = markStroke,
                cap = StrokeCap.Round
            )
        }
    }
}

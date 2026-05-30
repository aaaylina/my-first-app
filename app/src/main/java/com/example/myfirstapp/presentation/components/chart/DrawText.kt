package com.example.myfirstapp.presentation.components.chart

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawPercentageText(
    text: String,
    startAngle: Float,
    sweepAngle: Float,
    radius: Float,
    center: Offset,
    shiftX: Float = 0f,
    shiftY: Float = 0f,
) {

    val middleAngle = startAngle + sweepAngle / 2

    val radians = Math.toRadians(middleAngle.toDouble())

    val x = center.x +
            cos(radians).toFloat() * radius +
            shiftX

    val y = center.y +
            sin(radians).toFloat() * radius +
            shiftY

    drawContext.canvas.nativeCanvas.drawText(
        text,
        x,
        y,
        Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 42f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }
    )
}
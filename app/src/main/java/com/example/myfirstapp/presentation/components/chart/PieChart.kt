package com.example.myfirstapp.presentation.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.*

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    items: List<PieChartItem>,
    gapAngle: Float = 4f,
) {

    validatePieChart(items)

    var selectedIndex by remember {
        mutableStateOf<Int?>(null)
    }

    val angles = remember(items) {
        items.map {
            360f * it.percent / 100f
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    selectedIndex = null
                }
            },
        contentAlignment = Alignment.Center
    ) {

        BoxWithConstraints(
            modifier = modifier.aspectRatio(1f)
        ) {

            val sizePx = constraints.maxWidth.toFloat()
            val center = Offset(sizePx / 2, sizePx / 2)
            val outerRadius = sizePx / 2
            val innerRadius = outerRadius * 0.55f

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(items, selectedIndex) {

                        detectTapGestures { offset ->

                            val dx = offset.x - center.x
                            val dy = offset.y - center.y
                            val radius = sqrt(dx * dx + dy * dy)

                            if (radius < innerRadius) {
                                selectedIndex = null
                                return@detectTapGestures
                            }

                            var angle = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
                            angle = (angle + 90) % 360
                            if (angle < 0) angle += 360f

                            var startAngle = 0f
                            var foundIndex: Int? = null

                            for (index in angles.indices) {
                                val sweep = angles[index]
                                val sectorStart = startAngle + gapAngle / 2
                                val sectorEnd = startAngle + sweep - gapAngle / 2

                                if (angle >= sectorStart && angle <= sectorEnd) {
                                    foundIndex = index
                                    break
                                }
                                startAngle += sweep
                            }

                            selectedIndex = foundIndex
                        }
                    }
            ) {

                var startAngle = -90f

                items.forEachIndexed { index, item ->

                    val sweep = angles[index]
                    val isSelected = selectedIndex == index

                    val shift = if (isSelected) 24f else 0f
                    val middleAngle = Math.toRadians((startAngle + sweep / 2).toDouble())
                    val offsetX = cos(middleAngle).toFloat() * shift
                    val offsetY = sin(middleAngle).toFloat() * shift

                    val drawColor = if (isSelected) {
                        Color(
                            red = (item.color.red * 0.6f + 0.4f).coerceAtMost(1f),
                            green = (item.color.green * 0.6f + 0.4f).coerceAtMost(1f),
                            blue = (item.color.blue * 0.6f + 0.4f).coerceAtMost(1f)
                        )
                    } else {
                        item.color
                    }

                    drawArc(
                        color = drawColor,
                        startAngle = startAngle + gapAngle / 2,
                        sweepAngle = sweep - gapAngle,
                        useCenter = true,
                        topLeft = Offset(offsetX, offsetY),
                        size = size,
                    )

                    startAngle += sweep
                }

                drawCircle(
                    color = Color.White,
                    radius = innerRadius,
                    center = center,
                )

                startAngle = -90f
                items.forEachIndexed { index, item ->
                    val sweep = angles[index]
                    val isSelected = selectedIndex == index
                    val shift = if (isSelected) 24f else 0f
                    val textRadius = (outerRadius + innerRadius) / 2

                    if (sweep > 12f) {
                        drawPercentageText(
                            text = "${item.percent}%",
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            radius = textRadius,
                            center = center,
                            shiftX = cos(Math.toRadians((startAngle + sweep / 2).toDouble())).toFloat() * shift,
                            shiftY = sin(Math.toRadians((startAngle + sweep / 2).toDouble())).toFloat() * shift,
                        )
                    }
                    startAngle += sweep
                }
            }
        }
    }
}

private fun validatePieChart(
    items: List<PieChartItem>
) {

    require(items.isNotEmpty()) {
        ERROR_EMPTY_ITEMS
    }

    val total = items.sumOf { it.percent }

    require(total == 100) {
        ERROR_INVALID_SUM + total
    }

    for (i in items.indices) {
        val current = items[i]
        val next = items[(i + 1) % items.size]

        require(current.color != next.color) {
            ERROR_ADJACENT_COLORS
        }
    }
}

private const val ERROR_EMPTY_ITEMS = "Pie chart items cannot be empty"
private const val ERROR_INVALID_SUM = "Sum of all percentages must equal 100. Current sum: "
private const val ERROR_ADJACENT_COLORS = "Adjacent sectors cannot have the same color"
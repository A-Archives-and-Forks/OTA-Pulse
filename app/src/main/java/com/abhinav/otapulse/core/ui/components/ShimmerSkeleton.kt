/*
 * Copyright (C) 2026 OTA Pulse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abhinav.otapulse.core.ui.components

import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import com.abhinav.otapulse.core.ui.theme.LocalReduceMotion

fun Modifier.shimmerEffect(): Modifier = composed {
    val reduceMotion = LocalReduceMotion.current

    if (reduceMotion) {
        background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    } else {
        val baseColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        val highlightColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        val shimmerColors = listOf(baseColor, highlightColor, baseColor)

        val transition = rememberInfiniteTransition(label = "ShimmerTransition")
        val progress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1300, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ShimmerProgress"
        )

        drawWithContent {
            drawContent()
            val width = size.width
            val height = size.height
            if (width > 0f && height > 0f) {
                val bandWidth = (width * 0.7f).coerceAtLeast(180.dp.toPx())
                val totalDistance = width + bandWidth * 2f
                val startX = -bandWidth + (totalDistance * progress)
                val brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(x = startX, y = startX * 0.35f),
                    end = Offset(x = startX + bandWidth, y = (startX + bandWidth) * 0.35f)
                )
                drawRect(brush = brush)
            }
        }
    }
}

@Composable
fun ShimmerBox(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .size(width, height)
            .clip(RoundedCornerShape(cornerRadius))
            .shimmerEffect()
    )
}

@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShimmerBox(width = 48.dp, height = 48.dp, cornerRadius = 24.dp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                ShimmerBox(width = 120.dp, height = 16.dp)
                Spacer(modifier = Modifier.height(8.dp))
                ShimmerBox(width = 80.dp, height = 14.dp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            ShimmerBox(width = 80.dp, height = 36.dp, cornerRadius = 18.dp)
        }
    }
}

@Composable
fun SkeletonListItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBox(width = 40.dp, height = 40.dp, cornerRadius = 20.dp)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            ShimmerBox(width = 100.dp, height = 16.dp)
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerBox(width = 60.dp, height = 14.dp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        ShimmerBox(width = 24.dp, height = 24.dp)
    }
}

@Composable
fun SkeletonGrid(
    count: Int,
    modifier: Modifier = Modifier,
    columns: Int = 2
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val rows = (count + columns - 1) / columns
        for (i in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (j in 0 until columns) {
                    val index = i * columns + j
                    if (index < count) {
                        SkeletonCard(modifier = Modifier.weight(1f))
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


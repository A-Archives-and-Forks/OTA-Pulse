/*
 * Copyright 2026 OTA Pulse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abhinav.otapulse.core.ui.components

import android.provider.Settings
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import com.abhinav.otapulse.core.ui.theme.LocalReduceMotion

fun Modifier.stackItemAppearance(index: Int, sessionKey: String = ""): Modifier = composed {
    if (LocalReduceMotion.current) {
        return@composed this
    }
    
    val alphaAnim = remember { Animatable(0f) }
    val slideAnim = remember { Animatable(24f) }
    val scaleAnim = remember { Animatable(0.94f) }
    
    LaunchedEffect(sessionKey) {
        alphaAnim.snapTo(0f)
        slideAnim.snapTo(24f)
        scaleAnim.snapTo(0.94f)

        // Apply stagger only to the first 10 items (initial screen load or filter change).
        val staggerDelay = if (index < 10) index * 30L else 0L
        if (staggerDelay > 0) delay(staggerDelay)
        
        val spec = tween<Float>(durationMillis = 220, easing = FastOutSlowInEasing)
        
        launch { alphaAnim.animateTo(1f, spec) }
        launch { slideAnim.animateTo(0f, spec) }
        scaleAnim.animateTo(1f, spec)
    }
    
    this.graphicsLayer {
        alpha = alphaAnim.value
        translationY = slideAnim.value.dp.toPx()
        scaleX = scaleAnim.value
        scaleY = scaleAnim.value
    }
}

fun Modifier.pressInteraction(
    interactionSource: MutableInteractionSource,
    pressScale: Float = 0.96f
): Modifier = composed {
    if (LocalReduceMotion.current) {
        return@composed this
    }

    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressScale else 1f,
        animationSpec = if (isPressed) {
            spring(stiffness = 800f, dampingRatio = 0.65f)
        } else {
            spring(stiffness = 500f, dampingRatio = 0.5f)
        },
        label = "press_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = if (isPressed) {
            spring(stiffness = 800f, dampingRatio = 0.65f)
        } else {
            spring(stiffness = 500f, dampingRatio = 0.5f)
        },
        label = "press_elevation"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        shadowElevation = elevation.toPx()
    }
}

data class PressInteractionState(
    val interactionSource: MutableInteractionSource,
    val modifier: Modifier
)

@Composable
fun rememberPressInteraction(pressScale: Float = 0.96f): PressInteractionState {
    val interactionSource = remember { MutableInteractionSource() }
    val modifier = Modifier.pressInteraction(interactionSource, pressScale)
    return remember(interactionSource, modifier) {
        PressInteractionState(interactionSource, modifier)
    }
}

inline fun <T> LazyListScope.stackAnimatedItems(
    items: List<T>,
    noinline key: ((item: T) -> Any)? = null,
    noinline contentType: (item: T) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit
) {
    items(
        count = items.size,
        key = if (key != null) { index: Int -> key(items[index]) } else null,
        contentType = { index: Int -> contentType(items[index]) }
    ) { index ->
        val item = items[index]
        Box(
            modifier = Modifier
                .stackItemAppearance(index)
                .animateItem(
                    fadeInSpec = null,
                    fadeOutSpec = null,
                    placementSpec = spring(stiffness = 600f, dampingRatio = 0.8f)
                )
        ) {
            itemContent(item)
        }
    }
}


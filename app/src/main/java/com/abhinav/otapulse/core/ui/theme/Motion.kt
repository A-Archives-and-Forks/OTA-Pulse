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

package com.abhinav.otapulse.core.ui.theme

import android.provider.Settings
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset

/**
 * Whether the user has disabled animations via Accessibility > Remove Animations
 * or Developer Options > Animator duration scale = 0.
 *
 * When `true`, all motion specs should resolve to instant/snap animations.
 */
val LocalReduceMotion = compositionLocalOf { false }

/**
 * Provides [LocalReduceMotion] to the composition tree by reading
 * `Settings.Global.ANIMATOR_DURATION_SCALE`. Wrap your root composable
 * with this to enable Reduce Motion awareness throughout the app.
 */
@Composable
fun MotionProvider(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val reduceMotion = remember(context) {
        runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            ) == 0f
        }.getOrDefault(false)
    }
    CompositionLocalProvider(LocalReduceMotion provides reduceMotion) {
        content()
    }
}

/**
 * Resolves this [AnimationSpec] to [androidx.compose.animation.core.snap] if [LocalReduceMotion] is enabled.
 */
@Composable
fun <T> androidx.compose.animation.core.AnimationSpec<T>.orSnap(): androidx.compose.animation.core.AnimationSpec<T> =
    if (LocalReduceMotion.current) androidx.compose.animation.core.snap() else this

/**
 * Resolves this [FiniteAnimationSpec] to [androidx.compose.animation.core.snap] if [LocalReduceMotion] is enabled.
 */
@Composable
fun <T> androidx.compose.animation.core.FiniteAnimationSpec<T>.orSnap(): androidx.compose.animation.core.FiniteAnimationSpec<T> =
    if (LocalReduceMotion.current) androidx.compose.animation.core.snap() else this

/**
 * Spring-based motion specifications for OTA Pulse.
 *
 * Adheres to 2026 trending design direction by favoring bouncy, natural spring physics
 * over linear or basic easing curves.
 */
object OtaPulseMotion {

    // ── Spring Specs (Float) ──────────────────────────────────────────────

    val SpringStiff = spring<Float>(
        dampingRatio = 0.7f,
        stiffness = 800f
    )
    
    val SpringMedium = spring<Float>(
        dampingRatio = 0.75f,
        stiffness = 400f
    )
    
    val SpringGentle = spring<Float>(
        dampingRatio = 0.8f,
        stiffness = 200f
    )
    
    val SpringBouncy = spring<Float>(
        dampingRatio = 0.5f,
        stiffness = 500f
    )

    /** Snappy micro-interaction spring — fast with slight overshoot. */
    val SpringSnappy = spring<Float>(
        dampingRatio = 0.65f,
        stiffness = 1000f
    )

    /** Silky smooth spring for sheet drags and large gestures. */
    val SpringSilky = spring<Float>(
        dampingRatio = 0.9f,
        stiffness = 150f
    )

    // ── Spring Specs (Typed Overloads) ────────────────────────────────────

    val SpringMediumDp = spring<Dp>(
        dampingRatio = 0.75f,
        stiffness = 400f
    )

    val SpringMediumOffset = spring<IntOffset>(
        dampingRatio = 0.75f,
        stiffness = 400f
    )

    val SpringMediumSize = spring<androidx.compose.ui.unit.IntSize>(
        dampingRatio = 0.75f,
        stiffness = 400f
    )

    // ── Shared Element / Predictive Back ──────────────────────────────────

    val SharedElementSpec = spring<Float>(
        dampingRatio = 0.85f,
        stiffness = 350f
    )

    val PredictiveBackSpec = spring<Float>(
        dampingRatio = 0.9f,
        stiffness = 600f
    )

    // ── Content Transitions (Tween) ──────────────────────────────────────

    val FadeInSpec = tween<Float>(durationMillis = 200, easing = EaseOut)
    val FadeOutSpec = tween<Float>(durationMillis = 150, easing = EaseIn)

    // ── Standardized Duration Tokens ─────────────────────────────────────

    /** Quick micro-interaction (press feedback, icon swap). */
    const val DurationShort = 150

    /** Standard transition (content swap, navigation). */
    const val DurationMedium = 300

    /** Elaborate transition (sheet reveal, complex entrance). */
    const val DurationLong = 450

    // ── Stack List Animation Specs ────────────────────────────────────────

    /** Enter duration for list items. */
    const val StackEnterDuration = 220

    /** Exit duration for list items. */
    const val StackExitDuration = 180

    /** Press animation duration feel. */
    const val StackPressDuration = 120

    /** Stagger delay between list items (ms). */
    const val StaggerDelayMs = 40

    /** Max items to stagger (items beyond this appear without extra delay). */
    const val StaggerMaxItems = 8

    /** Standard entrance easing for list items. */
    val StackEnterSpec = tween<Float>(
        durationMillis = StackEnterDuration,
        easing = FastOutSlowInEasing
    )

    /** Reorder spring for animated list item movement. */
    val StackReorderSpec = spring<IntOffset>(
        dampingRatio = 0.8f,
        stiffness = 600f
    )
}


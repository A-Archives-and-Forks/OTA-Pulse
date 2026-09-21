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

package com.abhinav.otapulse.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import com.abhinav.otapulse.core.ui.theme.OtaPulseMotion

/**
 * Shared animation definitions for navigation transitions in OTA Pulse.
 *
 * Bottom Nav tabs use **Fade Through** (Material Motion pattern):
 *   outgoing screen fades out + scales down → incoming screen fades in + scales up.
 *   This creates a peer-to-peer feel without directional sliding.
 *
 * Push/Pop navigation uses **Shared Axis Z** with depth cues:
 *   push adds a subtle scale-down to the exiting screen for depth perception.
 *
 * Sheet transitions use slide-up + fade for modal bottom sheets.
 */
object NavigationAnimations {

    // ── Push Navigation (Secondary Screens) ───────────────────────────────

    fun defaultEnterTransition(): EnterTransition {
        return fadeIn(animationSpec = OtaPulseMotion.FadeInSpec) +
                slideInHorizontally(animationSpec = OtaPulseMotion.SpringMediumOffset) { fullWidth -> fullWidth }
    }

    fun defaultExitTransition(): ExitTransition {
        return fadeOut(animationSpec = OtaPulseMotion.FadeOutSpec) +
                slideOutHorizontally(animationSpec = OtaPulseMotion.SpringMediumOffset) { fullWidth -> -fullWidth / 4 } +
                scaleOut(targetScale = 0.95f, animationSpec = OtaPulseMotion.SpringMedium)
    }

    fun defaultPopEnterTransition(): EnterTransition {
        return fadeIn(animationSpec = OtaPulseMotion.FadeInSpec) +
                slideInHorizontally(animationSpec = OtaPulseMotion.SpringMediumOffset) { fullWidth -> -fullWidth / 4 } +
                scaleIn(initialScale = 0.95f, animationSpec = OtaPulseMotion.SpringMedium)
    }

    fun defaultPopExitTransition(): ExitTransition {
        return fadeOut(animationSpec = OtaPulseMotion.FadeOutSpec) +
                slideOutHorizontally(animationSpec = OtaPulseMotion.SpringMediumOffset) { fullWidth -> fullWidth }
    }

    // ── Bottom Nav (Fade Through) ─────────────────────────────────────────

    /**
     * Fade Through enter: incoming screen fades in from alpha=0 and scales
     * up from 0.92f — creating the illusion of stepping forward.
     */
    fun bottomNavEnterTransition(): EnterTransition {
        return fadeIn(
            animationSpec = tween(durationMillis = OtaPulseMotion.DurationMedium)
        ) + scaleIn(
            initialScale = 0.92f,
            animationSpec = OtaPulseMotion.SpringMedium
        )
    }

    /**
     * Fade Through exit: outgoing screen fades out and scales down to 0.92f
     * — creating the illusion of stepping back.
     */
    fun bottomNavExitTransition(): ExitTransition {
        return fadeOut(
            animationSpec = tween(durationMillis = OtaPulseMotion.DurationShort)
        ) + scaleOut(
            targetScale = 0.92f,
            animationSpec = OtaPulseMotion.SpringMedium
        )
    }

    // ── Sheet Transitions ─────────────────────────────────────────────────

    fun sheetEnterTransition(): EnterTransition {
        return fadeIn(
            animationSpec = tween(durationMillis = OtaPulseMotion.DurationMedium)
        ) + slideInVertically(
            animationSpec = OtaPulseMotion.SpringMediumOffset
        ) { it / 3 }
    }

    fun sheetExitTransition(): ExitTransition {
        return fadeOut(
            animationSpec = tween(durationMillis = OtaPulseMotion.DurationShort)
        ) + slideOutVertically(
            animationSpec = OtaPulseMotion.SpringMediumOffset
        ) { it / 3 }
    }
}


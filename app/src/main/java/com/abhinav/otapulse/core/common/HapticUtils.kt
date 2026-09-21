package com.abhinav.otapulse.core.common

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Semantic haptic feedback types for OTA Pulse.
 *
 * Each type maps to a distinct tactile sensation using platform-native
 * [HapticFeedbackConstants] where available, with graceful fallbacks
 * for older API levels. The system automatically respects the user's
 * haptic preference settings.
 */
enum class HapticType {
    /** Light tick — selections, list item highlight, scrolling past a boundary. */
    TICK,
    /** Standard click — button taps, card presses, navigation actions. */
    CLICK,
    /** Heavy click — destructive actions, important confirmations, long-press triggers. */
    HEAVY_CLICK,
    /** Success confirmation — download complete, verification passed. */
    CONFIRM,
    /** Error/rejection — validation failure, operation error. */
    REJECT,
    /** Segment tick — progress milestones (25%, 50%, etc.), slider detents. */
    SEGMENT_TICK,
    /** Toggle switched ON. */
    TOGGLE_ON,
    /** Toggle switched OFF. */
    TOGGLE_OFF
}

/**
 * Performs semantic haptic feedback on a [View].
 *
 * Uses the platform's native haptic feedback constants which automatically
 * respect the user's haptic settings. If the view does not handle the feedback
 * (e.g. unattached or OEM override), falls back seamlessly to [Context.haptic].
 */
fun View.haptic(type: HapticType = HapticType.TICK) {
    val constant = type.toHapticConstant()
    val handled = performHapticFeedback(
        constant,
        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
    )
    if (!handled) {
        context.haptic(type)
    }
}

/**
 * Performs semantic haptic feedback from a [Context].
 *
 * Uses [VibratorManager] on API 31+ or [Vibrator] on older APIs with
 * hardware-calibrated predefined effects where available.
 */
fun Context.haptic(type: HapticType = HapticType.TICK) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = getSystemService(VibratorManager::class.java)
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Vibrator::class.java)
    } ?: return

    if (!vibrator.hasVibrator()) return

    val effect = type.toVibrationEffect()
    vibrator.vibrate(effect)
}

/**
 * Creates a remembered haptic callback for use in Compose.
 *
 * Usage:
 * ```
 * val haptic = rememberHaptic()
 * Button(onClick = { haptic(HapticType.CLICK); doSomething() })
 * ```
 */
@Composable
fun rememberHaptic(): (HapticType) -> Unit {
    val view = LocalView.current
    return remember(view) {
        { type: HapticType -> view.haptic(type) }
    }
}

// ── Legacy compatibility ──────────────────────────────────────────────────

/**
 * Legacy haptic feedback — delegates to [HapticType.CLICK].
 * Kept for backward compatibility during migration.
 */
@Deprecated(
    message = "Use haptic(HapticType) for semantic haptic feedback",
    replaceWith = ReplaceWith("haptic(HapticType.CLICK)", "com.abhinav.otapulse.core.common.haptic", "com.abhinav.otapulse.core.common.HapticType")
)
fun View.performHapticFeedback() {
    haptic(HapticType.CLICK)
}

/**
 * Legacy haptic feedback — delegates to [HapticType.CLICK].
 * Kept for backward compatibility during migration.
 */
@Deprecated(
    message = "Use haptic(HapticType) for semantic haptic feedback",
    replaceWith = ReplaceWith("haptic(HapticType.CLICK)", "com.abhinav.otapulse.core.common.haptic", "com.abhinav.otapulse.core.common.HapticType")
)
fun Context.performHapticFeedback() {
    haptic(HapticType.CLICK)
}

@Deprecated(
    message = "Use haptic(HapticType) for semantic haptic feedback",
    replaceWith = ReplaceWith("setOnClickListener { haptic(HapticType.CLICK); listener.onClick(it) }")
)
fun View.setHapticClickListener(listener: View.OnClickListener) {
    setOnClickListener {
        haptic(HapticType.CLICK)
        listener.onClick(it)
    }
}

// ── Internal mappings ─────────────────────────────────────────────────────

/**
 * Maps [HapticType] to the best available [HapticFeedbackConstants] constant.
 * Uses API 30+ constants where available, with fallbacks for older devices.
 */
private fun HapticType.toHapticConstant(): Int = when (this) {
    HapticType.TICK -> HapticFeedbackConstants.CLOCK_TICK
    HapticType.CLICK -> HapticFeedbackConstants.CONTEXT_CLICK
    HapticType.HEAVY_CLICK -> if (Build.VERSION.SDK_INT >= 30) {
        HapticFeedbackConstants.LONG_PRESS
    } else {
        HapticFeedbackConstants.LONG_PRESS
    }
    HapticType.CONFIRM -> if (Build.VERSION.SDK_INT >= 30) {
        HapticFeedbackConstants.CONFIRM
    } else {
        HapticFeedbackConstants.CONTEXT_CLICK
    }
    HapticType.REJECT -> if (Build.VERSION.SDK_INT >= 30) {
        HapticFeedbackConstants.REJECT
    } else {
        HapticFeedbackConstants.LONG_PRESS
    }
    HapticType.SEGMENT_TICK -> if (Build.VERSION.SDK_INT >= 34) {
        HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
    } else {
        HapticFeedbackConstants.CLOCK_TICK
    }
    HapticType.TOGGLE_ON -> if (Build.VERSION.SDK_INT >= 34) {
        HapticFeedbackConstants.TOGGLE_ON
    } else {
        HapticFeedbackConstants.CONTEXT_CLICK
    }
    HapticType.TOGGLE_OFF -> if (Build.VERSION.SDK_INT >= 34) {
        HapticFeedbackConstants.TOGGLE_OFF
    } else {
        HapticFeedbackConstants.CLOCK_TICK
    }
}

/**
 * Maps [HapticType] to a [VibrationEffect] with appropriate duration and amplitude.
 * Used when performing haptics from a [Context] without a [View].
 */
private fun HapticType.toVibrationEffect(): VibrationEffect = when (this) {
    HapticType.TICK -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
    } else {
        VibrationEffect.createOneShot(10, 40)
    }
    HapticType.CLICK -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
    } else {
        VibrationEffect.createOneShot(20, 80)
    }
    HapticType.HEAVY_CLICK -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
    } else {
        VibrationEffect.createOneShot(30, 120)
    }
    HapticType.CONFIRM -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
    } else {
        VibrationEffect.createWaveform(
            longArrayOf(0, 15, 50, 15), intArrayOf(0, 80, 0, 60), -1
        )
    }
    HapticType.REJECT -> VibrationEffect.createWaveform(
        longArrayOf(0, 20, 40, 20, 40, 20), intArrayOf(0, 100, 0, 80, 0, 60), -1
    )
    HapticType.SEGMENT_TICK -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
    } else {
        VibrationEffect.createOneShot(8, 30)
    }
    HapticType.TOGGLE_ON -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
    } else {
        VibrationEffect.createOneShot(15, 60)
    }
    HapticType.TOGGLE_OFF -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
    } else {
        VibrationEffect.createOneShot(12, 40)
    }
}





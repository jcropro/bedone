package app.ember.studio.widgets

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Flame Minimal Widget Receiver (1×2)
 */
class FlameMinimalWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EmberGlanceWidget()
}

/**
 * Flame Card Widget Receiver (2×2)
 */
class FlameCardWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EmberGlanceWidget()
}

/**
 * Vinyl Widget Receiver (2×2)
 */
class VinylWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EmberGlanceWidget()
}

/**
 * Circular Widget Receiver (1×1)
 */
class CircularWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EmberGlanceWidget()
}

/**
 * Full Art Widget Receiver (4×2)
 */
class FullArtWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EmberGlanceWidget()
}
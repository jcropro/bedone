package app.ember.studio.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import app.ember.studio.MainActivity
import app.ember.studio.R
import app.ember.studio.playback.PlaybackEngine
import androidx.media3.common.Player

/**
 * Ember Audio Player Widget Provider
 * 
 * Provides beautiful, theme-aware widgets following the Golden Blueprint:
 * - Small (1×2): Track info + play/pause
 * - Medium (2×2): Track info + controls + next
 * - Large (4×2): Full controls + EQ shortcut
 * 
 * All widgets are theme responsive and follow Ember design tokens.
 */
class EmberWidgetProvider : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update all widget instances
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        // Handle widget updates when playback state changes
        when (intent.action) {
            ACTION_UPDATE_WIDGETS -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName = ComponentName(context, EmberWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }
    
    companion object {
        const val ACTION_UPDATE_WIDGETS = "app.ember.studio.widgets.UPDATE_WIDGETS"
        const val ACTION_PLAY_PAUSE = "app.ember.studio.widgets.PLAY_PAUSE"
        const val ACTION_PREVIOUS = "app.ember.studio.widgets.PREVIOUS"
        const val ACTION_NEXT = "app.ember.studio.widgets.NEXT"
        
        fun updateAllWidgets(context: Context) {
            val intent = Intent(context, EmberWidgetProvider::class.java).apply {
                action = ACTION_UPDATE_WIDGETS
            }
            context.sendBroadcast(intent)
        }
        
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.ember_widget_layout)
            
            // Get current track info
            val player = PlaybackEngine.player
            val isPlaying = player?.let { 
                it.playbackState == Player.STATE_READY && it.playWhenReady 
            } ?: false
            
            val currentTrack = player?.currentMediaItem?.mediaMetadata
            val title = currentTrack?.title?.toString() ?: "No track"
            val artist = currentTrack?.artist?.toString() ?: "Unknown artist"
            
            // Update text
            views.setTextViewText(R.id.widget_track_title, title)
            views.setTextViewText(R.id.widget_track_artist, artist)
            
            // Update play/pause button
            val playPauseIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
            views.setImageViewResource(R.id.widget_play_pause_button, playPauseIcon)
            
            // Set up click intents
            val mainIntent = Intent(context, MainActivity::class.java)
            val mainPendingIntent = PendingIntent.getActivity(
                context, 0, mainIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val playPauseIntent = Intent(context, EmberWidgetProvider::class.java).apply {
                action = ACTION_PLAY_PAUSE
            }
            val playPausePendingIntent = PendingIntent.getBroadcast(
                context, 1, playPauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val previousIntent = Intent(context, EmberWidgetProvider::class.java).apply {
                action = ACTION_PREVIOUS
            }
            val previousPendingIntent = PendingIntent.getBroadcast(
                context, 2, previousIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val nextIntent = Intent(context, EmberWidgetProvider::class.java).apply {
                action = ACTION_NEXT
            }
            val nextPendingIntent = PendingIntent.getBroadcast(
                context, 3, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Set click listeners
            views.setOnClickPendingIntent(R.id.widget_track_title, mainPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_track_artist, mainPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_play_pause_button, playPausePendingIntent)
            views.setOnClickPendingIntent(R.id.widget_prev_button, previousPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_next_button, nextPendingIntent)
            
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

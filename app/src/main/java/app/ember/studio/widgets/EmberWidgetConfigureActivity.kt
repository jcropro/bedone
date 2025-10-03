package app.ember.studio.widgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.core.ui.theme.EmberAudioPlayerTheme
import app.ember.core.ui.design.EmberFlame
import app.ember.core.ui.design.EmberInk
import app.ember.core.ui.design.TextStrong
import app.ember.core.ui.design.TextMuted
import app.ember.core.ui.design.Spacing16
import app.ember.core.ui.design.Spacing24
import app.ember.core.ui.design.RadiusLG

/**
 * Widget Configuration Activity
 * 
 * Allows users to customize widget appearance and behavior
 * following the Golden Blueprint design specifications.
 */
class EmberWidgetConfigureActivity : ComponentActivity() {
    
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set the result to CANCELED initially
        setResult(Activity.RESULT_CANCELED)
        
        // Find the widget id from the intent
        appWidgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        
        // If this activity was started with an intent without an app widget ID, finish with an error
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        
        prefs = getSharedPreferences("ember_widget_$appWidgetId", MODE_PRIVATE)
        
        setContent {
            EmberAudioPlayerTheme(
                themeState = app.ember.core.ui.theme.ThemeUiState()
            ) {
                WidgetConfigureScreen(
                    onSave = { style, showEq ->
                        saveWidgetConfiguration(style, showEq)
                        finishConfiguration()
                    },
                    onCancel = {
                        finish()
                    }
                )
            }
        }
    }
    
    private fun saveWidgetConfiguration(style: WidgetStyle, showEq: Boolean) {
        prefs.edit()
            .putString("widget_style", style.name)
            .putBoolean("show_eq", showEq)
            .apply()
    }
    
    private fun finishConfiguration() {
        // Update the widget
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val widgetProvider = EmberWidgetProvider()
        widgetProvider.onUpdate(this, appWidgetManager, intArrayOf(appWidgetId))
        
        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}

enum class WidgetStyle {
    ROUNDED, COMPACT, GLASS
}

@Composable
fun WidgetConfigureScreen(
    onSave: (WidgetStyle, Boolean) -> Unit,
    onCancel: () -> Unit
) {
    var selectedStyle by remember { mutableStateOf(WidgetStyle.ROUNDED) }
    var showEq by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EmberInk)
            .padding(Spacing24)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Spacing24)
        ) {
            // Header
            Text(
                text = "Customize Widget",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextStrong
                )
            )
            
            // Style selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF16181C)
                ),
                shape = RoundedCornerShape(RadiusLG)
            ) {
                Column(
                    modifier = Modifier.padding(Spacing16)
                ) {
                    Text(
                        text = "Widget Style",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextStrong
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(Spacing16))
                    
                    WidgetStyle.values().forEach { style ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedStyle == style,
                                onClick = { selectedStyle = style },
                                colors = androidx.compose.material3.RadioButtonDefaults.colors(
                                    selectedColor = EmberFlame
                                )
                            )
                            
                            Spacer(modifier = Modifier.width(Spacing16))
                            
                            Column {
                                Text(
                                    text = style.displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = TextStrong
                                    )
                                )
                                Text(
                                    text = style.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextMuted
                                    )
                                )
                            }
                        }
                    }
                }
            }
            
            // Options
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF16181C)
                ),
                shape = RoundedCornerShape(RadiusLG)
            ) {
                Column(
                    modifier = Modifier.padding(Spacing16)
                ) {
                    Text(
                        text = "Options",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextStrong
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(Spacing16))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Checkbox(
                            checked = showEq,
                            onCheckedChange = { showEq = it },
                            colors = androidx.compose.material3.CheckboxDefaults.colors(
                                checkedColor = EmberFlame
                            )
                        )
                        
                        Spacer(modifier = Modifier.width(Spacing16))
                        
                        Column {
                            Text(
                                text = "Show Equalizer Shortcut",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TextStrong
                                )
                            )
                            Text(
                                text = "Add quick access to equalizer controls",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMuted
                                )
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing16)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E3140)
                    ),
                    shape = RoundedCornerShape(RadiusLG)
                ) {
                    Text(
                        text = "Cancel",
                        color = TextStrong
                    )
                }
                
                Button(
                    onClick = { onSave(selectedStyle, showEq) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmberFlame
                    ),
                    shape = RoundedCornerShape(RadiusLG)
                ) {
                    Text(
                        text = "Save",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

val WidgetStyle.displayName: String
    get() = when (this) {
        WidgetStyle.ROUNDED -> "Rounded"
        WidgetStyle.COMPACT -> "Compact"
        WidgetStyle.GLASS -> "Glass"
    }

val WidgetStyle.description: String
    get() = when (this) {
        WidgetStyle.ROUNDED -> "Classic rounded corners with shadows"
        WidgetStyle.COMPACT -> "Minimal design with tight spacing"
        WidgetStyle.GLASS -> "Glass morphism with blur effects"
    }

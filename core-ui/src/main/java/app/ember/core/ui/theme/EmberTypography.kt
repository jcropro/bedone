package app.ember.core.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.core.ui.R

/**
 * Centralized typography for Ember. Keep color out of Typography; use MaterialTheme.colorScheme.
 * Font resources are expected under core-ui/src/main/res/font/.
 * The semi-bold font file must be named `inter_semibold.ttf` to resolve as R.font.inter_semibold.
 */
private val EmberFontFamily = FontFamily(
    Font(resId = R.font.inter_regular,  weight = FontWeight.Normal,   style = FontStyle.Normal),
    Font(resId = R.font.inter_medium,   weight = FontWeight.Medium,   style = FontStyle.Normal),
    Font(resId = R.font.inter_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
    Font(resId = R.font.inter_bold,     weight = FontWeight.Bold,     style = FontStyle.Normal)
)

val EmberTypography: Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = EmberFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 46.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = EmberFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.25).sp
    ),
    titleLarge = TextStyle(
        fontFamily = EmberFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = EmberFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = EmberFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = EmberFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = EmberFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    ),
    bodySmall = TextStyle(
        fontFamily = EmberFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.15.sp
    ),
    labelLarge = TextStyle(
        fontFamily = EmberFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = EmberFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = EmberFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

@Preview(name = "Typography preview", showBackground = true)
@Composable
private fun EmberTypographyPreview() {
    EmberAudioPlayerTheme(themeState = ThemeUiState()) {
        Surface(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PreviewRow(label = R.string.preview_style_display,       style = MaterialTheme.typography.displayLarge)
                PreviewRow(label = R.string.preview_style_headline,      style = MaterialTheme.typography.headlineLarge)
                PreviewRow(label = R.string.preview_style_title_large,   style = MaterialTheme.typography.titleLarge)
                PreviewRow(label = R.string.preview_style_title,         style = MaterialTheme.typography.titleMedium)
                PreviewRow(label = R.string.preview_style_subtitle,      style = MaterialTheme.typography.titleSmall)
                PreviewRow(label = R.string.preview_style_body_large,    style = MaterialTheme.typography.bodyLarge)
                PreviewRow(label = R.string.preview_style_body,          style = MaterialTheme.typography.bodyMedium)
                PreviewRow(label = R.string.preview_style_body_compact,  style = MaterialTheme.typography.bodySmall)
                PreviewRow(label = R.string.preview_style_caption,       style = MaterialTheme.typography.labelMedium)
                PreviewRow(label = R.string.preview_style_label,         style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun PreviewRow(
    label: Int,
    style: TextStyle
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(id = label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(id = R.string.preview_sample_text),
            style = style,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

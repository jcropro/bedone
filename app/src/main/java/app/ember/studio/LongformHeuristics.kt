package app.ember.studio

import java.util.Locale

internal const val LONGFORM_THRESHOLD_MS: Long = 20 * 60 * 1000L
internal const val LONGFORM_AUDIOBOOK_DURATION_MS: Long = 2 * 60 * 60 * 1000L

private val PODCAST_TOKENS = listOf("podcast", "episode", " ep", "show", "radio")
private val AUDIOBOOK_TOKENS = listOf("audiobook", "chapter", "book", "volume", "vol", " ch")

internal fun suggestLongformCategory(
    title: String,
    source: String,
    existingCategory: LongformCategory,
    durationMs: Long
): LongformCategory {
    if (existingCategory != LongformCategory.Unassigned) {
        return existingCategory
    }
    val haystack = "$title $source".lowercase(Locale.US)
    if (PODCAST_TOKENS.any { haystack.contains(it) }) {
        return LongformCategory.Podcast
    }
    if (AUDIOBOOK_TOKENS.any { haystack.contains(it) } || durationMs >= LONGFORM_AUDIOBOOK_DURATION_MS) {
        return LongformCategory.Audiobook
    }
    return LongformCategory.Unassigned
}

# Crossfade Behavior Documentation

## Overview

The Ember Audio Player implements intelligent crossfade functionality that smoothly transitions between tracks by fading out the current track while fading in the next track. This creates a seamless listening experience without gaps or abrupt cuts.

## Capability Checks

### Player State Validation

Before enabling crossfade, the system performs several capability checks:

1. **Player Availability**: The ExoPlayer instance must be available and initialized
2. **Media Items**: At least 2 media items must be available for crossfade to work
3. **Next Item**: A next media item must be available in the queue
4. **Volume Control**: The player must support volume control (0.0 to 1.0 range)

### Duration Validation

Crossfade duration is validated with the following constraints:

- **Minimum**: 0ms (disabled)
- **Maximum**: 12,000ms (12 seconds)
- **Warning**: Durations under 500ms may cause audio artifacts
- **Recommended**: 2,000-5,000ms for optimal experience

### Conflict Detection

Crossfade is automatically disabled when:

- Sleep timer fade is active (takes precedence)
- Sleep timer is running (prevents conflicts)
- Player is buffering (avoids audio glitches)
- Player has ended (no transition possible)

## Behavior Patterns

### Fade-Out Phase

When a track approaches its end (within crossfade duration):

1. Volume gradually decreases from 1.0 to 0.0
2. Fade-out starts `crossfadeDurationMs` before track end
3. Volume reaches 0.0 exactly when track ends
4. Next track begins playing at volume 0.0

### Fade-In Phase

After automatic track transition:

1. Volume starts at 0.0 (silent)
2. Volume gradually increases from 0.0 to 1.0
3. Fade-in completes over `crossfadeDurationMs`
4. Normal playback resumes at full volume

### Manual Transitions

For manual track changes (next/previous):

- Crossfade is **not applied** to maintain immediate response
- Volume remains at current level
- Transition is instant for better user control

## Implementation Details

### Volume Control

```kotlin
// Fade-out calculation
val remaining = (duration - position).coerceAtLeast(0L)
if (remaining in 0..crossfadeDurationMs) {
    val vol = (remaining.toFloat() / crossfadeDurationMs).coerceIn(0f, 1f)
    player.volume = vol
}

// Fade-in calculation
val fadeInEnd = crossfadeFadeInEndTimestampMs
if (fadeInEnd > nowMs) {
    val remaining = (fadeInEnd - nowMs).coerceAtMost(crossfadeDurationMs)
    val progress = 1f - (remaining.toFloat() / crossfadeDurationMs)
    val vol = progress.coerceIn(0f, 1f)
    player.volume = vol
}
```

### State Management

Crossfade state is managed through:

- `crossfadeDurationMs`: Configured duration (0-12,000ms)
- `crossfadeFadeInEndTimestampMs`: When fade-in completes
- Volume restoration when crossfade is disabled

### Error Handling

All volume control operations are wrapped in try-catch blocks to handle:

- Player state changes during fade
- Concurrent access to volume control
- Player disposal during crossfade

## Testing Strategy

### Instrumentation Tests

1. **Capability Validation**: Verify player state checks
2. **Fade-Out Testing**: Validate volume reduction in fade window
3. **Fade-In Testing**: Validate volume restoration after transition
4. **Boundary Conditions**: Test duration limits and edge cases
5. **Conflict Resolution**: Test interaction with sleep timer

### Test Scenarios

```kotlin
// Test fade-out behavior
exo.seekTo(duration - crossfadeDurationMs)
waitUntil { exo.volume < 0.99f }
assertTrue("Fade-out should start", exo.volume < 0.99f)

// Test fade-in behavior
waitUntil { exo.currentMediaItemIndex != startIndex }
waitUntil { exo.volume >= 0.95f }
assertTrue("Fade-in should complete", exo.volume >= 0.95f)
```

## Performance Considerations

### CPU Usage

- Volume changes are applied at 50ms intervals
- Calculations are lightweight (simple arithmetic)
- No audio processing overhead

### Memory Usage

- Minimal state tracking (2 long values)
- No audio buffer manipulation
- Efficient volume control API usage

### Battery Impact

- Negligible impact on battery life
- No additional audio processing
- Leverages existing player volume control

## Troubleshooting

### Common Issues

1. **No Crossfade Effect**
   - Check if crossfade duration > 0
   - Verify player has multiple media items
   - Ensure no conflicting audio effects

2. **Audio Artifacts**
   - Reduce crossfade duration
   - Check for sleep timer conflicts
   - Verify player state is stable

3. **Volume Not Restoring**
   - Disable crossfade to restore volume
   - Check for sleep timer fade interference
   - Restart playback if needed

### Debug Information

Enable debug logging to monitor:

- Crossfade state changes
- Volume control operations
- Capability check results
- Conflict detection

## Future Enhancements

### Planned Features

1. **Adaptive Crossfade**: Adjust duration based on track genre
2. **Smart Detection**: Detect natural track endings
3. **User Preferences**: Per-genre crossfade settings
4. **Visual Feedback**: Show crossfade progress in UI

### Technical Improvements

1. **Audio Analysis**: Analyze track endings for optimal fade points
2. **Machine Learning**: Learn user preferences for crossfade timing
3. **Advanced Effects**: Support for more complex transition effects
4. **Real-time Adjustment**: Allow runtime crossfade duration changes

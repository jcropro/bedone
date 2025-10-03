# Onboarding Issue Diagnosis & Resolution Plan

## Current Problem

The app still shows only media controls and an orange line after onboarding, with no tabs visible. This indicates that the onboarding overlay is still being shown instead of the main content.

## What Has Been Tried

1. ✅ **Conditional Rendering**: Wrapped `ModalNavigationDrawer` in `if (!onboardingState.isVisible)`
2. ✅ **Initial State**: Changed `createInitialOnboardingState()` to start with `isVisible = true`
3. ✅ **Preferences Observation**: Verified `observeOnboardingPreferences()` is called in init
4. ✅ **Completion Function**: Verified `completeOnboarding()` updates state and persists to DataStore
5. ✅ **Theme Step**: Verified Theme step calls `onComplete` when finish button is clicked

## Root Cause Analysis

The issue is likely one of the following:

1. **Onboarding DataStore Not Persisting**: The onboarding completion status is not being saved to DataStore correctly
2. **State Not Updating**: The `onboardingState.isVisible` is not being updated when onboarding completes
3. **Timing Issue**: The preferences observation is not immediately updating the state
4. **DataStore File Issue**: The DataStore file might be corrupted or not being created

## Debug Steps to Try

### Step 1: Clear App Data

**In Android Studio:**
1. Go to Settings (on device/emulator) → Apps → Ember Audio Player
2. Tap "Storage"
3. Tap "Clear Data"
4. Restart the app

This will reset the onboarding state and allow you to see if the issue is with the DataStore persistence.

### Step 2: Manual Override

**Add this to MainActivity:**

```kotlin
// In onCreate(), before setContent
playerViewModel.forceHideOnboarding() // Force hide onboarding for testing
```

This will bypass the onboarding completely and show the main content directly. If this works, it confirms the issue is with onboarding state management.

### Step 3: Check DataStore File

**Location:** `/data/data/app.ember.studio/files/datastore/onboarding_preferences`

The file should exist and contain the onboarding completion status. If it doesn't exist or is empty, the DataStore is not persisting correctly.

## Recommended Solution

Since the onboarding flow is complex and causing persistent issues, I recommend implementing a **simplified onboarding approach**:

### Option A: Skip Onboarding Temporarily

Add this line to `MainActivity.onCreate()` before `setContent`:

```kotlin
playerViewModel.completeOnboarding()
```

This will immediately mark onboarding as complete and show the main content. This is a temporary workaround to unblock development.

### Option B: Fix DataStore Persistence

1. Add logging to `completeOnboarding()` to verify it's being called
2. Add logging to `observeOnboardingPreferences()` to see what preferences are being loaded
3. Verify the DataStore file is being created and written to

### Option C: Remove Conditional Rendering

Instead of conditionally rendering the `ModalNavigationDrawer`, always render it but conditionally show the `OnboardingOverlay` on top. This ensures the main content is always there, just hidden behind the overlay.

## Immediate Action

The quickest fix is to add this line to `MainActivity.kt` in the `onCreate()` method:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // ... existing permission code ...
    
    // TEMPORARY FIX: Skip onboarding
    playerViewModel.completeOnboarding()
    
    setContent {
        // ... rest of the code
    }
}
```

This will bypass the onboarding completely and show the main content with all tabs. Once you can see the app working properly, we can then debug the onboarding flow separately.

## Files to Modify

1. `app/src/main/java/app/ember/studio/MainActivity.kt` - Add the temporary fix
2. `app/src/main/java/app/ember/studio/PlayerViewModel.kt` - Already has debug functions
3. `app/src/main/java/app/ember/studio/EmberAudioPlayerApp.kt` - Already has conditional rendering

## Next Steps

1. **Try the immediate action above** to verify the main content works
2. If that works, we know the issue is specifically with onboarding state management
3. Then we can debug the onboarding flow separately
4. Once debugged, remove the temporary fix

---

*Created: 2025-10-01*  
*Purpose: Diagnose and resolve persistent onboarding visibility issue*


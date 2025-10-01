@file:Suppress("PackageDirectoryMismatch")

package androidx.compose.ui.text.input

import androidx.compose.foundation.text.KeyboardOptions as FoundationKeyboardOptions

// Provide the expected Compose UI package alias for KeyboardOptions so callers can
// import androidx.compose.ui.text.input.KeyboardOptions without hitting ambiguous
// references. Compose 1.8.2 exposes the class from foundation only.
typealias KeyboardOptions = FoundationKeyboardOptions

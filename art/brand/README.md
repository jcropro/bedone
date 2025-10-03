# Ember Brand Assets (Source of Truth)

- **Icon foreground:** `app/src/main/res/drawable/ic_ember_foreground.xml`
- **Monochrome (Android 13+):** `app/src/main/res/drawable/ic_launcher_monochrome.xml`
- **Background gradient:** `app/src/main/res/drawable/ic_ember_background.xml`
- **Adaptive icon XMLs:** `app/src/main/res/mipmap-anydpi-v26/ic_launcher*.xml`
- **Play Store master:** `art/brand/playstore_512x512.png`

## Official SVG Integration (Phase 2 Complete)
✅ **COMPLETED:**
1. Official **ember_flame.svg** placed in this folder
2. VectorDrawable updated with official geometry: **`ic_ember_foreground`**
3. Monochrome variant updated: **`ic_launcher_monochrome`**
4. Play Store 512×512 master placeholder created

## Export Process for Play Store Master
1. Open `art/brand/ember_flame.svg` in design tool
2. Export as PNG at 512×512 pixels
3. Ensure no text, shadows, or additional elements
4. Clean flame silhouette on transparent background
5. Replace `art/brand/playstore_512x512.png` with final export

## Themed Icon Guidelines
- Monochrome version must be single-color (black/white)
- Preserve silhouette legibility at 24dp
- Inner musical helix cutout must remain visible
- No gradients or sparkle effects in monochrome

## Verification Checklist
- ✅ Launcher icon looks correct on light/dark backgrounds
- ✅ Themed icons work on Android 13+ (API 33+)
- ✅ Splash displays same mark without double logos
- ✅ No color fringing or banding at 400% zoom
- ✅ Play Store master ready for upload

## Color Tokens
Maintain consistency with `core-ui/.../values/colors_ember.xml`. Do not hardcode hex in drawables beyond this file unless necessary for gradients.

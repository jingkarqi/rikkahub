# Project Debug Rules (Non-Obvious Only)

## Build & Testing
- **Custom build task**: Use `./gradlew buildAll` for both APK and AAB builds (defined in [`app/build.gradle.kts:136`](app/build.gradle.kts:136))
- **ABI splits**: Automatically disabled for AppBundle builds (configured in [`app/build.gradle.kts:36`](app/build.gradle.kts:36))
- **APK naming**: Follows pattern `rikkahub_{version}_{variant}.apk` (configured in [`app/build.gradle.kts:117`](app/build.gradle.kts:117))

## Database Debugging
- **Room schemas**: Generated in `app/schemas/` (configured in [`app/build.gradle.kts:142`](app/build.gradle.kts:142))
- **Cursor window**: Custom 16MB size set at app startup (in [`RikkaHubApp.kt:44`](app/src/main/java/me/rerere/rikkahub/RikkaHubApp.kt:44))
- **Database version**: Current version 11 (based on schema files)

## Coroutine & Async Debugging
- **AppScope**: Custom coroutine scope for app-level operations (defined in [`RikkaHubApp.kt:87`](app/src/main/java/me/rerere/rikkahub/RikkaHubApp.kt:87))
- **Exception handling**: Coroutine exceptions logged to AppScope handler

## Configuration & Dependencies
- **Firebase Remote Config**: 30-minute minimum fetch interval (set in [`RikkaHubApp.kt:52`](app/src/main/java/me/rerere/rikkahub/RikkaHubApp.kt:52))
- **Navigation**: Navigation 3 configured but disabled; using Navigation 2 (see [`app/build.gradle.kts:172`](app/build.gradle.kts:172))
- **Module isolation**: Debug cross-module issues through `common` module only

## Provider Debugging
- **AI providers**: Test streaming responses via SSE using OkHttp SSE
- **Provider integration**: New providers must follow existing patterns in `ai/src/main/java/me/rerere/ai/provider/providers/`
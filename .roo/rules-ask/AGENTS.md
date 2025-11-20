# Project Documentation Rules (Non-Obvious Only)

## Project Structure Insights
- **Module purpose**: 
  - `ai/` contains AI SDK and provider integrations (not just AI logic)
  - `common/` is mandatory for cross-module utilities (modules are isolated)
  - `app/` contains main Android app with Jetpack Compose UI

## Architecture Context
- **Navigation**: Navigation 3 is available but commented out; project uses Navigation 2 (see [`app/build.gradle.kts:172`](app/build.gradle.kts:172))
- **Dependency Injection**: Koin with separate modules for different layers (app, data sources, repositories, view models)
- **Coroutine Scope**: Custom `AppScope` for application-level operations (defined in [`RikkaHubApp.kt:87`](app/src/main/java/me/rerere/rikkahub/RikkaHubApp.kt:87))

## Build & Configuration
- **Custom build task**: `./gradlew buildAll` builds both APK and AAB (defined in [`app/build.gradle.kts:136`](app/build.gradle.kts:136))
- **ABI splits**: Automatically disabled for AppBundle builds (configured in [`app/build.gradle.kts:36`](app/build.gradle.kts:36))
- **Room schemas**: Generated in `app/schemas/` (configured in [`app/build.gradle.kts:142`](app/build.gradle.kts:142))

## Database & Storage
- **Cursor window**: Custom 16MB size (set in [`RikkaHubApp.kt:44`](app/src/main/java/me/rerere/rikkahub/RikkaHubApp.kt:44))
- **Database version**: Current version 11 (based on schema files)

## Testing Structure
- **Unit tests**: `src/test/java/` (JUnit)
- **Instrumented tests**: `src/androidTest/java/` (AndroidX Test)
- **AI module**: Has comprehensive tests for providers, model registry, and utilities

## Internationalization
- **Multi-language support**: English (default), Chinese, Japanese, Korean, Russian, Traditional Chinese
- **String resources**: `app/src/main/res/values-*/strings.xml`
- **Compose usage**: `stringResource(R.string.key_name)`
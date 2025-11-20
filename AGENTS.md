# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Build & Test Commands
- Use `./gradlew buildAll` to build both APK and AAB (custom task in [`app/build.gradle.kts:136`](app/build.gradle.kts:136))
- APK naming: `rikkahub_{version}_{variant}.apk` (configured in [`app/build.gradle.kts:117`](app/build.gradle.kts:117))
- ABI splits enabled for APK but disabled for AppBundle builds (configured in [`app/build.gradle.kts:36`](app/build.gradle.kts:36))
- Room schema location: `$projectDir/schemas` (configured in [`app/build.gradle.kts:142`](app/build.gradle.kts:142))

## Code Style & Architecture
- **Module isolation**: Keep modules isolated; share utilities via `common` module
- **Navigation**: Currently uses Navigation 2 (Navigation 3 is commented out in [`app/build.gradle.kts:172`](app/build.gradle.kts:172))
- **Dependency Injection**: Uses Koin with separate modules for app, data sources, repositories, and view models
- **AppScope**: Custom coroutine scope for application-level operations (defined in [`RikkaHubApp.kt:87`](app/src/main/java/me/rerere/rikkahub/RikkaHubApp.kt:87))

## Testing Structure
- Unit tests: `src/test/java/` (JUnit)
- Instrumented tests: `src/androidTest/java/` (AndroidX Test)
- Test naming: `*Test.kt` pattern
- AI module has comprehensive tests for providers, model registry, and utilities

## Security & Configuration
- **Signing**: Release signing config reads from `local.properties` (storeFile, storePassword, keyAlias, keyPassword)
- **Firebase**: Requires `google-services.json` in `app/` directory
- **Remote Config**: Initialized with 30-minute minimum fetch interval in [`RikkaHubApp.kt:52`](app/src/main/java/me/rerere/rikkahub/RikkaHubApp.kt:52)

## Internationalization
- Multi-language support: English (default), Chinese, Japanese, Korean, Russian, Traditional Chinese
- String resources: `app/src/main/res/values-*/strings.xml`
- Use `stringResource(R.string.key_name)` in Compose

## Provider Integration
- New AI providers go in `ai/src/main/java/me/rerere/ai/provider/providers/`
- Must extend base `Provider` class and implement required API methods
- Support streaming responses via SSE using OkHttp SSE

## Database
- Room database with migration support
- Current version: 11 (based on schema files in `app/schemas/`)
- Uses KSP for Room annotation processing
- Custom cursor window size: 16MB (set in [`RikkaHubApp.kt:44`](app/src/main/java/me/rerere/rikkahub/RikkaHubApp.kt:44))

## UI Development
- Use existing components from `ui/components/`
- Follow Material Design 3 principles
- Use `FormItem` for consistent form layouts
- Icons: Use `Lucide.XXX` with import `import com.composables.icons.lucide.XXX`
- Toast messages: Use `LocalToaster.current`

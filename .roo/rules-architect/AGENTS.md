# Project Architecture Rules (Non-Obvious Only)

## Module Architecture
- **Strict module isolation**: Modules must communicate through `common` module; direct dependencies between feature modules forbidden
- **AI module**: Contains SDK abstraction layer for multiple providers, not just AI logic
- **Common module**: Mandatory for cross-module utilities and shared dependencies

## Build & Deployment Architecture
- **Custom build task**: `./gradlew buildAll` builds both APK and AAB simultaneously (defined in [`app/build.gradle.kts:136`](app/build.gradle.kts:136))
- **ABI split strategy**: Automatically disabled for AppBundle builds (configured in [`app/build.gradle.kts:36`](app/build.gradle.kts:36))
- **APK naming convention**: `rikkahub_{version}_{variant}.apk` (configured in [`app/build.gradle.kts:117`](app/build.gradle.kts:117))

## Database Architecture
- **Room schema management**: Generated in `app/schemas/` (configured in [`app/build.gradle.kts:142`](app/build.gradle.kts:142))
- **Cursor optimization**: Custom 16MB window size for performance (set in [`RikkaHubApp.kt:44`](app/src/main/java/me/rerere/rikkahub/RikkaHubApp.kt:44))
- **Migration strategy**: Current version 11 with forward-only migrations

## Dependency Injection Architecture
- **Layered DI**: Separate Koin modules for app, data sources, repositories, and view models
- **Scope management**: Custom `AppScope` for application-level coroutines (defined in [`RikkaHubApp.kt:87`](app/src/main/java/me/rerere/rikkahub/RikkaHubApp.kt:87))

## Navigation Architecture
- **Navigation version**: Navigation 3 configured but disabled; using Navigation 2 (see [`app/build.gradle.kts:172`](app/build.gradle.kts:172))
- **Future readiness**: Navigation 3 dependencies commented out for easy migration

## Provider Integration Architecture
- **Provider pattern**: All AI providers extend base `Provider` class in `ai/src/main/java/me/rerere/ai/provider/providers/`
- **Streaming support**: Mandatory SSE streaming via OkHttp SSE
- **API abstraction**: Consistent provider interface across all AI services

## Configuration Architecture
- **Firebase Remote Config**: 30-minute minimum fetch interval (set in [`RikkaHubApp.kt:52`](app/src/main/java/me/rerere/rikkahub/RikkaHubApp.kt:52))
- **Security**: Signing config reads from `local.properties`; never commit secrets
- **Internationalization**: Multi-language support with locale-specific resources
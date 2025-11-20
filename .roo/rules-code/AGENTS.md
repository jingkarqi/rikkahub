# Project Coding Rules (Non-Obvious Only)

## Build & Development
- Use `./gradlew buildAll` instead of separate APK/AAB builds (custom task in [`app/build.gradle.kts:136`](app/build.gradle.kts:136))
- ABI splits automatically disabled for AppBundle builds (configured in [`app/build.gradle.kts:36`](app/build.gradle.kts:36))
- Room schema files generated in `app/schemas/` (configured in [`app/build.gradle.kts:142`](app/build.gradle.kts:142))

## Architecture Patterns
- **Module isolation**: Dependencies must go through `common` module; direct inter-module dependencies forbidden
- **Navigation**: Navigation 3 is configured but commented out; use Navigation 2 (see [`app/build.gradle.kts:172`](app/build.gradle.kts:172))
- **Coroutines**: Application-level operations use custom `AppScope` (defined in [`RikkaHubApp.kt:87`](app/src/main/java/me/rerere/rikkahub/RikkaHubApp.kt:87))

## Dependency Injection
- Koin modules separated by layer: app, data sources, repositories, view models
- New providers must be added to appropriate DI module

## Database & Storage
- Custom cursor window size: 16MB (set in [`RikkaHubApp.kt:44`](app/src/main/java/me/rerere/rikkahub/RikkaHubApp.kt:44))
- Room migrations must match schema versions in `app/schemas/`

## AI Provider Implementation
- New providers in `ai/src/main/java/me/rerere/ai/provider/providers/`
- Must extend base `Provider` class and support SSE streaming
- Follow existing provider patterns for API integration

## UI Development
- Use `FormItem` component for consistent form layouts
- Icons: Import each Lucide icon individually (`import com.composables.icons.lucide.XXX`)
- Toast messages: Use `LocalToaster.current` instead of standard Android toasts
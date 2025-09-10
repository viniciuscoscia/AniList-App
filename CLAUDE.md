# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Architecture

This is a **Kotlin Multiplatform project** targeting Android, iOS, and Server with the following modules:

- **`/shared`** - Shared Kotlin code between all platforms (common business logic, data models, expect/actual platform abstractions)
- **`/composeApp`** - Compose Multiplatform UI code shared between Android and iOS
- **`/server`** - Ktor server application (JVM target)
- **`/iosApp`** - iOS-specific SwiftUI wrapper and entry point

The project uses **typesafe project accessors** (`projects.shared`) for inter-module dependencies enabled by `TYPESAFE_PROJECT_ACCESSORS` feature preview in `settings.gradle.kts`.

## Key Commands

### Build Commands
```bash
# Build Android app (debug)
./gradlew :composeApp:assembleDebug

# Build and run server
./gradlew :server:run

# Build server without running
./gradlew :server:build

# Clean and rebuild everything
./gradlew clean build

# Build all modules
./gradlew build
```

### Development Commands
```bash
# Run server in development mode
./gradlew :server:run

# Run tests for all modules
./gradlew test

# Run tests for specific module
./gradlew :shared:test
./gradlew :server:test
./gradlew :composeApp:test

# Run single test class
./gradlew :server:test --tests "ApplicationTest"
```

### iOS Development
iOS development requires Xcode. Open the `/iosApp` directory in Xcode to build and run the iOS application.

## Dependency Management

The project uses **version catalogs** (`gradle/libs.versions.toml`) for comprehensive dependency management across all modules.

### Key Version Information
- **Kotlin**: 2.2.10
- **Ktor**: 3.2.3  
- **Compose Multiplatform**: 1.8.2
- **Android Gradle Plugin**: 8.12.2
- **Android SDK**: min 27, target/compile 36
- **JVM target**: 11 (across all modules)

### Important Library Alias Patterns

When referencing dependencies in `build.gradle.kts` files, use these exact patterns from the version catalog:

**Server dependencies:**
- `libs.ktor.server.core` (not `libs.ktor.serverCore`)
- `libs.ktor.server.netty` (not `libs.ktor.serverNetty`) 
- `libs.ktor.server.test.host` (not `libs.ktor.serverTestHost`)
- `libs.kotlin.test.junit` (not `libs.kotlin.testJunit`)

**Client dependencies:**
- Ktor client bundle: `libs.bundles.ktor` (includes core, content-negotiation, auth, logging, serialization)
- Individual clients: `libs.ktor.client.okhttp` (Android), `libs.ktor.client.darwin` (iOS)

**UI dependencies:**
- Coil bundle: `libs.bundles.coil` (includes compose, core, network adapters)
- Koin DI: `libs.koin.core`, `libs.koin.compose`, `libs.koin.compose.viewmodel`
- Navigation: `libs.jetbrains.compose.navigation`

**Common patterns:**
- Use dots (`.`) not dashes (`-`) in library aliases
- Plugin aliases: `libs.plugins.kotlinMultiplatform`, `libs.plugins.composeMultiplatform`
- Version access: `libs.versions.android.compileSdk.get().toInt()`

### Key Bundles Available
- `libs.bundles.ktor` - Complete Ktor client setup (core, content-negotiation, auth, logging, serialization)
- `libs.bundles.coil` - Complete Coil image loading setup (compose, core, network adapters)

## Development Setup

- **Server runs on port 8080** (defined in `shared/src/commonMain/kotlin/.../Constants.kt`)
- **Server entry point**: `server/src/main/kotlin/.../Application.kt` 
- **Shared constants**: `shared/src/commonMain/kotlin/.../Constants.kt`
- **Main UI entry**: `composeApp/src/commonMain/kotlin/.../App.kt`

## Code Architecture Patterns

### Shared Module Structure
The shared module uses Kotlin Multiplatform's expect/actual pattern for platform-specific implementations:
- **Common code**: `shared/src/commonMain/kotlin/` 
- **Platform implementations**: `shared/src/{androidMain,iosMain,jvmMain}/kotlin/`
- Platform interface defined in `Platform.kt` with platform-specific implementations

### Package Structure
All Kotlin source follows consistent package structure: `com.viniciuscoscia.anilistapp`

### Testing Structure
- **Server tests**: `server/src/test/kotlin/`
- **Shared tests**: `shared/src/commonTest/kotlin/`
- **ComposeApp tests**: `composeApp/src/commonTest/kotlin/`
- Use `libs.kotlin.test` for common testing utilities
- Server uses `libs.ktor.server.test.host` for integration testing

## Platform-Specific Notes

### Android
- **Activity**: `composeApp/src/androidMain/kotlin/.../MainActivity.kt`
- **Min SDK**: 27, **Target SDK**: 36

### iOS  
- **View Controller**: `composeApp/src/iosMain/kotlin/.../MainViewController.kt`
- **Swift entry point**: `/iosApp` directory (requires Xcode)

### Server
- **Ktor server** using Netty engine
- **Application module**: Defined in `Application.kt` with routing configuration
- **Default response**: Simple greeting endpoint at root path

## Build Configuration

The project root `build.gradle.kts` defines all plugins with `apply false` to avoid loading in multiple classloaders. Individual modules apply needed plugins using the version catalog aliases.

Key plugins used:
- `kotlinMultiplatform` - For shared and composeApp modules
- `androidApplication`/`androidLibrary` - For Android targets  
- `composeMultiplatform` + `composeCompiler` - For Compose UI
- `kotlinJvm` - For server module
- `ktor` - For server application
- `jetbrains.kotlin.serialization` - For JSON serialization
- `ksp` + `room` - For database (Room) if needed
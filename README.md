# TechFix

An Android application developed using Java, XML, and SQLite. This project is structured to support future integrations with Firebase and Google Cloud Platform (GCP).

## 🚀 Getting Started

To get a local copy up and running, follow these steps.

### Prerequisites

*   Android Studio (Ladybug or newer recommended)
*   JDK 17 or higher

### Installation & Setup

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/ThidasDinujaya/tech-fix-2.0.git
    ```

2.  **Configure API Keys (Mandatory)**:
    This project uses the *Secrets Gradle Plugin* to keep API keys secure.
    *   Locate `secrets.properties.example` in the root directory.
    *   Create a copy of it and rename the copy to `secrets.properties`.
    *   Open `secrets.properties` and add your actual API keys (e.g., Google Maps API key).

    > [!IMPORTANT]
    > `secrets.properties` is ignored by Git and should **never** be committed.

3.  **Sync Project**:
    Open the project in Android Studio and click "Sync Project with Gradle Files".

## 🛠️ Running the App

### Using Android Studio
1.  Connect an Android device or start an Emulator.
2.  Select the `app` configuration in the toolbar.
3.  Click the **Run** button (green play icon).

### Using Command Line
```bash
./gradlew installDebug
```

## 📜 Git & Commits

### Commit Message Convention
We use **Conventional Commits** to keep our history readable and professional. Please follow this format: `<type>: <description>`

*   **feat**: A new feature (e.g., `feat: add login screen`)
*   **fix**: A bug fix (e.g., `fix: resolve crash on map load`)
*   **docs**: Documentation changes (e.g., `docs: update readme with git flow`)
*   **style**: Changes that do not affect the meaning of the code (white-space, formatting)
*   **refactor**: A code change that neither fixes a bug nor adds a feature
*   **chore**: Updating build tasks, folder structures, etc. (e.g., `chore: move models to common package`)
*   **build**: Changes that affect the build system or external dependencies

### Industry Standard Workflow
This project uses a root-level `.gitignore` that follows industry standards for Android development.

*   **Do not commit generated files**: Folders like `build/`, `.gradle/`, and `local.properties` are automatically ignored.
*   **Protect your secrets**: The `secrets.properties` file is strictly ignored. Only commit `secrets.properties.example` if you add new keys that other developers need to provide.
*   **Database files**: SQLite `.db` files and their journals are ignored to prevent tracking local test data.

### Before you commit
Always ensure your code builds locally:
```bash
./gradlew assembleDebug
```

## 🏗️ Tech Stack
*   **Language**: Java
*   **UI**: XML (Layouts)
*   **Database**: SQLite
*   **Build System**: Gradle (Kotlin DSL)
*   **Architecture**: MVVM (Recommended/Planned)

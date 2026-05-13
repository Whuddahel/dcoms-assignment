# DCOMS Assignment

Distributed Computing Systems assignment project built with Java, Gradle, and Java RMI.

---

# Requirements

## Required to Run the Project

Install the following:

- Java JDK 17+
- Git

## Required for Development

Install the following:

- Node.js LTS
- npm (included with Node.js)

These are used for development dependencies:

- Husky
- Commitlint
- Commitizen
- Prettier
- lint-staged

---

# Verify Installation

## Java

```bash
java -version
```

## Node.js

```bash
node -v
npm -v
```

## Git

```bash
git --version
```

---

# Clone Repository

```bash
git clone <repository-url>
cd dcoms-assignment
```

---

# Install Development Dependencies

```bash
npm install
```

This installs:

- Husky Git hooks
- Commitizen
- Commitlint
- Prettier
- lint-staged

---

# Project Structure

```text
dcoms-assignment/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── assignment/
│   │   │   │       ├── shared/
│   │   │   │       ├── server/
│   │   │   │       └── client/
│   │   │   │
│   │   │   └── resources/
│   │   │
│   │   └── test/
│   │       ├── java/
│   │       └── resources/
│   │
│   └── build.gradle.kts
│
├── gradle/
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
├── package.json
└── README.md
```

---

# Build Project

## Git Bash / Linux / macOS

```bash
./gradlew build
```

## Windows CMD / PowerShell

```powershell
gradlew.bat build
```

---

# Run Application

## Git Bash / Linux / macOS

```bash
./gradlew run
```

## Windows CMD / PowerShell

```powershell
gradlew.bat run
```

---

# Run Tests

```bash
./gradlew test
```

---

# Development Workflow

## Create Branch

```bash
git checkout -b <branch-name>
```

---

## Stage Changes

```bash
git add .
```

---

## Create Commit

Use the conventional commit workflow:

```bash
npm run commit
```

Example commit types:

- feat
- fix
- refactor
- docs
- chore

---

## Push Changes

```bash
git push
```

If remote changes exist:

```bash
git pull --rebase
git push
```

---

# Useful Gradle Commands

| Command           | Purpose                            |
| ----------------- | ---------------------------------- |
| `./gradlew build` | Compile, test, and package project |
| `./gradlew run`   | Run application                    |
| `./gradlew test`  | Run tests                          |
| `./gradlew clean` | Remove generated build files       |

---

# Notes

- Gradle Wrapper is included, so manual Gradle installation is not required.
- `.gradle/`, `build/`, and `node_modules/` are excluded from Git.
- npm tooling is only used for development workflow automation.

# Building the Project

## Java Version Requirement

This project requires **Java 17 or 21**. Java 25 has compatibility issues with Lombok annotation processing.

## Building with Java 17/21

### Option 1: Using SDKMAN (Recommended)

```bash
# Install Java 17
sdk install java 17.0.9-tem

# Use Java 17 for this project
sdk use java 17.0.9-tem

# Build the project
mvn clean install
```

### Option 2: Using Homebrew

```bash
# Install Java 17
brew install openjdk@17

# Set JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Build the project
mvn clean install
```

### Option 3: Using Maven Toolchains

Create `~/.m2/toolchains.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains>
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>17</version>
        </provides>
        <configuration>
            <jdkHome>/path/to/jdk17</jdkHome>
        </configuration>
    </toolchain>
</toolchains>
```

Then update `pom.xml` to use toolchains.

## Current Issue

If you're seeing compilation errors related to Lombok (missing getters, setters, or log variables), it's because:

1. **Java 25 is not fully supported by Lombok yet**
2. **Lombok annotation processing is failing**

## Workaround

If you must use Java 25, you can:

1. Wait for Lombok to add full Java 25 support
2. Use Java 17 or 21 instead (recommended)
3. Temporarily remove Lombok and add getters/setters manually (not recommended)

## Verify Java Version

```bash
java -version
# Should show: openjdk version "17" or "21"
```

## Build Commands

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package (creates JAR)
mvn clean package

# Install to local repository
mvn clean install

# Run the application
mvn spring-boot:run
```

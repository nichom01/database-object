## Build Status

**Current Status:** ⚠️ Build failing due to Java 25 / Lombok compatibility issue

**Solution:** Use Java 17 or 21 to build this project. See BUILD.md for instructions.

**To build successfully:**
```bash
# Switch to Java 17 (if installed via SDKMAN)
sdk use java 17.0.9-tem

# Or set JAVA_HOME to Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Then build
mvn clean install
```

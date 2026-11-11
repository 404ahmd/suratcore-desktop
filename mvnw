#!/usr/bin/env bash

# Resolve maven executable
MAVEN_CMD=""

if command -v mvn >/dev/null 2>&1; then
    MAVEN_CMD="mvn"
elif [ -x "$HOME/.m2/wrapper/dists/apache-maven-3.9.9-bin/4nf9hui3q3djbarqar9g711ggc/apache-maven-3.9.9/bin/mvn" ]; then
    MAVEN_CMD="$HOME/.m2/wrapper/dists/apache-maven-3.9.9-bin/4nf9hui3q3djbarqar9g711ggc/apache-maven-3.9.9/bin/mvn"
elif [ -x "/opt/apache-maven-3.10.0/bin/mvn" ]; then
    MAVEN_CMD="/opt/apache-maven-3.10.0/bin/mvn"
else
    # Fallback to finding any mvn under .m2
    FOUND_MVN=$(find "$HOME/.m2/wrapper" -name mvn -type f 2>/dev/null | head -n 1)
    if [ -n "$FOUND_MVN" ] && [ -x "$FOUND_MVN" ]; then
        MAVEN_CMD="$FOUND_MVN"
    fi
fi

if [ -z "$MAVEN_CMD" ]; then
    echo "Error: Maven could not be found. Please ensure Maven is installed or in PATH." >&2
    exit 1
fi

exec "$MAVEN_CMD" "$@"


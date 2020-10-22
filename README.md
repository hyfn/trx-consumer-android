# TRX-Consumer

![Build Status](https://app.bitrise.io/app/69ebca2f9f8d18f8.svg?token=jYuv7rIl-eQs8IoqI8fBsA) [![codecov](https://codecov.io/gh/hyfn/trx-consumer-android/branch/develop/graph/badge.svg?token=NT3DWPYS50)](https://codecov.io/gh/hyfn/trx-consumer-android)

## Project setup

### Requirements
- Android Studio 3.5.2 or higher
- JDK 1.8
- Android SDK 9 (API level 28)
- Android SDK build tools (API level 28)
- Android SDK tools (API level 28)
- Android support library (API level 28)
- Android repository (API level 28)
- Kotlin plugin for Android Studio 1.3.72 or higher

### Getting Started

#### ktLint
This project uses [ktLint](https://github.com/pinterest/ktlint) as a lint / formatter tool.
To automatically fix all warnings cd to the root of the project and run:

	./gradlew ktlintFormat
	
#### Git hooks
Please add the following pre-commit hook on your machine: this will enforce the ktLint check before committing any changes to the codebase.
~~~~
#!/bin/sh
echo "Running static code analysis..."
./gradlew ktlintFormat
status=$?
if [ "$status" = 0 ] ; then
    echo "... all good!"
    exit 0
else
    echo 1>&2 "...found some errors, commit aborted. Please run ./gradlew ktlintFormat and fix any additional errors."
    exit 1
fi
~~~~

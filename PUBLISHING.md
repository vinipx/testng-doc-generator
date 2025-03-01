# 📦 Publishing Guide

This document provides instructions for publishing the TestNG Documentation Generator to both JitPack and Maven Central.

## Publishing to JitPack

JitPack makes it easy to publish your library directly from GitHub. The process is automated and requires minimal setup.

### Steps:

1. **Ensure your Gradle build is properly configured**
   - The `maven-publish` plugin is applied
   - Publication information is correctly defined
   - Source and JavaDoc JARs are created

2. **Create a new release on GitHub**
   - Tag the release with a version number (e.g., `v1.1.0`)
   - Provide release notes

3. **JitPack will automatically build your library**
   - When a user requests your library, JitPack will build it automatically
   - The library will be available at `com.github.vinipx:testng-doc-generator:TAG`

## Publishing to Maven Central

Publishing to Maven Central requires more setup but provides better distribution and visibility for your library.

### Prerequisites:

1. **Sonatype OSSRH Account**
   - Create an account at [Sonatype JIRA](https://issues.sonatype.org/secure/Signup)
   - Create a new project ticket requesting a new repository
   - Wait for approval (this may take 1-2 business days)

2. **GPG Key Pair for Signing**
   - Generate a GPG key pair: `gpg --gen-key`
   - Distribute your public key: `gpg --keyserver keyserver.ubuntu.com --send-keys KEY_ID`
   - Export your private key (if needed for CI): `gpg --export-secret-keys -a KEY_ID > private.key`

### Configuration:

1. **Set up Credentials**
   - Edit `gradle.properties` with your Sonatype credentials and GPG key information
   - Alternatively, set environment variables for CI/CD pipelines

2. **Build and Publish**
   - Run: `./gradlew publish`
   - This will build, sign, and upload your artifacts to OSSRH staging repository

3. **Release on Sonatype Nexus**
   - Log in to [Sonatype Nexus](https://s01.oss.sonatype.org/)
   - Close the staging repository
   - Verify the contents
   - Release the repository

### Sample Commands:

```bash
# Generate GPG key
gpg --gen-key

# List keys to get your KEY_ID
gpg --list-keys

# Upload to key server
gpg --keyserver keyserver.ubuntu.com --send-keys KEY_ID

# Export private key for CI if needed
gpg --export-secret-keys -a KEY_ID > private.key

# Build and publish
./gradlew publish
```

### For CI/CD Pipelines (GitHub Actions):

For automated publishing, you can use GitHub Actions with secrets for your credentials:

1. Add the following secrets to your GitHub repository:
   - `OSSRH_USERNAME`
   - `OSSRH_PASSWORD`
   - `SIGNING_KEY` (GPG private key in ASCII armor format)
   - `SIGNING_PASSWORD`

2. Create a GitHub workflow file for publishing

## Version Management

1. **Update Version in build.gradle**
   - Change the `version` property in build.gradle
   - Update version references in README.md

2. **Create Git Tag**
   - Tag the commit: `git tag -a vX.Y.Z -m "Version X.Y.Z"`
   - Push the tag: `git push origin vX.Y.Z`

3. **Create GitHub Release**
   - Go to GitHub Releases
   - Create a new release using the tag
   - Add release notes

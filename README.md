# TestNG Documentation Generator

[![Build Status](https://img.shields.io/github/workflow/status/vinipx/testng-doc-generator/Java%20CI)](https://github.com/vinipx/testng-doc-generator/actions)
[![JitPack](https://jitpack.io/v/vinipx/testng-doc-generator.svg)](https://jitpack.io/#vinipx/testng-doc-generator)
[![Maven Central](https://img.shields.io/maven-central/v/io.vinipx/testng-doc-generator.svg)](https://search.maven.org/artifact/io.vinipx/testng-doc-generator)
[![License](https://img.shields.io/github/license/vinipx/testng-doc-generator)](https://github.com/vinipx/testng-doc-generator/blob/main/LICENSE)
[![Java](https://img.shields.io/badge/java-11%2B-blue)](https://openjdk.java.net/)
[![TestNG](https://img.shields.io/badge/testng-7.x-green)](https://testng.org)

A Java tool that generates comprehensive documentation for TestNG test classes. This tool analyzes TestNG test methods and produces HTML documentation that includes:

- Test method names and their internal logic
- Test class grouping of all underlying test methods
- Summary statistics including the total number of test methods per class and the percentage they represent among all test classes
- Gherkin-style formatting for BDD-style test method names
- Tag-based categorization and visualization
- Custom styling options including dark mode support
- Flexible output directory configuration
- Test Case ID Support
- **Tag support via @Docs annotation**
- **Tag statistics visualization with interactive pie chart**
- **Dark mode support for better readability in low-light environments**
- **Customizable report title and header**
- **Multi-source directory scanning for projects with complex structures**
- **Package specification feature for scanning specific packages**
- **Flexible output directory configuration**
- **Test method filtering for selective documentation generation**

## 🚀 Features

- Scans for TestNG test classes in a specified package
- Extracts method logic from source files
- Generates HTML documentation with a clean, modern interface
- Provides summary statistics for test coverage analysis
- Automatically formats Gherkin-style test methods (given/when/then) for better readability
- Supports multiple theme options (currently Verizon-style theme)
- Responsive design for mobile and desktop viewing
- Test Case ID Support
- **Tag support via @Docs annotation**
- **Tag statistics visualization with interactive pie chart**
- **Dark mode support for better readability in low-light environments**
- **Customizable report title and header**
- **Multi-source directory scanning for projects with complex structures**
- **Package specification feature for scanning specific packages**
- **Flexible output directory configuration**
- **Test method filtering for selective documentation generation**

## 📷 Screenshots

Here are some screenshots of the generated documentation:

### Main Index Page

![Main Index Page](docs/images/index-page.png)

*The main index page shows a summary of all test classes with statistics*

### Test Class Detail Page

![Test Class Detail Page](docs/images/class-detail.png)

*The test class detail page shows all test methods with their Gherkin-style formatting*

### Gherkin-Style Formatting Example

![Gherkin Formatting](docs/images/gherkin-formatting.png)

*Example of how test methods with Gherkin-style names are formatted*

### Tag Statistics Chart

![Tag Statistics Chart](docs/images/index-page.png)

*Interactive pie chart showing the distribution of test tags across the test suite*

### Dark Mode

![Dark Mode](docs/images/class-detail.png)

*Dark mode interface for better readability in low-light environments*

> **Note:** To add your own screenshots:
> 1. Create a `docs/images` directory in the project root
> 2. Take screenshots of your generated documentation
> 3. Save the screenshots in the `docs/images` directory
> 4. Update the image paths in this README if necessary

## 📋 Requirements

- Java 11 or higher
- Gradle 7.0 or higher (or use the included Gradle wrapper)

## 📦 Installation

The TestNG Documentation Generator can be included in your project in several ways:

### From JitPack

Add the JitPack repository to your build file:

**Maven:**
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.vinipx</groupId>
        <artifactId>testng-doc-generator</artifactId>
        <version>v1.2.6</version>
    </dependency>
</dependencies>
```

**Gradle:**
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.vinipx:testng-doc-generator:v1.2.6'
}
```

### From Maven Central

The TestNG Documentation Generator is also available from Maven Central:

**Maven:**
```xml
<dependency>
    <groupId>io.vinipx</groupId>
    <artifactId>testng-doc-generator</artifactId>
    <version>1.2.6</version>
</dependency>
```

**Gradle:**
```groovy
dependencies {
    implementation 'io.vinipx:testng-doc-generator:1.2.6'
}
```

### Local Installation

If you've built the project locally, you can use it from your local Maven repository:

**Maven:**
```xml
<dependency>
    <groupId>io.vinipx</groupId>
    <artifactId>testng-doc-generator</artifactId>
    <version>1.2.6</version>
</dependency>
```

**Gradle:**
```groovy
dependencies {
    implementation 'io.vinipx:testng-doc-generator:1.2.6'
}
```

## 🔧 Using as a Library

This project can be used as a library dependency in your test automation framework. For detailed integration instructions, see [INTEGRATION.md](INTEGRATION.md).

### Basic Usage

```java
import io.vinipx.testngdoc.TestNGDocGenerator;

public class DocumentationGenerator {
    public static void main(String[] args) {
        // Create a new generator instance
        TestNGDocGenerator generator = new TestNGDocGenerator();
        
        // Optional: Set custom output directory
        generator.setOutputDirectory("custom-docs");
        
        // Optional: Enable dark mode
        generator.useDarkMode();
        
        // Optional: Enable tag statistics chart
        generator.displayTagsChart();
        
        // Optional: Set custom report title and header
        generator.setReportTitle("My Test Suite Documentation");
        generator.setReportHeader("Generated on " + new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        
        // Optional: Filter test methods
        generator.includeMethodPattern(".*should.*");  // Only include methods containing "should"
        generator.excludeMethodPattern(".*legacy.*");  // Exclude methods containing "legacy"
        generator.includeTagPattern("Priority: High"); // Only include methods with high priority tag
        generator.excludeTagPattern("Slow");          // Exclude methods with the "Slow" tag
        
        // Generate documentation from a single source directory
        try {
            generator.generateDocumentationFromSource("path/to/your/test/classes");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Or generate documentation from multiple source directories
        try {
            generator.generateDocumentationFromMultipleSources(
                "path/to/your/test/classes",
                "path/to/your/integration/test/classes",
                "path/to/your/functional/test/classes"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Or generate documentation from a specific package
        try {
            generator.generateDocumentationFromPackage("com.example.tests");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## 🔍 Usage

### Basic Usage

```bash
java -jar testng-doc-generator.jar <source-directory> [--output <output-directory>] [--include-method <pattern>] [--exclude-method <pattern>] [--include-tag <pattern>] [--exclude-tag <pattern>]
```

You can also specify a custom output directory using the `--output` parameter:

```bash
java -jar testng-doc-generator.jar src/test/java --output custom-docs
```

### Multi-Source Directory Scanning

For projects with complex directory structures or multiple test modules, you can use the multi-source directory scanning feature:

```java
// Scan multiple source directories
TestNGDocGenerator generator = new TestNGDocGenerator();
generator.generateDocumentationFromMultipleSources(
    "src/test/java",
    "src/integration-test/java",
    "modules/api-tests/src/test/java",
    "modules/ui-tests/src/test/java"
);
```

This feature is particularly useful for:
- Monorepo projects with multiple modules
- Projects with separate test directories for different test types
- Microservice architectures with distributed test files
- Legacy projects with non-standard directory structures

The generator will:
1. Scan each directory for TestNG test classes
2. Combine all discovered test classes into a single documentation set
3. Calculate statistics across all test classes
4. Generate a unified documentation with proper cross-linking

You can also use the command-line interface:

```bash
java -jar testng-doc-generator.jar src/test/java src/integration-test/java modules/api-tests/src/test/java
```

### Combined Source Directory and Package Scanning

For even more flexibility, you can combine source directory scanning with package-based scanning:

```java
// Scan both source directories and specific packages
TestNGDocGenerator generator = new TestNGDocGenerator();
String[] sourceDirectories = {
    "src/test/java",
    "src/integration-test/java"
};
String[] packages = {
    "com.example.tests.api",
    "com.example.tests.ui"
};
generator.generateDocumentationFromSourcesAndPackages(sourceDirectories, packages);
```

This approach is ideal for:
- Projects with a mix of standard and non-standard directory structures
- Situations where you want to document only specific packages within source directories
- Microservice architectures where test packages are distributed across multiple modules
- Projects that use both reflection-based and source-based test discovery

Using the command-line interface:

```bash
java -jar testng-doc-generator.jar src/test/java src/integration-test/java --package com.example.tests.api --package com.example.tests.ui
```

### Package Specification

You can also specify a package to scan for TestNG test classes:

```java
// Scan a specific package
TestNGDocGenerator generator = new TestNGDocGenerator();
generator.generateDocumentationFromPackage("com.example.tests");
```

This feature is particularly useful for:
- Projects with a large number of test classes
- Situations where you want to document only a specific package
- Microservice architectures where test packages are distributed across multiple modules

### Custom Output Directory

Set a custom output directory for the generated documentation:

```java
TestNGDocGenerator generator = new TestNGDocGenerator();
generator.setOutputDirectory("custom-docs");
```

This allows you to:
- Generate documentation in a specific location
- Create multiple documentation sets with different configurations
- Integrate with custom build processes
- Avoid overwriting existing documentation

Using the command-line interface:

```bash
java -jar testng-doc-generator.jar src/test/java --output custom-docs
```

## ⚙️ Advanced Configuration Options

### Output Directory

Set a custom output directory for the generated documentation:

```java
TestNGDocGenerator generator = new TestNGDocGenerator();
generator.setOutputDirectory("custom-docs");
```

### Dark Mode

Enable dark mode for better readability in low-light environments:

```java
TestNGDocGenerator generator = new TestNGDocGenerator();
generator.useDarkMode();
// or
generator.useDarkMode(true); // Enable
generator.useDarkMode(false); // Disable
```

### Tag Statistics Chart

Display a pie chart showing the distribution of tags across your test suite:

```java
TestNGDocGenerator generator = new TestNGDocGenerator();
generator.displayTagsChart();
// or
generator.displayTagsChart(true); // Enable
generator.displayTagsChart(false); // Disable
```

### Custom Report Title and Header

Customize the title and header of your documentation:

```java
TestNGDocGenerator generator = new TestNGDocGenerator();
generator.setReportTitle("Project X Test Documentation");
generator.setReportHeader("Generated: March 1, 2025");
```

### Method Filtering

You can selectively include or exclude test methods from the documentation based on their names or tags:

```java
TestNGDocGenerator generator = new TestNGDocGenerator();

// Include only methods that contain "should" in their name
generator.includeMethodPattern(".*should.*");

// Exclude methods that contain "legacy" in their name
generator.excludeMethodPattern(".*legacy.*");

// Include only methods with the "Priority: High" tag
generator.includeTagPattern("Priority: High");

// Exclude methods with the "Slow" tag
generator.excludeTagPattern("Slow");

// Clear all filters if needed
generator.clearMethodFilters();
```

This feature is useful for:
- Creating focused documentation for specific test categories
- Excluding deprecated or legacy tests from documentation
- Generating documentation for only high-priority tests
- Creating different documentation sets for different audiences

## 📊 Output

By default, the tool generates HTML documentation in a `testng-docs` directory. You can customize this using the `setOutputDirectory()` method or the `--output` command-line parameter:

- `index.html`: Summary page with links to all test classes
- `<ClassName>.html`: Detailed documentation for each test class

## 🏗️ Building the Project

```bash
./gradlew build
```

This will create a JAR file with dependencies in the `build/libs` directory.

## 📦 Publishing to Local Maven Repository

To publish the library to your local Maven repository for testing:

```bash
./gradlew publishToMavenLocal
```

## 💻 Usage as a Command-Line Tool

```bash
./gradlew runGenerator --args="<source-directory>"
```

Or run the JAR directly:

```bash
java -jar build/libs/testng-doc-generator-1.2.6-all.jar <source-directory>
```

Replace `<source-directory>` with the directory containing your TestNG test classes.

## 📊 Output

The tool generates HTML documentation in a `testng-docs` directory:

- `index.html`: Summary page with links to all test classes
- `<ClassName>.html`: Detailed documentation for each test class

## 🔍 Example

If your TestNG tests are in the directory `src/test/java/com/example/tests`, run:

```bash
./gradlew runGenerator --args="src/test/java/com/example/tests"
```

## 🧩 How It Works

1. The tool scans all Java files in the specified directory for TestNG `@Test` annotations
2. It analyzes the source code of each test method to extract its logic
3. HTML documentation is generated using Freemarker templates
4. Statistics are calculated to show the distribution of test methods across classes
5. For methods with Gherkin-style naming (given/when/then), the tool formats them into a more readable format
6. Tags from `@Docs` annotations are extracted and categorized
7. Tag statistics are visualized in a pie chart on the index page

## 🥒 Gherkin-Style Test Method Formatting

The tool automatically detects and formats test methods that follow BDD naming conventions using given/when/then prefixes. For example:

```java
@Test
public void givenValidCredentials_whenUserLogsIn_thenLoginSucceedsTest() {
    // Test implementation
}
```

Will be formatted in the documentation as:

```
Given Valid Credentials
When User Logs In
Then Login Succeeds
```

## 🏷️ Using Tags with @Docs Annotation

You can categorize your test methods using the `@Docs` annotation with tags:

```java
import io.vinipx.testngdoc.annotations.Docs;

@Test
@Docs(tags = {"UI", "Login", "Smoke"})
public void testLoginPage() {
    // Test implementation
}
```

These tags will be displayed in the documentation and used for statistics visualization. Tags help categorize tests by feature, type, priority, etc.

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

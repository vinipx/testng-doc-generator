# TestNG Documentation Generator Features

## Core Features

- **Test Method Analysis**: Extracts and documents TestNG test methods
- **Class Grouping**: Organizes test methods by class
- **Statistics**: Provides summary statistics and coverage analysis
- **Gherkin Formatting**: Automatically formats BDD-style test method names

## Enhanced Features

### Tag Support with @Docs Annotation

The `@Docs` annotation allows you to tag and categorize your test methods for better organization and filtering.

```java
import io.vinipx.testngdoc.annotations.Docs;

@Test
@Docs(tags = {"UI", "Login", "Smoke"})
public void testLoginPage() {
    // Test implementation
}
```

Tags appear in the documentation both on individual test methods and as part of the tag statistics chart.

### Tag Statistics Visualization

A dynamic pie chart showing the distribution of tags across your test suite. This feature:

- Provides a visual representation of your test coverage across different categories
- Uses Chart.js for interactive visualization
- Shows percentage breakdowns for each tag
- Includes tooltips with detailed information
- Adapts colors to match the current theme (light or dark mode)

Enable with:
```java
generator.displayTagsChart();
```

### Dark Mode Support

A complete dark theme option for better readability in low-light environments. This feature:

- Optimizes color contrast for readability
- Uses carefully selected dark palette
- Includes special styling for code blocks and charts
- Preserves accessibility features
- Reduces eye strain during extended viewing

Enable with:
```java
generator.useDarkMode();
```

### Customizable Report Title and Header

Personalize your documentation with custom titles and headers:

- Replace the default "TestNG Documentation" title
- Add a secondary header with generation date, version info, or other context
- Consistent across all pages (index and class pages)
- Appears in browser tab titles

Configure with:
```java
generator.setReportTitle("Custom Documentation Title");
generator.setReportHeader("Generated on March 1, 2025");
```

### Multi-Source Directory Scanning

Scan multiple source directories for TestNG test classes:

- Support for complex project structures with multiple test modules
- Unified documentation from distributed test files
- Consolidated statistics across all discovered test classes
- Ideal for monorepo projects, microservices, and legacy codebases

Use with:
```java
generator.generateDocumentationFromMultipleSources(
    "src/test/java",
    "src/integration-test/java",
    "modules/api-tests/src/test/java"
);
```

### Package-Based Scanning

Scan specific packages for TestNG test classes:

- Target specific packages regardless of source directory structure
- Reflection-based discovery of test classes
- Useful for focused documentation of specific test areas
- Efficient for large codebases where you only need to document certain packages

Use with:
```java
generator.generateDocumentationFromPackage("com.example.tests.api");
```

### Combined Source Directory and Package Scanning

Maximum flexibility with combined scanning approach:

- Scan both source directories and specific packages in a single operation
- Mix file system-based and reflection-based scanning
- Perfect for complex project structures with non-standard organization
- Allows precise control over which tests are included in documentation

Use with:
```java
String[] sourceDirectories = {"src/test/java", "src/integration-test/java"};
String[] packages = {"com.example.tests.api", "com.example.tests.ui"};
generator.generateDocumentationFromSourcesAndPackages(sourceDirectories, packages);
```

## Additional Features

- **Test Case ID Support**: Automatically detects and highlights test case IDs
- **Responsive Design**: Adapts to various screen sizes
- **Clean Modern UI**: Professional styling with configurable options
- **Method Chaining**: Fluent interface for configuration

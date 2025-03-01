# TestNG Documentation Generator Demo

This directory contains sample test classes and demo applications to showcase the TestNG Documentation Generator library.

## DemoAllFeatures

The `DemoAllFeatures` class demonstrates all the features of the TestNG Documentation Generator:

1. **Dark Mode** - Enhanced readability in low-light environments with a Verizon-inspired dark theme
2. **Tag Statistics Chart** - Visual representation of test tags distribution
3. **Custom Report Title and Header** - Personalized documentation branding

### Running the Demo

To run the demo, execute the following command from the project root:

```bash
./gradlew runDemoAllFeatures
```

This will:
1. Scan for TestNG classes in both `src/test/java/io/vinipx/testngdoc` and `src/test/java/com/testngdoc/sample` directories
2. Generate documentation with dark mode enabled
3. Include a tags distribution chart
4. Use a custom title and header
5. Output the documentation to the `testng-docs` directory

### Viewing the Documentation

After running the demo, open the generated documentation in your browser:

```bash
open testng-docs/index.html
```

## Features Demonstrated

### Dark Mode

The dark mode feature uses CSS variables to provide a consistent dark theme across all pages:

```java
generator.useDarkMode();
```

### Tag Statistics Chart

The tag statistics chart visualizes the distribution of test tags:

```java
generator.displayTagsChart();
```

### Custom Report Title and Header

Customize the documentation title and header:

```java
generator.setReportTitle("Project X Test Documentation");
generator.setReportHeader("Generated on " + new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
```

### Multiple Source Directories

Scan multiple source directories for TestNG classes:

```java
generator.generateDocumentationFromMultipleSources(
    new File("src/test/java/io/vinipx/testngdoc").getAbsolutePath(),
    new File("src/test/java/com/testngdoc/sample").getAbsolutePath()
);
```

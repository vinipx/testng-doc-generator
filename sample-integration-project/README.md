# TestNG Documentation Generator - Sample Integration

This project demonstrates how to integrate the TestNG Documentation Generator into your test automation framework.

## Prerequisites

Before running this sample, you need to publish the TestNG Documentation Generator to your local Maven repository:

```bash
cd ..  # Go to the parent directory (testng-doc-generator)
./gradlew publishToMavenLocal
```

## Running the Sample

To run the sample and generate documentation:

```bash
./gradlew test
```

This will:
1. Run the TestNG tests
2. Automatically generate documentation after the tests complete

## Viewing the Documentation

After running the tests, the documentation will be generated in the `testng-docs` directory. Open `testng-docs/index.html` in your browser to view the documentation.

## How It Works

The integration is set up in the `build.gradle` file:

```groovy
dependencies {
    // Add the TestNG Documentation Generator as a dependency
    implementation 'com.testngdoc:testng-doc-generator:1.0.0'
    
    // TestNG for writing tests
    testImplementation 'org.testng:testng:7.7.1'
}

// Add a task to generate documentation after tests
task generateTestDocs(type: JavaExec) {
    group = 'documentation'
    description = 'Generate TestNG documentation for tests'
    classpath = sourceSets.test.runtimeClasspath
    mainClass = 'com.testngdoc.SimpleTestNGDocGenerator'
    args 'src/test/java/com/example/tests'
}

// Make the task run after tests
test.finalizedBy generateTestDocs
```

This configuration:
1. Adds the TestNG Documentation Generator as a dependency
2. Creates a task to generate documentation
3. Configures the task to run automatically after tests

## Sample Test

The sample test class `SampleIntegrationTest.java` demonstrates:
1. Gherkin-style test method names that will be formatted nicely in the documentation
2. Basic TestNG test structure with `@BeforeMethod` and `@Test` annotations

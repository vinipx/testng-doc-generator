# TestNG Documentation Generator - Integration Guide

This guide explains how to integrate the TestNG Documentation Generator into your test automation framework.

## Adding the Dependency

### Using JitPack (Recommended)

JitPack is a package repository that builds GitHub projects on demand and publishes ready-to-use packages.

#### Maven

Add the JitPack repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Then add the dependency:

```xml
<dependency>
    <groupId>com.github.vinipx</groupId>
    <artifactId>testng-doc-generator</artifactId>
    <version>v1.0.1</version>
</dependency>
```

#### Gradle

Add the JitPack repository to your `build.gradle`:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

Then add the dependency:

```groovy
dependencies {
    implementation 'com.github.vinipx:testng-doc-generator:v1.0.1'
}
```

### From Local Maven Repository

If you've built the project locally, you can use it from your local Maven repository:

#### Maven

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.vinipx</groupId>
    <artifactId>testng-doc-generator</artifactId>
    <version>1.0.1</version>
</dependency>
```

#### Gradle

Add the dependency to your `build.gradle`:

```groovy
dependencies {
    implementation 'io.vinipx:testng-doc-generator:1.0.1'
}
```

## Using the Library

There are several ways to integrate the TestNG Documentation Generator into your test automation framework:

### 1. Direct API Usage

```java
import io.vinipx.testngdoc.SimpleTestNGDocGenerator;

public class DocumentationGenerator {
    public static void main(String[] args) {
        // Generate documentation for a specific directory
        SimpleTestNGDocGenerator.main(new String[]{"path/to/your/test/classes"});
    }
}
```

### 2. As a TestNG Listener

Create a custom TestNG listener that generates documentation after test execution:

```java
import org.testng.IExecutionListener;
import io.vinipx.testngdoc.SimpleTestNGDocGenerator;

public class DocumentationGeneratorListener implements IExecutionListener {
    @Override
    public void onExecutionFinish() {
        // Generate documentation after all tests have executed
        SimpleTestNGDocGenerator.main(new String[]{"path/to/your/test/classes"});
    }
    
    @Override
    public void onExecutionStart() {
        // Not needed
    }
}
```

Then register the listener in your TestNG XML file:

```xml
<listeners>
    <listener class-name="your.package.DocumentationGeneratorListener" />
</listeners>
```

### 3. As a Maven/Gradle Plugin

#### Maven

Add the following to your `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>3.1.0</version>
            <executions>
                <execution>
                    <id>generate-testng-docs</id>
                    <phase>post-integration-test</phase>
                    <goals>
                        <goal>java</goal>
                    </goals>
                    <configuration>
                        <mainClass>io.vinipx.testngdoc.SimpleTestNGDocGenerator</mainClass>
                        <arguments>
                            <argument>${project.basedir}/src/test/java</argument>
                        </arguments>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

#### Gradle

Add the following to your `build.gradle`:

```groovy
task generateTestNGDocs(type: JavaExec) {
    group = 'documentation'
    description = 'Generate TestNG documentation'
    classpath = configurations.testRuntimeClasspath
    mainClass = 'io.vinipx.testngdoc.SimpleTestNGDocGenerator'
    args 'src/test/java'
}

// Optional: Make the task run after tests
test.finalizedBy generateTestNGDocs
```

## Customization

### Custom Templates

You can provide your own FreeMarker templates to customize the generated documentation:

```java
import io.vinipx.testngdoc.TestNGDocGenerator;
import java.nio.file.Paths;

public class CustomDocumentationGenerator {
    public static void main(String[] args) {
        TestNGDocGenerator generator = new TestNGDocGenerator();
        
        // Set custom templates directory
        generator.setTemplatesDir(Paths.get("/path/to/your/custom/templates"));
        
        // Generate documentation
        generator.generateDocs("path/to/your/test/classes");
    }
}
```

### Custom Output Directory

You can specify a custom output directory for the generated documentation:

```java
import io.vinipx.testngdoc.TestNGDocGenerator;
import java.nio.file.Paths;

public class CustomDocumentationGenerator {
    public static void main(String[] args) {
        TestNGDocGenerator generator = new TestNGDocGenerator();
        
        // Set custom output directory
        generator.setOutputDir(Paths.get("/path/to/your/custom/output"));
        
        // Generate documentation
        generator.generateDocs("path/to/your/test/classes");
    }
}
```

## CI/CD Integration

### Jenkins

Add the following to your Jenkinsfile:

```groovy
stage('Generate TestNG Documentation') {
    steps {
        sh './gradlew generateTestNGDocs'
        // Or for Maven:
        // sh 'mvn exec:java -Dexec.mainClass="io.vinipx.testngdoc.SimpleTestNGDocGenerator" -Dexec.args="src/test/java"'
    }
    post {
        success {
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'testng-docs',
                reportFiles: 'index.html',
                reportName: 'TestNG Documentation'
            ])
        }
    }
}
```

### GitHub Actions

Add the following to your GitHub Actions workflow:

```yaml
- name: Generate TestNG Documentation
  run: ./gradlew generateTestNGDocs
  # Or for Maven:
  # run: mvn exec:java -Dexec.mainClass="io.vinipx.testngdoc.SimpleTestNGDocGenerator" -Dexec.args="src/test/java"

- name: Archive TestNG Documentation
  uses: actions/upload-artifact@v2
  with:
    name: testng-documentation
    path: testng-docs/
```

## Troubleshooting

### Common Issues

1. **Templates not found**: Ensure the templates directory is included in your classpath or specify the absolute path to your templates.

2. **No test classes found**: Verify the path to your test classes is correct and that they contain TestNG annotations.

3. **Custom styling not applied**: Check that your custom templates are correctly formatted and that all required CSS variables are defined.

### Getting Help

If you encounter any issues, please open an issue on the GitHub repository or contact the maintainers directly.

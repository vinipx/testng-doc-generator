package io.vinipx.sample;

import io.vinipx.testngdoc.TestNGDocGenerator;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import freemarker.template.TemplateException;
import java.nio.file.Paths;

/**
 * Sample class demonstrating how to use the TestNG Documentation Generator library
 * with all available features, generating documentation from source directories.
 */
public class GenerateDocumentationFromSource {
    
    public static void main(String[] args) {
        // Path to the test classes
        String testClassesPath = "src/test/java";
        String outputDirectory = "testng-docs";
        
        // Get current date for the report header
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        String currentDate = dateFormat.format(new Date());
        
        // Create a TestNGDocGenerator instance with all features enabled
        TestNGDocGenerator generator = new TestNGDocGenerator()
            // Basic configuration
            .setReportTitle("Sample Integration Project - TestNG Documentation")
            .setReportHeader("Generated on " + currentDate + " | Version 1.0")
            .setOutputDirectory(outputDirectory)
            
            // UI features
            .useDarkMode(false)  // Set to true for dark mode
            .displayTagsChart(false)
            
            // Pattern replacements for improved readability
            .addPatternReplacement("_", " ")
            .addPatternReplacement("whenFeatureIs", "When Feature Is ")
            .addPatternReplacement("thenFeatureIs", "Then Feature Is ")
            .addPatternReplacement("givenFeature", "Given Feature ");
        
        // Generate documentation
        System.out.println("Generating TestNG documentation from source directory: " + testClassesPath);
        try {
            // Use generateDocumentationFromSource method
            generator.generateDocumentationFromSource(testClassesPath);
            
            System.out.println("Documentation generated successfully in directory: " + outputDirectory);
        } catch (IOException | TemplateException e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Documentation generation complete!");
    }
}

package io.vinipx.sample;

import io.vinipx.testngdoc.TestNGDocGenerator;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import freemarker.template.TemplateException;

/**
 * Sample class demonstrating how to use the TestNG Documentation Generator library
 * with all available features.
 */
public class GenerateDocumentation {
    
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
            .useDarkMode(true)  // Set to true for dark mode
            .displayTagsChart(true)  // Set to false to disable tags chart
            
            // Pattern replacements for improved readability
            .addPatternReplacement("_", " ")
            .addPatternReplacement("whenFeatureIs", "When Feature Is ")
            .addPatternReplacement("thenFeatureIs", "Then Feature Is ")
            .addPatternReplacement("givenFeature", "Given Feature ")
            
            // Method filtering (optional)
            .includeMethodPattern(".*Test$")  // Only include methods ending with "Test"
            .includeTagPattern(".*");         // Include all tags
        
        // Generate documentation
        System.out.println("Generating TestNG documentation for: " + testClassesPath);
        try {
            // Ensure templates exist before generating documentation
            generator.ensureTemplateFilesExist();
            
            // Use generateDocumentationFromSourcesAndPackages to ensure all test classes are found
            generator.generateDocumentationFromSourcesAndPackages(
                new String[]{testClassesPath}, // Source directories
                new String[]{"com.example.tests"} // Packages to scan
            );
            
            System.out.println("Documentation generated successfully in directory: " + outputDirectory);
            System.out.println("Documentation generation complete!");
        } catch (IOException | TemplateException e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package io.vinipx.sample;

import io.vinipx.testngdoc.TestNGDocGenerator;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
            .setReportTitle("Sample Integration Project - TestNG Documentation")
            .setReportHeader("Generated on " + currentDate + " | Version 1.0")
            .useDarkMode(false)  // Set to true for dark mode
            .displayTagsChart(true);
        
        // Generate documentation
        System.out.println("Generating TestNG documentation for: " + testClassesPath);
        try {
            generator.generateDocumentation(testClassesPath);
            System.out.println("Documentation generated successfully in directory: " + outputDirectory);
        } catch (IOException | TemplateException e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Documentation generation complete!");
    }
}

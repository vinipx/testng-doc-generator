package com.testngdoc.sample;

import io.vinipx.testngdoc.TestNGDocGenerator;
import java.io.IOException;
import freemarker.template.TemplateException;

/**
 * Example class demonstrating how to use the setOutputDirectory method
 * to customize the output location of the generated documentation.
 */
public class CustomOutputDirectoryExample {
    
    public static void main(String[] args) {
        try {
            System.out.println("Demonstrating custom output directory usage...");
            
            // Define a custom output directory
            String customOutputDir = "custom-docs-output";
            
            // Create TestNGDocGenerator and set the custom output directory
            TestNGDocGenerator generator = new TestNGDocGenerator()
                .setOutputDirectory(customOutputDir)  // Set custom output directory
                .useDarkMode(true)                    // Enable dark mode
                .displayTagsChart()                   // Display tags chart
                .setReportTitle("Custom Output Example")
                .setReportHeader("Documentation with custom output directory");
            
            // Generate documentation
            System.out.println("Generating documentation in: " + customOutputDir);
            generator.generateDocumentationFromSource("src/test/java");
            
            System.out.println("Documentation generated successfully!");
            System.out.println("Check the '" + customOutputDir + "' directory to view the generated documentation.");
            
        } catch (IOException | TemplateException e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

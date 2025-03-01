package com.testngdoc.sample;

import io.vinipx.testngdoc.TestNGDocGenerator;
import java.io.IOException;
import freemarker.template.TemplateException;

/**
 * Test class to verify that templates are properly loaded when used as a library
 */
public class TemplateLoadingTest {
    
    public static void main(String[] args) {
        try {
            System.out.println("Testing template loading mechanism...");
            
            // Set output directory to a specific test directory
            String outputDir = "testng-docs-template-test";
            System.out.println("Generating documentation in " + outputDir + " directory");
            
            // Create TestNGDocGenerator with dark mode and custom title/header
            TestNGDocGenerator generator = new TestNGDocGenerator()
                .setOutputDirectory(outputDir)
                .useDarkMode(true)
                .displayTagsChart()
                .setReportTitle("Template Loading Test")
                .setReportHeader("Testing template loading from classpath resources");
            
            // Generate documentation
            generator.generateDocumentationFromSource("src/test/java");
            
            System.out.println("Documentation generated successfully!");
            System.out.println("Please check the " + outputDir + " directory to verify that:");
            System.out.println("1. Dark mode is properly applied");
            System.out.println("2. Custom title and header are displayed");
            System.out.println("3. Tags are properly displayed in the charts");
            
        } catch (IOException | TemplateException e) {
            System.err.println("Error testing template loading: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package com.testngdoc.sample;

import io.vinipx.testngdoc.TestNGDocGenerator;
import java.io.IOException;
import freemarker.template.TemplateException;

/**
 * Test class to verify the styling of the documentation generator
 * with different configurations for dark mode and headers.
 */
public class StyleTestGenerator {
    
    public static void main(String[] args) {
        try {
            // Test 1: Light mode with standard headers
            generateDocumentation(
                "testng-docs-light", 
                false, 
                "TestNG Documentation - Light Mode", 
                "Standard header in light mode"
            );
            
            // Test 2: Dark mode with standard headers
            generateDocumentation(
                "testng-docs-dark", 
                true, 
                "TestNG Documentation - Dark Mode", 
                "Standard header in dark mode"
            );
            
            // Test 3: Light mode with long header
            generateDocumentation(
                "testng-docs-light-long-header", 
                false, 
                "TestNG Documentation - Light Mode", 
                "This is a much longer header text to test how the styling handles longer text in the header section. " +
                "It should be displayed with proper styling, smaller font size, and italic formatting."
            );
            
            // Test 4: Dark mode with long header
            generateDocumentation(
                "testng-docs-dark-long-header", 
                true, 
                "TestNG Documentation - Dark Mode", 
                "This is a much longer header text to test how the styling handles longer text in the header section. " +
                "It should be displayed with proper styling, smaller font size, and italic formatting."
            );
            
            System.out.println("All documentation variants generated successfully!");
            System.out.println("Check the following directories:");
            System.out.println("- testng-docs-light");
            System.out.println("- testng-docs-dark");
            System.out.println("- testng-docs-light-long-header");
            System.out.println("- testng-docs-dark-long-header");
            
        } catch (Exception e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to generate documentation with specific settings
     */
    private static void generateDocumentation(
            String outputDir, 
            boolean darkMode, 
            String title, 
            String header) throws IOException, TemplateException {
        
        // Create TestNGDocGenerator with specified settings
        TestNGDocGenerator generator = new TestNGDocGenerator();
        
        // Set output directory by reflection (since it's a private static field)
        try {
            java.lang.reflect.Field outputDirField = TestNGDocGenerator.class.getDeclaredField("OUTPUT_DIR");
            outputDirField.setAccessible(true);
            outputDirField.set(null, outputDir);
        } catch (Exception e) {
            System.err.println("Could not set output directory: " + e.getMessage());
        }
        
        // Configure generator
        generator.useDarkMode(darkMode)
                .displayTagsChart()
                .setReportTitle(title)
                .setReportHeader(header);
        
        // Generate documentation
        System.out.println("Generating documentation with settings:");
        System.out.println("- Output directory: " + outputDir);
        System.out.println("- Dark mode: " + darkMode);
        System.out.println("- Title: " + title);
        System.out.println("- Header: " + header);
        
        generator.generateDocumentationFromSource("src/test/java");
        
        System.out.println("Documentation generated in: " + outputDir);
        System.out.println();
    }
}

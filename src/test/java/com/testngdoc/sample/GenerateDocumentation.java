package com.testngdoc.sample;

import io.vinipx.testngdoc.TestNGDocGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple class to generate documentation for test classes
 */
public class GenerateDocumentation {
    
    public static void main(String[] args) {
        // Initialize the TestNG Documentation Generator
        TestNGDocGenerator generator = new TestNGDocGenerator();
        
        // Define pattern replacements for technical terms
        Map<String, String> patternReplacements = new HashMap<>();
        patternReplacements.put("API", "Application Programming Interface");
        patternReplacements.put("REST", "Representational State Transfer");
        patternReplacements.put("JSON", "JavaScript Object Notation");
        patternReplacements.put("OAuth", "Open Authorization");
        patternReplacements.put("HTTP", "Hypertext Transfer Protocol");
        patternReplacements.put("UI", "User Interface");
        patternReplacements.put("DB", "Database");
        
        // Set the pattern replacements
        for (Map.Entry<String, String> entry : patternReplacements.entrySet()) {
            generator.addPatternReplacement(entry.getKey(), entry.getValue());
        }
        
        // Enable tags chart on the index page
        generator.displayTagsChart();
        
        // Enable dark mode for the documentation
        generator.useDarkMode();
        
        // Set custom report title and header
        generator.setReportTitle("My Test Suite Documentation");
        generator.setReportHeader("Generated on " + new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        
        // Generate documentation for test classes
        // The @Docs annotation can be used to add tags to test methods
        // These tags will appear in the documentation and help categorize tests
        try {
            generator.generateDocumentation(new Class<?>[] {
                LoginTests.class,
                GherkinStyleTests.class,
                TCPrefixTests.class,
                UnderscoreTests.class,
                UserProfileTests.class,
                APITests.class,
                TaggedTests.class  // Added the TaggedTests class to showcase the @Docs annotation
            }, "testng-docs");
            System.out.println("Documentation generated in: testng-docs");
            System.out.println("Documentation generated successfully!");
        } catch (Exception e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

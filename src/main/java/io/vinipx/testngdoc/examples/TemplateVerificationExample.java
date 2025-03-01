package io.vinipx.testngdoc.examples;

import io.vinipx.testngdoc.TestNGDocGenerator;

/**
 * Example class demonstrating how to verify and synchronize templates when using the TestNG Documentation Generator library.
 */
public class TemplateVerificationExample {

    public static void main(String[] args) {
        // Define project directory (typically the root of your project)
        String projectDir = args.length > 0 ? args[0] : ".";
        
        // Create a TestNGDocGenerator instance
        TestNGDocGenerator generator = new TestNGDocGenerator();
        
        // Check if templates are synchronized
        boolean templatesInSync = generator.areTemplatesSynchronized(projectDir);
        
        if (!templatesInSync) {
            System.out.println("Templates are not synchronized with the library. Synchronizing now...");
            
            // Synchronize templates
            generator.synchronizeTemplates(projectDir);
            
            System.out.println("Templates have been synchronized. You can customize them in the templates directory.");
        } else {
            System.out.println("Templates are already synchronized with the library.");
        }
        
        // Now you can proceed with generating documentation
        System.out.println("Generating documentation...");
        
        // Example: Generate documentation for a package
        try {
            generator
                .setReportTitle("My TestNG Documentation")
                .useDarkMode(true)
                .displayTagsChart(true)
                .generateDocumentation("com.example.tests");
                
            System.out.println("Documentation generated successfully!");
        } catch (Exception e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

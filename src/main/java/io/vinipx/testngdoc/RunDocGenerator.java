package io.vinipx.testngdoc;

/**
 * Simple class to run the TestNGDocGenerator
 */
public class RunDocGenerator {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar testng-doc-generator.jar <source-directory>");
            System.exit(1);
        }
        
        String sourceDirectory = args[0];
        
        try {
            TestNGDocGenerator generator = new TestNGDocGenerator();
            
            // Generate documentation from source directory
            System.out.println("Generating documentation from: " + sourceDirectory);
            generator.generateDocumentationFromSource(sourceDirectory);
            
            System.out.println("Documentation generated successfully!");
        } catch (Exception e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

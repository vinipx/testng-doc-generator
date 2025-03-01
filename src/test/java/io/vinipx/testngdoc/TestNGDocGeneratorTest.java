package io.vinipx.testngdoc;

import com.testngdoc.sample.GherkinStyleTests;
import com.testngdoc.sample.TCPrefixTests;
import com.testngdoc.sample.UnderscoreTests;
import org.junit.Test;

/**
 * Test class to demonstrate how the TestNGDocGenerator library would be used as a dependency
 */
public class TestNGDocGeneratorTest {
    
    @Test
    public void testGenerateDocumentation() {
        try {
            // Create an instance of the TestNGDocGenerator
            TestNGDocGenerator generator = new TestNGDocGenerator();
            
            // Generate documentation for specific test classes
            System.out.println("Generating documentation for test classes...");
            generator.generateDocumentation(
                new Class<?>[] {
                    TCPrefixTests.class,
                    UnderscoreTests.class,
                    GherkinStyleTests.class
                }, 
                "testng-docs"
            );
            
            System.out.println("Documentation generated successfully in the 'testng-docs' directory!");
        } catch (Exception e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Documentation generation failed", e);
        }
    }
    
    // This main method allows running the test directly
    public static void main(String[] args) {
        new TestNGDocGeneratorTest().testGenerateDocumentation();
    }
}

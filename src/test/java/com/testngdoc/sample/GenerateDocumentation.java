package com.testngdoc.sample;

import io.vinipx.testngdoc.TestNGDocGenerator;

/**
 * Simple class to generate documentation for test classes
 */
public class GenerateDocumentation {
    
    public static void main(String[] args) {
        TestNGDocGenerator generator = new TestNGDocGenerator();
        
        // Generate documentation for the TCPrefixTests class
        generator.generateDocumentation(TCPrefixTests.class, "testng-docs");
        
        System.out.println("Documentation generated successfully!");
    }
}

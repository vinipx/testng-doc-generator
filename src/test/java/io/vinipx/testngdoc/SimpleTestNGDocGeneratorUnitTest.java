package io.vinipx.testngdoc;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the SimpleTestNGDocGenerator class
 * Focuses on testing the method name parsing and description generation functionality
 */
public class SimpleTestNGDocGeneratorUnitTest {
    
    private SimpleTestNGDocGenerator generator;
    
    @Before
    public void setUp() {
        generator = new SimpleTestNGDocGenerator();
    }
    
    /**
     * Test the generateHumanReadableExplanation method for camelCase method names
     */
    @Test
    public void testGenerateHumanReadableExplanationForCamelCase() {
        // Test with a simple camelCase method name
        String result = generator.generateHumanReadableExplanation(
            "// Test method\nAssert.assertTrue(true);", "testVerifyLogin");
        
        // Verify the result
        assertTrue("Should contain the method name without 'test'", 
            result.contains("verify login"));
        assertFalse("Should not contain camelCase", 
            result.contains("verifyLogin"));
    }
    
    /**
     * Test the generateHumanReadableExplanation method for underscore method names
     */
    @Test
    public void testGenerateHumanReadableExplanationForUnderscores() {
        // Test with an underscore-separated method name
        String result = generator.generateHumanReadableExplanation(
            "// Test method\nAssert.assertTrue(true);", "test_verify_login");
        
        // Verify the result
        assertTrue("Should contain spaces instead of underscores", 
            result.contains("verify login"));
        assertFalse("Should not contain underscores", 
            result.contains("verify_login"));
    }
    
    /**
     * Test the generateHumanReadableExplanation method for TC-prefixed method names
     */
    @Test
    public void testGenerateHumanReadableExplanationForTCPrefix() {
        // Test with a TC-prefixed method name
        String result = generator.generateHumanReadableExplanation(
            "// Test method\nAssert.assertTrue(true);", "TC01_verifyLogin");
        
        // Verify the result
        assertTrue("Should extract the test case ID", 
            result.contains("TC01"));
        assertTrue("Should contain the method name without the TC prefix", 
            result.contains("verify login"));
    }
    
    /**
     * Test the generateHumanReadableExplanation method for Gherkin-style method names
     */
    @Test
    public void testGenerateHumanReadableExplanationForGherkinStyle() {
        // Test with a Gherkin-style method name
        String result = generator.generateHumanReadableExplanation(
            "// Test method\nAssert.assertTrue(true);", 
            "givenValidCredentials_whenUserLogsIn_thenLoginSucceedsTest");
        
        // Verify the result
        assertTrue("Should contain the original method name", 
            result.contains("Method: givenValidCredentials_whenUserLogsIn_thenLoginSucceedsTest"));
        assertTrue("Should format with Given", 
            result.contains("Given "));
        assertTrue("Should format with When", 
            result.contains("When "));
        assertTrue("Should format with Then", 
            result.contains("Then "));
    }
    
    /**
     * Test the generateHumanReadableExplanation method for TC-prefixed Gherkin-style method names
     */
    @Test
    public void testGenerateHumanReadableExplanationForTCPrefixedGherkinStyle() {
        // Test with a TC-prefixed Gherkin-style method name
        String result = generator.generateHumanReadableExplanation(
            "// Test method\nAssert.assertTrue(true);", 
            "TC01_givenValidCredentials_whenUserLogsIn_thenLoginSucceedsTest");
        
        // Verify the result
        assertTrue("Should contain the original method name", 
            result.contains("Method: TC01_givenValidCredentials_whenUserLogsIn_thenLoginSucceedsTest"));
        assertTrue("Should extract the test case ID", 
            result.contains("TC01"));
        assertTrue("Should format with Given", 
            result.contains("Given "));
        assertTrue("Should format with When", 
            result.contains("When "));
        assertTrue("Should format with Then", 
            result.contains("Then "));
    }
}

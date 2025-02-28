package com.example.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Sample test class demonstrating integration with TestNG Documentation Generator
 */
public class SampleIntegrationTest {
    
    private boolean featureEnabled;
    
    @BeforeMethod
    public void setup() {
        featureEnabled = true;
    }
    
    @Test
    public void givenFeatureEnabled_whenFeatureIsAccessed_thenFeatureWorksCorrectlyTest() {
        // This test demonstrates a Gherkin-style test method name
        // The documentation generator will format this nicely
        
        // Given feature is enabled (setup in @BeforeMethod)
        
        // When feature is accessed
        boolean featureAccessible = checkFeatureAccessibility();
        
        // Then feature works correctly
        Assert.assertTrue(featureAccessible, "Feature should be accessible when enabled");
    }
    
    @Test
    public void whenFeatureIsDisabled_thenFeatureIsNotAccessibleTest() {
        // Disable the feature
        featureEnabled = false;
        
        // When feature is accessed
        boolean featureAccessible = checkFeatureAccessibility();
        
        // Then feature is not accessible
        Assert.assertFalse(featureAccessible, "Feature should not be accessible when disabled");
    }
    
    private boolean checkFeatureAccessibility() {
        return featureEnabled;
    }
}

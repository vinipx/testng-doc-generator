package com.example.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.vinipx.testngdoc.annotations.Docs;

/**
 * Sample test class demonstrating integration with TestNG Documentation Generator
 * This class tests a feature toggle system with various scenarios
 */
public class SampleIntegrationTest {
    
    private boolean featureEnabled;
    private String featureName;
    private int accessCount;
    
    @BeforeMethod
    public void setup() {
        featureEnabled = true;
        featureName = "Premium Content";
        accessCount = 0;
    }
    
    @Test
    @Docs(tags = {"feature:toggle", "positive:test", "gherkin:style"})
    public void givenFeatureEnabled_whenFeatureIsAccessed_thenFeatureWorksCorrectlyTest() {
        // This test demonstrates a Gherkin-style test method name
        // The documentation generator will format this nicely
        
        // Given feature is enabled (setup in @BeforeMethod)
        
        // When feature is accessed
        boolean featureAccessible = checkFeatureAccessibility();
        
        // Then feature works correctly
        Assert.assertTrue(featureAccessible, "Feature should be accessible when enabled");
        Assert.assertEquals(accessCount, 1, "Access count should be incremented");
    }
    
    @Test
    @Docs(tags = {"feature:toggle", "negative-test"})
    public void whenFeatureIsDisabled_thenFeatureIsNotAccessibleTest() {
        // Disable the feature
        featureEnabled = false;
        
        // When feature is accessed
        boolean featureAccessible = checkFeatureAccessibility();
        
        // Then feature is not accessible
        Assert.assertFalse(featureAccessible, "Feature should not be accessible when disabled");
    }
    
    @Test
    @Docs(tags = {"feature-name", "validation"})
    public void testFeatureNameValidation() {
        // Set an invalid feature name
        featureName = null;
        
        // Validate feature name
        boolean isValid = validateFeatureName();
        
        // Verify validation works
        Assert.assertFalse(isValid, "Null feature name should be invalid");
        
        // Set a valid feature name
        featureName = "Premium Content";
        
        // Validate again
        isValid = validateFeatureName();
        
        // Verify validation works
        Assert.assertTrue(isValid, "Valid feature name should pass validation");
    }
    
    @Test
    @Docs(tags = {"access-tracking", "counter"})
    public void testAccessCountIncrementing() {
        // Initial count should be 0
        Assert.assertEquals(accessCount, 0, "Initial access count should be 0");
        
        // Access feature multiple times
        checkFeatureAccessibility();
        checkFeatureAccessibility();
        checkFeatureAccessibility();
        
        // Verify count is incremented correctly
        Assert.assertEquals(accessCount, 3, "Access count should be incremented to 3");
    }
    
    private boolean checkFeatureAccessibility() {
        // Increment access count
        accessCount++;
        return featureEnabled;
    }
    
    private boolean validateFeatureName() {
        return featureName != null && !featureName.isEmpty();
    }
}

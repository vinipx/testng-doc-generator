package com.example.tests;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import io.vinipx.testngdoc.annotations.Docs;

/**
 * Feature Test Suite that demonstrates various test scenarios
 * This class shows how to use TestNG's test suite features
 */
@Test
public class FeatureTestSuite {
    
    private boolean setupComplete = false;
    
    @BeforeClass
    public void setupTestSuite() {
        setupComplete = true;
        System.out.println("Test suite setup complete");
    }
    
    @Test
    @Docs(tags = {"suite", "setup", "validation"})
    public void testSuiteSetup() {
        // Verify that the test suite setup was completed successfully
        assert setupComplete : "Test suite setup should be complete";
    }
    
    @Test(groups = {"feature", "basic"})
    @Docs(tags = {"feature", "basic", "validation"})
    public void testBasicFeature() {
        // Test a basic feature
        assert true : "Basic feature should work";
    }
    
    @Test(groups = {"feature", "advanced"})
    @Docs(tags = {"feature", "advanced", "validation"})
    public void testAdvancedFeature() {
        // Test an advanced feature
        assert true : "Advanced feature should work";
    }
    
    @Test(dependsOnMethods = {"testBasicFeature"})
    @Docs(tags = {"dependency", "feature"})
    public void testFeatureDependency() {
        // Test that depends on another test
        assert true : "Feature dependency should work";
    }
    
    @AfterClass
    public void teardownTestSuite() {
        setupComplete = false;
        System.out.println("Test suite teardown complete");
    }
}

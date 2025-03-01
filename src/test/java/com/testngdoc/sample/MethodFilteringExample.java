package com.testngdoc.sample;

import io.vinipx.testngdoc.TestNGDocGenerator;
import io.vinipx.testngdoc.annotations.Docs;
import org.testng.annotations.Test;

/**
 * Example class to demonstrate the method filtering feature.
 */
public class MethodFilteringExample {

    public static void main(String[] args) {
        // Create a new generator instance
        TestNGDocGenerator generator = new TestNGDocGenerator();
        
        // Set custom output directory
        generator.setOutputDirectory("filtered-docs");
        
        // Enable dark mode and tag chart
        generator.useDarkMode()
                 .displayTagsChart();
        
        // Set custom title and header
        generator.setReportTitle("Filtered TestNG Documentation");
        generator.setReportHeader("Example of method filtering feature");
        
        // Include only methods that contain "should" in their name
        generator.includeMethodPattern(".*should.*");
        
        // Exclude methods with the "Slow" tag
        generator.excludeTagPattern("Slow");
        
        // Generate documentation
        try {
            generator.generateDocumentationFromSource("src/test/java/com/testngdoc/sample");
            System.out.println("Documentation with filtered methods generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @Docs(tags = {"Feature: Login", "Priority: High"})
    public void testLoginWithValidCredentials() {
        // Test implementation
        System.out.println("Testing login with valid credentials");
    }
    
    @Test
    @Docs(tags = {"Feature: Login", "Priority: Medium", "Slow"})
    public void userShouldBeAbleToLogin() {
        // Test implementation
        System.out.println("Testing that user should be able to login");
    }
    
    @Test
    @Docs(tags = {"Feature: Registration", "Priority: High"})
    public void userShouldBeAbleToRegister() {
        // Test implementation
        System.out.println("Testing that user should be able to register");
    }
    
    @Test
    @Docs(tags = {"Feature: Logout", "Priority: Low"})
    public void testLogout() {
        // Test implementation
        System.out.println("Testing logout functionality");
    }
    
    @Test
    @Docs(tags = {"Feature: Password Reset", "Priority: Medium"})
    public void shouldResetPasswordWhenRequested() {
        // Test implementation
        System.out.println("Testing password reset functionality");
    }
    
    @Test
    @Docs(tags = {"Feature: Profile", "Priority: Low", "Slow"})
    public void shouldUpdateUserProfile() {
        // Test implementation
        System.out.println("Testing user profile update");
    }
}

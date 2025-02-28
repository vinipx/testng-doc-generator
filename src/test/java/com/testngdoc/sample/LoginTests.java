package com.testngdoc.sample;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test class for login functionality
 */
public class LoginTests {
    
    private MockUserService userService;
    
    @BeforeMethod
    public void setup() {
        userService = new MockUserService();
    }
    
    @Test
    public void testSuccessfulLogin() {
        // Test a successful login with valid credentials
        String username = "validUser";
        String password = "validPassword";
        
        boolean loginResult = userService.login(username, password);
        
        Assert.assertTrue(loginResult, "Login should be successful with valid credentials");
    }
    
    @Test
    public void testFailedLoginWithInvalidPassword() {
        // Test login failure with invalid password
        String username = "validUser";
        String password = "wrongPassword";
        
        boolean loginResult = userService.login(username, password);
        
        Assert.assertFalse(loginResult, "Login should fail with invalid password");
    }
    
    @Test
    public void testFailedLoginWithInvalidUsername() {
        // Test login failure with invalid username
        String username = "nonExistentUser";
        String password = "anyPassword";
        
        boolean loginResult = userService.login(username, password);
        
        Assert.assertFalse(loginResult, "Login should fail with invalid username");
    }
    
    @Test
    public void testLoginWithEmptyCredentials() {
        // Test login with empty credentials
        String username = "";
        String password = "";
        
        boolean loginResult = userService.login(username, password);
        
        Assert.assertFalse(loginResult, "Login should fail with empty credentials");
    }
    
    // Mock class for demonstration
    private static class MockUserService {
        public boolean login(String username, String password) {
            return "validUser".equals(username) && "validPassword".equals(password);
        }
    }
}

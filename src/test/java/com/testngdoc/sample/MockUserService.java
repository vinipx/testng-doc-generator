package com.testngdoc.sample;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of a user service for testing purposes
 */
public class MockUserService {
    private final Map<String, String> validUsers;
    
    public MockUserService() {
        validUsers = new HashMap<>();
        validUsers.put("validUser", "validPassword");
        validUsers.put("testUser", "testPassword");
    }
    
    /**
     * Attempts to log in a user with the provided credentials
     * 
     * @param username the username
     * @param password the password
     * @return true if login is successful, false otherwise
     */
    public boolean login(String username, String password) {
        String storedPassword = validUsers.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }
    
    /**
     * Logs out a user
     * 
     * @param username the username to log out
     * @return true if logout is successful
     */
    public boolean logout(String username) {
        return validUsers.containsKey(username);
    }
}

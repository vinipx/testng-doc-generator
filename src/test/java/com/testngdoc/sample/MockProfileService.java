package com.testngdoc.sample;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of a profile service for testing purposes
 */
public class MockProfileService {
    private final Map<String, Map<String, String>> userProfiles;
    
    public MockProfileService() {
        userProfiles = new HashMap<>();
        
        // Initialize with some test data
        Map<String, String> user1Profile = new HashMap<>();
        user1Profile.put("name", "Test User");
        user1Profile.put("email", "test@example.com");
        user1Profile.put("status", "active");
        
        Map<String, String> user2Profile = new HashMap<>();
        user2Profile.put("name", "Another User");
        user2Profile.put("email", "another@example.com");
        user2Profile.put("status", "inactive");
        
        userProfiles.put("user123", user1Profile);
        userProfiles.put("user456", user2Profile);
    }
    
    /**
     * Gets a user profile by ID
     * 
     * @param userId the user ID
     * @return the user profile or null if not found
     */
    public Map<String, String> getUserProfile(String userId) {
        return userProfiles.get(userId);
    }
    
    /**
     * Updates a user profile
     * 
     * @param userId the user ID
     * @param updatedProfile the updated profile data
     * @return true if update is successful
     */
    public boolean updateUserProfile(String userId, Map<String, String> updatedProfile) {
        if (!userProfiles.containsKey(userId)) {
            return false;
        }
        
        Map<String, String> existingProfile = userProfiles.get(userId);
        existingProfile.putAll(updatedProfile);
        return true;
    }
}

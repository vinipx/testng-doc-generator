package com.testngdoc.sample;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for user profile functionality
 */
public class UserProfileTests {
    
    private MockProfileService profileService;
    
    @BeforeMethod
    public void setup() {
        profileService = new MockProfileService();
    }
    
    @Test
    public void testGetUserProfile() {
        // Test retrieving a user profile
        String userId = "user123";
        Map<String, String> profile = profileService.getUserProfile(userId);
        
        Assert.assertNotNull(profile, "Profile should not be null");
        Assert.assertEquals(profile.get("name"), "Test User", "Profile name should match");
        Assert.assertEquals(profile.get("email"), "test@example.com", "Profile email should match");
    }
    
    @Test
    public void testUpdateUserProfile() {
        // Test updating a user profile
        String userId = "user123";
        Map<String, String> updatedProfile = new HashMap<>();
        updatedProfile.put("name", "Updated Name");
        updatedProfile.put("email", "updated@example.com");
        
        boolean updateResult = profileService.updateUserProfile(userId, updatedProfile);
        
        Assert.assertTrue(updateResult, "Profile update should be successful");
        
        // Verify the update
        Map<String, String> retrievedProfile = profileService.getUserProfile(userId);
        Assert.assertEquals(retrievedProfile.get("name"), "Updated Name", "Updated name should match");
        Assert.assertEquals(retrievedProfile.get("email"), "updated@example.com", "Updated email should match");
    }
    
    @Test
    public void testGetNonExistentUserProfile() {
        // Test retrieving a non-existent user profile
        String userId = "nonExistentUser";
        Map<String, String> profile = profileService.getUserProfile(userId);
        
        Assert.assertNull(profile, "Profile should be null for non-existent user");
    }
    
    // Mock class for demonstration
    private static class MockProfileService {
        private final Map<String, Map<String, String>> profiles = new HashMap<>();
        
        public MockProfileService() {
            // Initialize with a test profile
            Map<String, String> testProfile = new HashMap<>();
            testProfile.put("name", "Test User");
            testProfile.put("email", "test@example.com");
            profiles.put("user123", testProfile);
        }
        
        public Map<String, String> getUserProfile(String userId) {
            return profiles.get(userId);
        }
        
        public boolean updateUserProfile(String userId, Map<String, String> profile) {
            if (profiles.containsKey(userId)) {
                profiles.put(userId, profile);
                return true;
            }
            return false;
        }
    }
}

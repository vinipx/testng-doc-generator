package com.testngdoc.sample;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class demonstrating test methods with underscores in Gherkin-style names
 */
public class UnderscoreTests {

    @Test
    public void given_User_Is_Logged_In_when_Accessing_Profile_then_Details_Are_VisibleTest() {
        // Given user is logged in
        boolean userLoggedIn = true;
        
        // When accessing profile
        boolean accessingProfile = true;
        
        // Then details are visible
        boolean detailsVisible = true;
        
        Assert.assertTrue(userLoggedIn && accessingProfile && detailsVisible, 
                "User is logged in, accessing profile, and details are visible");
    }
    
    @Test
    public void TC01_given_Network_Connection_when_Downloading_File_then_Progress_Is_ShownTest() {
        // Given network connection
        boolean networkConnected = true;
        
        // When downloading file
        boolean downloadingFile = true;
        
        // Then progress is shown
        boolean progressShown = true;
        
        Assert.assertTrue(networkConnected && downloadingFile && progressShown, 
                "Network is connected, downloading file, and progress is shown");
    }
    
    @Test
    public void simple_Test_With_Underscores() {
        // This is a simple test with underscores but not in Gherkin style
        boolean testPassed = true;
        Assert.assertTrue(testPassed, "Test should pass");
    }
}
